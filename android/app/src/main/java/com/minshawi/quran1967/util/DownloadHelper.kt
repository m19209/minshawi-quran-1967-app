package com.minshawi.quran1967.util

import android.content.Context
import android.media.MediaScannerConnection
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
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false
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

    private fun getPartSurahFile(context: Context, surah: Surah): File {
        val fileName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3.part", surah.number, surah.arabicName)
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "مصحف المنشاوي 1967")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    fun isSurahDownloaded(context: Context, surah: Surah): Boolean {
        val file = getLocalSurahFile(context, surah)
        // A complete Surah MP3 is at least 200 KB
        return file.exists() && file.length() > 200_000
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
        // If already downloading, clicking the button cancels/stops the download cleanly
        if (activeJobs.containsKey(surah.number) || _downloadStates.value[surah.number]?.isDownloading == true) {
            cancelDownload(surah.number, surah.arabicName, context)
            return
        }

        val targetFile = getLocalSurahFile(context, surah)
        if (isSurahDownloaded(context, surah)) {
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

        val partFile = getPartSurahFile(context, surah)
        if (partFile.exists()) {
            partFile.delete()
        }

        Toast.makeText(context, "بدأ تنزيل سورة ${surah.arabicName}...", Toast.LENGTH_SHORT).show()

        val job = scope.launch {
            val candidateUrls = listOf(surah.audioUrl1967, surah.fallbackAudioUrl)
            var downloadSuccess = false

            for (streamUrl in candidateUrls) {
                if (coroutineContext[Job]?.isActive != true) break

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

                    connection = openUrlConnection(streamUrl)
                    if (connection == null || connection.responseCode != HttpURLConnection.HTTP_OK) {
                        try { connection?.disconnect() } catch (_: Exception) {}
                        continue
                    }

                    activeConnections[surah.number] = connection

                    val totalBytes = connection.contentLengthLong.takeIf { it > 0 } ?: (15L * 1024 * 1024)
                    inputStream = connection.inputStream
                    outputStream = FileOutputStream(partFile)

                    val buffer = ByteArray(32768)
                    var bytesDownloaded = 0L
                    var lastBytes = 0L
                    var lastTime = System.currentTimeMillis()

                    var read: Int
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
                                isDownloading = true
                            ))

                            lastBytes = bytesDownloaded
                            lastTime = now
                        }
                    }

                    outputStream.flush()
                    outputStream.close()
                    outputStream = null

                    // If job was cancelled while reading, do not mark complete
                    if (coroutineContext[Job]?.isActive != true) {
                        partFile.delete()
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
                        downloadSuccess = true
                        break // Successful download!
                    } else {
                        partFile.delete()
                    }

                } catch (e: Exception) {
                    if (partFile.exists()) {
                        partFile.delete()
                    }
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
                updateState(surah.number, DownloadProgress(
                    surahNumber = surah.number,
                    isDownloading = false,
                    isFailed = true
                ))
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "تعذر إكمال تنزيل سورة ${surah.arabicName}، يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show()
                }
            }

            activeJobs.remove(surah.number)
        }

        activeJobs[surah.number] = job
    }

    private fun openUrlConnection(urlString: String): HttpURLConnection? {
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

    fun cancelDownload(surahNumber: Int, surahName: String = "", context: Context? = null) {
        activeJobs.remove(surahNumber)?.cancel()
        activeConnections.remove(surahNumber)?.let {
            try { it.disconnect() } catch (_: Exception) {}
        }

        // Remove downloading state immediately
        _downloadStates.value = _downloadStates.value - surahNumber

        if (context != null) {
            val surah = QuranRepository.getSurah(surahNumber)
            val partFile = if (surah != null) getPartSurahFile(context, surah) else null
            partFile?.delete()

            val name = surahName.ifEmpty { surah?.arabicName ?: "" }
            Toast.makeText(context, "تم إيقاف تنزيل سورة $name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateState(surahNumber: Int, state: DownloadProgress) {
        _downloadStates.value = _downloadStates.value + (surahNumber to state)
    }
}

