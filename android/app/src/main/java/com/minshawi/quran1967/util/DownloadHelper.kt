package com.minshawi.quran1967.util

import android.content.Context
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.widget.Toast
import com.minshawi.quran1967.data.QuranRepository
import com.minshawi.quran1967.data.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

data class DownloadProgress(
    val surahNumber: Int,
    val progress: Float = 0f,
    val percentage: Int = 0,
    val speedFormatted: String = "",
    val downloadedFormatted: String = "",
    val isDownloading: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false,
    val totalBytes: Long = 0L,
    val downloadedBytes: Long = 0L
)

object DownloadHelper {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val activeJobs = ConcurrentHashMap<Int, Job>()
    private val activeConnections = ConcurrentHashMap<Int, HttpURLConnection>()

    private val _downloadStates = MutableStateFlow<Map<Int, DownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<Int, DownloadProgress>> = _downloadStates.asStateFlow()

    fun getLocalSurahFile(context: Context, surah: Surah): File {
        val fileName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3", surah.number, surah.arabicName)
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "مصحف المنشاوي 1967")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    fun getPartSurahFile(context: Context, surah: Surah): File {
        val fileName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3.part", surah.number, surah.arabicName)
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "مصحف المنشاوي 1967")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    fun isSurahDownloaded(context: Context, surah: Surah): Boolean {
        val file = getLocalSurahFile(context, surah)
        // A complete Surah MP3 is at least 150 KB
        return file.exists() && file.length() > 150_000
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return true
    }

    fun checkInitialDownloadedState(context: Context, surahs: List<Surah>) {
        scope.launch {
            val initialMap = mutableMapOf<Int, DownloadProgress>()
            surahs.forEach { s ->
                val localFile = getLocalSurahFile(context, s)
                if (localFile.exists() && localFile.length() > 150_000) {
                    val len = localFile.length()
                    initialMap[s.number] = DownloadProgress(
                        surahNumber = s.number,
                        progress = 1f,
                        percentage = 100,
                        speedFormatted = "تم الحفظ",
                        downloadedFormatted = "مكتمل",
                        isDownloading = false,
                        isPaused = false,
                        isCompleted = true,
                        totalBytes = len,
                        downloadedBytes = len
                    )
                } else {
                    val partFile = getPartSurahFile(context, s)
                    if (partFile.exists() && partFile.length() > 50_000) {
                        val partLen = partFile.length()
                        val estTotal = 15L * 1024 * 1024
                        val pct = ((partLen * 100) / estTotal).toInt().coerceIn(1, 99)
                        initialMap[s.number] = DownloadProgress(
                            surahNumber = s.number,
                            progress = (partLen.toFloat() / estTotal.toFloat()).coerceIn(0f, 0.99f),
                            percentage = pct,
                            speedFormatted = "متوقف مؤقتاً",
                            downloadedFormatted = String.format(Locale.US, "%.1f MB", partLen / (1024f * 1024f)),
                            isDownloading = false,
                            isPaused = true,
                            totalBytes = estTotal,
                            downloadedBytes = partLen
                        )
                    }
                }
            }
            if (initialMap.isNotEmpty()) {
                _downloadStates.value = _downloadStates.value + initialMap
            }
        }
    }

