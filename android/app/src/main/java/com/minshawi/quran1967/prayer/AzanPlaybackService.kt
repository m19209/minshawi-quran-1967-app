package com.minshawi.quran1967.prayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.minshawi.quran1967.MainActivity
import com.minshawi.quran1967.MinshawiApp
import com.minshawi.quran1967.R
import com.minshawi.quran1967.audio.AudioPlaybackManager

class AzanPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var prayerName: String = "الصلاة"

    companion object {
        const val ACTION_STOP_AZAN = "com.minshawi.quran1967.ACTION_STOP_AZAN"
        private const val NOTIFICATION_ID = 2001

        // Default Azan stream (Sheikh Minshawi's pristine Azan recording)
        val MINSHAWI_AZAN_URL = "https://archive.org/download/90---azan---90---azan--many----sound----mp3---alazan/048-.mp3"
        val MAKKAH_AZAN_URL = "https://archive.org/download/90---azan---90---azan--many----sound----mp3---alazan/019--1.mp3"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_AZAN) {
            stopAzanAndResume()
            return START_NOT_STICKY
        }

        prayerName = intent?.getStringExtra(AzanScheduler.EXTRA_PRAYER_NAME) ?: "الصلاة"

        // 1. Notify AudioPlaybackManager to pause Quran recitation and record playback position
        AudioPlaybackManager.onAzanStarted(prayerName)

        // 2. Start Foreground Notification
        startForeground(NOTIFICATION_ID, buildAzanNotification())

        // 3. Play Azan Audio
        playAzanAudio()

        return START_NOT_STICKY
    }

    private fun playAzanAudio() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(this@AzanPlaybackService, Uri.parse(MINSHAWI_AZAN_URL))
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    // 4. Azan audio completed -> automatically resume Quran recitation!
                    onAzanFinished()
                }
                setOnErrorListener { _, _, _ ->
                    onAzanFinished()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onAzanFinished()
        }
    }

    private fun onAzanFinished() {
        mediaPlayer?.release()
        mediaPlayer = null

        // Trigger automatic resumption of Quran recitation
        AudioPlaybackManager.onAzanCompleted()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopAzanAndResume() {
        onAzanFinished()
    }

    private fun buildAzanNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AzanPlaybackService::class.java).apply {
            action = ACTION_STOP_AZAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, MinshawiApp.CHANNEL_AZAN_ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد أذان $prayerName")
            .setContentText("يُرفع الآن الأذان بصوت الشيخ المنشاوي. ستستأنف التلاوة تلقائياً بعد الأذان.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(openAppIntent)
            .addAction(
                android.R.drawable.ic_media_play,
                "إيقاف الأذان واستئناف التلاوة الآن",
                stopPendingIntent
            )
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
