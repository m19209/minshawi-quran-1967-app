package com.minshawi.quran1967.util

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.widget.Toast
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
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false
)

object DownloadHelper {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val activeJobs = ConcurrentHashMap<Int, Job>()

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

    fun isSurahDownloaded(context: Context, surah: Surah): Boolean {
        val file = getLocalSurahFile(context, surah)
        return file.exists() && file.length() > 50_000
    }

    fun checkInitialDownloadedState(context: Context, surahs: List<Surah>) {
        scope.launch {
            val initialMap = mutableMapOf<Int, DownloadProgress>()
            surahs.forEach { s ->
                if (isSurahDownloaded(context, s)) {
                    initialMap[s.number] = DownloadProgress(
                        surahNumber = s.number,
                        progress = 1f,
                        percentage = 100,
                        isDownloading = false,
                        isCompleted = true
                    )
                }
            }
            if (initialMap.isNotEmpty()) {
                _downloadStates.value = _downloadStates.value + initialMap
            }
        }
    }

    fun downloadSurah(context: Context, surah: Surah) {
        if (activeJobs.containsKey(surah.number)) {
            cancelDownload(surah.number)
            Toast.makeText(context, "تم إيقاف تنزيل سورة ${surah.arabicName}", Toast.LENGTH_SHORT).show()
            return
        }

        val targetFile = getLocalSurahFile(context, surah)
        if (targetFile.exists() && targetFile.length() > 50_000) {
            Toast.makeText(context, "سورة ${surah.arabicName} تم تنزيلها مسبقاً على جهازك", Toast.LENGTH_SHORT).show()
            updateState(surah.number, DownloadProgress(
                surahNumber = surah.number,
                progress = 1f,
                percentage = 100,
                isDownloading = false,
                isCompleted = true
            ))
            return
        }

        Toast.makeText(context, "بدأ تنزيل سورة ${surah.arabicName}...", Toast.LENGTH_SHORT).show()

        val job = scope.launch {
            var connection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                updateState(surah.number, DownloadProgress(
                    surahNumber = surah.number,
                    progress = 0f,
                    percentage = 0,
                    speedFormatted = "0 KB/s",
                    downloadedFormatted = "جاري الاتصال...",
                    isDownloading = true
                ))

                var currentUrl = surah.audioUrl1967
                var redirects = 0
                while (redirects < 5) {
                    val url = URL(currentUrl)
                    connection = url.openConnection() as HttpURLConnection
                    connection.instanceFollowRedirects = true
                    connection.connectTimeout = 15000
                    connection.readTimeout = 20000
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 MinshawiQuran1967App")
                    val status = connection.responseCode
                    if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP || status == 307 || status == 308) {
                        currentUrl = connection.getHeaderField("Location") ?: break
                        connection.disconnect()
                        redirects++
                    } else {
                        break
                    }
                }

                val totalBytes = connection?.contentLengthLong?.takeIf { it > 0 } ?: (15L * 1024 * 1024)
                inputStream = connection!!.inputStream
                outputStream = FileOutputStream(targetFile)

                val buffer = ByteArray(16384)
                var bytesDownloaded = 0L
                var lastBytes = 0L
                var lastTime = System.currentTimeMillis()

                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
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
                            isDownloading = true
                        ))

                        lastBytes = bytesDownloaded
                        lastTime = now
                    }
                }

                outputStream.flush()

                try {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(targetFile.absolutePath),
                        arrayOf("audio/mpeg"),
                        null
                    )
                } catch (_: Exception) {}

                updateState(surah.number, DownloadProgress(
                    surahNumber = surah.number,
                    progress = 1f,
                    percentage = 100,
                    speedFormatted = "تم الحفظ",
                    downloadedFormatted = "مكتمل",
                    isDownloading = false,
                    isCompleted = true
                ))

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "تم اكتمال تنزيل سورة ${surah.arabicName} بنجاح", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                if (targetFile.exists() && targetFile.length() < 50_000) {
                    targetFile.delete()
                }
                updateState(surah.number, DownloadProgress(
                    surahNumber = surah.number,
                    isDownloading = false,
                    isFailed = true
                ))
            } finally {
                try { inputStream?.close() } catch (_: Exception) {}
                try { outputStream?.close() } catch (_: Exception) {}
                connection?.disconnect()
                activeJobs.remove(surah.number)
            }
        }

        activeJobs[surah.number] = job
    }

    fun cancelDownload(surahNumber: Int) {
        activeJobs.remove(surahNumber)?.cancel()
        val current = _downloadStates.value[surahNumber]
        if (current != null && current.isDownloading) {
            _downloadStates.value = _downloadStates.value - surahNumber
        }
    }

    private fun updateState(surahNumber: Int, state: DownloadProgress) {
        _downloadStates.value = _downloadStates.value + (surahNumber to state)
    }
}