    /**
     * Toggles or initiates download with full Pause/Resume & HTTP Range capabilities.
     */
    fun downloadSurah(context: Context, surah: Surah) {
        val currentState = _downloadStates.value[surah.number]

        // 1. If currently active in downloading -> Clicking it will PAUSE
        if (activeJobs.containsKey(surah.number) || currentState?.isDownloading == true) {
            pauseDownload(surah.number, surah.arabicName, context)
            return
        }

        // 2. If already completed -> Already on device
        val targetFile = getLocalSurahFile(context, surah)
        if (isSurahDownloaded(context, surah)) {
            val len = targetFile.length()
            updateState(surah.number, DownloadProgress(
                surahNumber = surah.number,
                progress = 1f,
                percentage = 100,
                speedFormatted = "تم الحفظ",
                downloadedFormatted = "مكتمل",
                isDownloading = false,
                isPaused = false,
                isCompleted = true,
                totalBytes = len,
                downloadedBytes = len
            ))
            return
        }

        // 3. Network connectivity check before initiating
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "لا يوجد اتصال بالإنترنت لبدء تنزيل سورة ${surah.arabicName}", Toast.LENGTH_LONG).show()
            return
        }

        val partFile = getPartSurahFile(context, surah)
        val isResuming = partFile.exists() && partFile.length() > 0 && currentState?.isPaused == true

        if (isResuming) {
            Toast.makeText(context, "جاري استئناف تنزيل سورة ${surah.arabicName}...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "بدأ تنزيل سورة ${surah.arabicName}...", Toast.LENGTH_SHORT).show()
        }

        val job = scope.launch {
            val candidateUrls = listOf(surah.audioUrl1967, surah.fallbackAudioUrl)
            var downloadSuccess = false

            for (streamUrl in candidateUrls) {
                if (coroutineContext[Job]?.isActive != true) break

                var connection: HttpURLConnection? = null
                var inputStream: InputStream? = null
                var outputStream: FileOutputStream? = null

                try {
                    val existingBytes = if (partFile.exists()) partFile.length() else 0L

                    connection = openUrlConnection(streamUrl, existingBytes)
                    if (connection == null) continue

                    val responseCode = connection.responseCode
                    val isPartial = (responseCode == HttpURLConnection.HTTP_PARTIAL) // 206
                    val isOk = (responseCode == HttpURLConnection.HTTP_OK) // 200

                    if (!isPartial && !isOk) {
                        try { connection.disconnect() } catch (_: Exception) {}
                        continue
                    }

                    activeConnections[surah.number] = connection

                    val contentLength = connection.contentLengthLong.takeIf { it > 0 } ?: (15L * 1024 * 1024)
                    val totalBytes = if (isPartial) (existingBytes + contentLength) else contentLength

                    val startOffset = if (isPartial) existingBytes else 0L
                    if (!isPartial && partFile.exists()) {
                        partFile.delete()
                    }

                    inputStream = connection.inputStream
                    outputStream = FileOutputStream(partFile, isPartial)

                    val initialPct = if (totalBytes > 0) ((startOffset * 100) / totalBytes).toInt().coerceIn(0, 99) else 0
                    val initialFrac = if (totalBytes > 0) (startOffset.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f

                    updateState(surah.number, DownloadProgress(
                        surahNumber = surah.number,
                        progress = initialFrac,
                        percentage = initialPct,
                        speedFormatted = if (isResuming) "جاري الاستئناف..." else "0 KB/s",
                        downloadedFormatted = String.format(Locale.US, "%.1f / %.1f MB", startOffset / (1024f * 1024f), totalBytes / (1024f * 1024f)),
                        isDownloading = true,
                        isPaused = false,
                        totalBytes = totalBytes,
                        downloadedBytes = startOffset
                    ))

                    val buffer = ByteArray(32768)
                    var bytesDownloaded = startOffset
                    var lastBytes = bytesDownloaded
                    var lastTime = System.currentTimeMillis()

                    var read = 0
                    while (coroutineContext[Job]?.isActive == true && inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                        bytesDownloaded += read

                        val now = System.currentTimeMillis()
                        val timeDelta = now - lastTime
                        if (timeDelta >= 250) {
                            val bytesDelta = bytesDownloaded - lastBytes
                            val speedBytesPerSec = if (timeDelta > 0) (bytesDelta * 1000f) / timeDelta else 0f
                            val speedStr = if (speedBytesPerSec >= 1024 * 1024) {
                                String.format(Locale.US, "%.1f MB/s", speedBytesPerSec / (1024f * 1024f))
                            } else {
                                String.format(Locale.US, "%d KB/s", (speedBytesPerSec / 1024f).toInt())
                            }

                            val downloadedMb = bytesDownloaded / (1024f * 1024f)
                            val totalMb = totalBytes / (1024f * 1024f)
                            val dataStr = String.format(Locale.US, "%.1f / %.1f MB", downloadedMb, totalMb)

                            val pct = ((bytesDownloaded * 100) / totalBytes).toInt().coerceIn(0, 99)
                            val progressFraction = (bytesDownloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)

                            updateState(surah.number, DownloadProgress(
                                surahNumber = surah.number,
                                progress = progressFraction,
                                percentage = pct,
                                speedFormatted = speedStr,
                                downloadedFormatted = dataStr,
                                isDownloading = true,
                                isPaused = false,
                                totalBytes = totalBytes,
                                downloadedBytes = bytesDownloaded
                            ))

                            lastBytes = bytesDownloaded
                            lastTime = now
                        }
                    }

                    outputStream.flush()
                    outputStream.close()
                    outputStream = null

                    // If job was cancelled or paused while reading, exit cleanly without deleting part
                    if (coroutineContext[Job]?.isActive != true) {
                        return@launch
                    }

                    // Verify file size and rename part to final target file
                    if (partFile.exists() && partFile.length() > 100_000) {
                        if (targetFile.exists()) targetFile.delete()
                        partFile.renameTo(targetFile)

                        try {
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(targetFile.absolutePath),
                                arrayOf("audio/mpeg"),
                                null
                            )
                        } catch (_: Exception) {}

                        val finalLen = targetFile.length()
                        updateState(surah.number, DownloadProgress(
                            surahNumber = surah.number,
                            progress = 1f,
                            percentage = 100,
                            speedFormatted = "تم الحفظ",
                            downloadedFormatted = "مكتمل",
                            isDownloading = false,
                            isPaused = false,
                            isCompleted = true,
                            totalBytes = finalLen,
                            downloadedBytes = finalLen
                        ))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "تم اكتمال تنزيل سورة ${surah.arabicName} بنجاح", Toast.LENGTH_LONG).show()
                        }
                        downloadSuccess = true
                        break // Success!
                    }

                } catch (e: Exception) {
                    if (coroutineContext[Job]?.isActive != true) {
                        return@launch
                    }
                } finally {
                    try { inputStream?.close() } catch (_: Exception) {}
                    try { outputStream?.close() } catch (_: Exception) {}
                    try { connection?.disconnect() } catch (_: Exception) {}
                    activeConnections.remove(surah.number)
                }
            }

            if (!downloadSuccess && coroutineContext[Job]?.isActive == true) {
                val partLen = if (partFile.exists()) partFile.length() else 0L
                if (partLen > 10_000) {
                    // Graceful network pause so user can resume seamlessly
                    val estTotal = 15L * 1024 * 1024
                    val pct = ((partLen * 100) / estTotal).toInt().coerceIn(1, 99)
                    updateState(surah.number, DownloadProgress(
                        surahNumber = surah.number,
                        progress = (partLen.toFloat() / estTotal.toFloat()).coerceIn(0f, 0.99f),
                        percentage = pct,
                        speedFormatted = "متوقف مؤقتاً",
                        downloadedFormatted = "توقف مؤقت لضعف الشبكة",
                        isDownloading = false,
                        isPaused = true,
                        totalBytes = estTotal,
                        downloadedBytes = partLen
                    ))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "توقف التنزيل لضعف الشبكة، اضغط على الزر للاستئناف", Toast.LENGTH_LONG).show()
                    }
                } else {
                    updateState(surah.number, DownloadProgress(
                        surahNumber = surah.number,
                        isDownloading = false,
                        isFailed = true
                    ))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "تعذر تنزيل سورة ${surah.arabicName}، يرجى المحاولة لاحقاً", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            activeJobs.remove(surah.number)
        }

        activeJobs[surah.number] = job
    }

    private fun openUrlConnection(urlString: String, rangeStart: Long = 0L): HttpURLConnection? {
        var currentUrl = urlString
        var redirects = 0
        var connection: HttpURLConnection? = null
        while (redirects < 5) {
            val url = URL(currentUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 15000
            connection.readTimeout = 25000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) MinshawiQuran1967App")
            if (rangeStart > 0L) {
                connection.setRequestProperty("Range", "bytes=$rangeStart-")
            }
            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP || status == 307 || status == 308) {
                currentUrl = connection.getHeaderField("Location") ?: break
                connection.disconnect()
                redirects++
            } else {
                break
            }
        }
        return connection
    }

    /**
     * Pauses the active download cleanly, keeping the .part file for instant resuming.
     */
    fun pauseDownload(surahNumber: Int, surahName: String = "", context: Context? = null) {
        activeJobs.remove(surahNumber)?.cancel()
        activeConnections.remove(surahNumber)?.let {
            try { it.disconnect() } catch (_: Exception) {}
        }

        val current = _downloadStates.value[surahNumber]
        if (current != null) {
            _downloadStates.value = _downloadStates.value + (surahNumber to current.copy(
                isDownloading = false,
                isPaused = true,
                speedFormatted = "متوقف مؤقتاً"
            ))
        }

        if (context != null) {
            val name = surahName.ifEmpty { QuranRepository.getSurah(surahNumber)?.arabicName ?: "" }
            Toast.makeText(context, "تم إيقاف تنزيل سورة $name مؤقتاً (اضغط للاستئناف)", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cancels the download completely and deletes the temporary .part file.
     */
    fun cancelDownload(surahNumber: Int, surahName: String = "", context: Context? = null) {
        activeJobs.remove(surahNumber)?.cancel()
        activeConnections.remove(surahNumber)?.let {
            try { it.disconnect() } catch (_: Exception) {}
        }

        _downloadStates.value = _downloadStates.value - surahNumber

        if (context != null) {
            val surah = QuranRepository.getSurah(surahNumber)
            val partFile = if (surah != null) getPartSurahFile(context, surah) else null
            partFile?.delete()

            val name = surahName.ifEmpty { surah?.arabicName ?: "" }
            Toast.makeText(context, "تم إلغاء تنزيل سورة $name", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Deletes the completed offline surah file to free storage space and returns to idle download state.
     */
    fun deleteDownloadedSurah(context: Context, surah: Surah) {
        val targetFile = getLocalSurahFile(context, surah)
        val partFile = getPartSurahFile(context, surah)

        if (targetFile.exists()) targetFile.delete()
        if (partFile.exists()) partFile.delete()

        try {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetFile.absolutePath),
                arrayOf("audio/mpeg"),
                null
            )
        } catch (_: Exception) {}

        _downloadStates.value = _downloadStates.value - surah.number
        Toast.makeText(context, "تم حذف سورة ${surah.arabicName} وتوفير المساحة", Toast.LENGTH_SHORT).show()
    }

    private fun updateState(surahNumber: Int, state: DownloadProgress) {
        _downloadStates.value = _downloadStates.value + (surahNumber to state)
    }
}

