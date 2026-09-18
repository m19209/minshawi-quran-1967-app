package com.minshawi.quran1967.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.minshawi.quran1967.data.QuranRepository
import com.minshawi.quran1967.data.Surah
import com.minshawi.quran1967.util.DownloadHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RepeatMode {
    OFF, ALL, ONE
}

/**
 * Central singleton managing Quran playback, audio focus, offline playback,
 * error auto-recovery, and the intelligent Azan interruption & auto-resumption mechanism.
 */
object AudioPlaybackManager {

    private var exoPlayer: ExoPlayer? = null
    private var appContext: Context? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTrackerJob: Job? = null
    private var isUsingFallback: Boolean = false

    // Playback States
    private val _currentSurah = MutableStateFlow<Surah?>(QuranRepository.getSurah(1))
    val currentSurah: StateFlow<Surah?> = _currentSurah.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    // Smart Azan Interruption State Machine
    private val _isAzanActive = MutableStateFlow(false)
    val isAzanActive: StateFlow<Boolean> = _isAzanActive.asStateFlow()

    private val _activeAzanPrayerName = MutableStateFlow<String?>(null)
    val activeAzanPrayerName: StateFlow<String?> = _activeAzanPrayerName.asStateFlow()

    private var wasPlayingBeforeAzan: Boolean = false
    private var savedPositionBeforeAzan: Long = 0L
    private var savedSurahBeforeAzan: Surah? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (exoPlayer != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        // Resilient HTTP Data Source with 30-second timeouts and redirect following
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) MinshawiQuran1967App")
            .setConnectTimeoutMs(30000)
            .setReadTimeoutMs(30000)
            .setAllowCrossProtocolRedirects(true)

        val mediaSourceFactory = DefaultMediaSourceFactory(context.applicationContext)
            .setDataSourceFactory(DefaultDataSource.Factory(context.applicationContext, httpDataSourceFactory))

        exoPlayer = ExoPlayer.Builder(context.applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                        if (playing) {
                            startProgressTracker()
                        } else {
                            stopProgressTracker()
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> _isLoading.value = true
                            Player.STATE_READY -> {
                                _isLoading.value = false
                                _duration.value = duration.coerceAtLeast(0L)
                            }
                            Player.STATE_ENDED -> {
                                _isLoading.value = false
                                handleTrackEnded()
                            }
                            Player.STATE_IDLE -> {
                                _isLoading.value = false
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _isLoading.value = false
                        val surah = _currentSurah.value ?: return
                        val failedPos = _currentPosition.value

                        // If primary stream failed, seamlessly switch to high-speed CDN mirror
                        if (!isUsingFallback && surah.fallbackAudioUrl.isNotEmpty()) {
                            isUsingFallback = true
                            scope.launch {
                                delay(300)
                                playWithUrl(surah, surah.fallbackAudioUrl, failedPos)
                            }
                        } else {
                            // Retry current track from the saved position
                            scope.launch {
                                delay(1200)
                                if (_currentSurah.value?.number == surah.number) {
                                    playSurah(surah, startPositionMs = failedPos)
                                }
                            }
                        }
                    }
                })
            }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    /**
     * Plays a Surah with automatic local offline detection and seamless position resumption.
     */
    fun playSurah(surah: Surah, startPositionMs: Long = -1L) {
        val player = exoPlayer ?: return
        val context = appContext

        // Check if the Surah is downloaded locally on device
        val localFile = if (context != null) DownloadHelper.getLocalSurahFile(context, surah) else null
        val isOffline = localFile != null && localFile.exists() && localFile.length() > 50_000L

        val isSameSurah = _currentSurah.value?.number == surah.number
        _currentSurah.value = surah

        // Target playback position: keep current position if same Surah and not explicitly set
        val targetPosition = when {
            startPositionMs >= 0L -> startPositionMs
            isSameSurah && _currentPosition.value > 0L -> _currentPosition.value
            else -> 0L
        }

        val mediaUri = if (isOffline) {
            isUsingFallback = false
            Uri.fromFile(localFile)
        } else {
            isUsingFallback = false
            Uri.parse(surah.audioUrl1967)
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(surah.arabicName)
            .setArtist("الشيخ محمد صديق المنشاوي")
            .setAlbumTitle(if (isOffline) "الختمة المرتلة 1967 (محفوظة أوفلاين)" else "الختمة المرتلة 1967 النقية")
            .setDisplayTitle("سورة ${surah.arabicName}")
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        if (targetPosition > 0L) {
            player.seekTo(targetPosition)
        }
        player.play()
    }

    /**
     * Plays a Surah with an explicit stream URL (e.g. CDN fallback) from a given timestamp.
     */
    fun playWithUrl(surah: Surah, url: String, startPositionMs: Long = 0L) {
        val player = exoPlayer ?: return
        _currentSurah.value = surah

        val metadata = MediaMetadata.Builder()
            .setTitle(surah.arabicName)
            .setArtist("الشيخ محمد صديق المنشاوي")
            .setAlbumTitle("الختمة المرتلة 1967 (خادم بديل سريع)")
            .setDisplayTitle("سورة ${surah.arabicName}")
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        if (startPositionMs > 0L) {
            player.seekTo(startPositionMs)
        }
        player.play()
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            val surah = _currentSurah.value ?: return
            when (player.playbackState) {
                Player.STATE_IDLE -> {
                    // Always resume from the exact saved position!
                    playSurah(surah, startPositionMs = _currentPosition.value)
                }
                Player.STATE_ENDED -> {
                    // Track finished naturally, restart from beginning
                    playSurah(surah, startPositionMs = 0L)
                }
                else -> {
                    player.play()
                }
            }
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun seekForward(ms: Long = 10000L) {
        val current = _currentPosition.value
        val total = _duration.value
        seekTo((current + ms).coerceAtMost(total))
    }

    fun seekBackward(ms: Long = 10000L) {
        val current = _currentPosition.value
        seekTo((current - ms).coerceAtLeast(0L))
    }

    fun playNext() {
        val current = _currentSurah.value ?: return
        val nextNumber = if (current.number < 114) current.number + 1 else 1
        QuranRepository.getSurah(nextNumber)?.let { playSurah(it, 0L) }
    }

    fun playPrevious() {
        val current = _currentSurah.value ?: return
        val prevNumber = if (current.number > 1) current.number - 1 else 114
        QuranRepository.getSurah(prevNumber)?.let { playSurah(it, 0L) }
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    private fun handleTrackEnded() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                _currentSurah.value?.let { playSurah(it, 0L) }
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                playNext()
            }
        }
    }

    // =========================================================================
    // SMART AZAN INTERRUPTION & AUTO-RESUMPTION SYSTEM
    // =========================================================================

    /**
     * Called when Azan time arrives:
     * 1. Detects if Quran is currently playing.
     * 2. If playing, freezes position and saves the Surah.
     * 3. Pauses Quran immediately so Azan can sound.
     * 4. Updates state for UI display.
     */
    fun onAzanStarted(prayerName: String) {
        val player = exoPlayer
        wasPlayingBeforeAzan = player?.isPlaying == true

        if (wasPlayingBeforeAzan) {
            savedPositionBeforeAzan = player?.currentPosition ?: 0L
            savedSurahBeforeAzan = _currentSurah.value
            player?.pause()
        }

        _isAzanActive.value = true
        _activeAzanPrayerName.value = prayerName
    }

    /**
     * Called when Azan audio finishes:
     * Checks if Quran was playing prior to Azan, and automatically resumes
     * playback from the exact saved millisecond!
     */
    fun onAzanCompleted() {
        _isAzanActive.value = false
        _activeAzanPrayerName.value = null

        if (wasPlayingBeforeAzan && savedSurahBeforeAzan != null) {
            val surahToResume = savedSurahBeforeAzan!!
            val positionToResume = savedPositionBeforeAzan
            wasPlayingBeforeAzan = false
            savedSurahBeforeAzan = null

            playSurah(surahToResume, positionToResume)
        }
    }

    /**
     * Allows user to stop the Azan early from the UI/notification
     * and resume the Quran recitation immediately.
     */
    fun stopAzanAndResumeQuran() {
        onAzanCompleted()
    }

    // =========================================================================
    // PROGRESS TRACKER
    // =========================================================================

    private fun startProgressTracker() {
        stopProgressTracker()
        progressTrackerJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPosition.value = player.currentPosition
                        _duration.value = player.duration.coerceAtLeast(0L)
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }
}
