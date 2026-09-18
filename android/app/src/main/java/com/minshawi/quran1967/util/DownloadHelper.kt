package com.minshawi.quran1967.util

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
    private val resolvedFiles = ConcurrentHashMap<Int, File>()

    private val _downloadStates = MutableStateFlow<Map<Int, DownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<Int, DownloadProgress>> = _downloadStates.asStateFlow()

    fun normalizeArabic(text: String): String {
        return text
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
            .replace("\u064B", "")
            .replace("\u064C", "")
            .replace("\u064D", "")
            .replace("\u064E", "")
            .replace("\u064F", "")
            .replace("\u0650", "")
            .replace("\u0651", "")
            .replace("\u0652", "")
            .trim()
    }

    fun getCandidateFileNames(surah: Surah): List<String> {
        val paddedNum = String.format(Locale.US, "%03d", surah.number)
        val num = surah.number.toString()
        val normArabic = normalizeArabic(surah.arabicName)
        return listOf(
            "$paddedNum - ${surah.arabicName} - المنشاوي 1967.mp3",
            surah.archiveFileName,
            "$paddedNum - ${surah.arabicName}.mp3",
            "$num - ${surah.arabicName}.mp3",
            "$paddedNum - $normArabic - المنشاوي 1967.mp3",
            "$paddedNum - $normArabic.mp3",
            "$num - $normArabic.mp3",
            "$paddedNum - ${surah.arabicName} - المنشاوي.mp3",
            "$num - ${surah.arabicName} - المنشاوي.mp3",
            "$paddedNum - ${surah.englishName}.mp3",
            "$num - ${surah.englishName}.mp3",
            "$paddedNum.mp3",
            "$num.mp3",
            "${surah.arabicName}.mp3",
            "$normArabic.mp3"
        ).distinct()
    }

    fun isFileForSurah(file: File, surah: Surah): Boolean {
        if (!file.exists() || file.isDirectory || file.length() < 30_000L) return false
        val name = file.name
        if (name.endsWith(".part", ignoreCase = true) || !name.endsWith(".mp3", ignoreCase = true)) return false

        val paddedNum = String.format(Locale.US, "%03d", surah.number)
        val numStr = surah.number.toString()
        val cleanName = normalizeArabic(name.lowercase())
        val cleanSurahArabic = normalizeArabic(surah.arabicName.lowercase())

        if (getCandidateFileNames(surah).any { it.equals(name, ignoreCase = true) }) {
            return true
        }

        val startsWithNumber = name.startsWith("$paddedNum ") ||
                name.startsWith("$paddedNum-") ||
                name.startsWith("${paddedNum}_") ||
                name.startsWith("$paddedNum.") ||
                name.startsWith("$numStr ") ||
                name.startsWith("$numStr-") ||
                name.startsWith("${numStr}_") ||
                name.startsWith("$numStr.")

        if (startsWithNumber) {
            if (cleanName.contains(cleanSurahArabic) || file.parent?.contains("المنشاوي") == true || file.parent?.contains("Minshawi") == true) {
                return true
            }
            val baseWithoutExt = name.substringBeforeLast(".").trim()
            if (baseWithoutExt == paddedNum || baseWithoutExt == numStr) {
                return true
            }
        }

        if (cleanName.contains(cleanSurahArabic) && (cleanName.contains(paddedNum) || cleanName.contains(numStr))) {
            return true
        }

        return false
    }

    fun getCandidateSearchDirectories(context: Context): List<File> {
        val dirs = mutableListOf<File>()

        // 1. Primary app-specific storage (Zero permissions required, 100% accessible on Android 10+)
        context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.let {
            dirs.add(it)
            dirs.add(File(it, "مصحف المنشاوي 1967"))
        }
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
            dirs.add(it)
            dirs.add(File(it, "مصحف المنشاوي 1967"))
        }
        context.getExternalFilesDir(null)?.let {
            dirs.add(it)
            dirs.add(File(it, "مصحف المنشاوي 1967"))
            dirs.add(File(it, "Downloads"))
            dirs.add(File(it, "Music"))
        }
        dirs.add(context.filesDir)
        dirs.add(File(context.filesDir, "مصحف المنشاوي 1967"))

        // 2. Public Storage locations (where previous versions may have saved downloads)
        try {
            val pubDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (pubDownloads != null) {
                dirs.add(File(pubDownloads, "مصحف المنشاوي 1967"))
                dirs.add(pubDownloads)
            }
        } catch (_: Throwable) {}

        try {
            val pubMusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            if (pubMusic != null) {
                dirs.add(File(pubMusic, "مصحف المنشاوي 1967"))
                dirs.add(pubMusic)
            }
        } catch (_: Throwable) {}

        try {
            val extStorage = Environment.getExternalStorageDirectory()
            if (extStorage != null) {
                dirs.add(File(extStorage, "مصحف المنشاوي 1967"))
                dirs.add(File(extStorage, "Download/مصحف المنشاوي 1967"))
                dirs.add(File(extStorage, "Download"))
            }
        } catch (_: Throwable) {}

        return dirs.distinct()
    }

    private fun querySurahInMediaStore(context: Context, surah: Surah, targetFile: File): Boolean {
        try {
            val paddedNum = String.format(Locale.US, "%03d", surah.number)
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE
            )

            val urisToQuery = mutableListOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                urisToQuery.add(MediaStore.Downloads.EXTERNAL_CONTENT_URI)
            }

            for (contentUri in urisToQuery) {
                try {
                    val selection = "(${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ? OR ${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?) AND ${MediaStore.MediaColumns.SIZE} > 30000"
                    val selectionArgs = arrayOf("%$paddedNum%", "%${surah.arabicName}%")

                    context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)?.use { cursor ->
                        val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                        val nameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        val sizeCol = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)

                        while (cursor.moveToNext()) {
                            val name = if (nameCol != -1) cursor.getString(nameCol) ?: "" else ""
                            val size = if (sizeCol != -1) cursor.getLong(sizeCol) else 0L

                            if (size > 30_000L && (name.contains(paddedNum) || name.contains(surah.arabicName))) {
                                val id = cursor.getLong(idCol)
                                val itemUri = ContentUris.withAppendedId(contentUri, id)

                                context.contentResolver.openInputStream(itemUri)?.use { input ->
                                    val parent = targetFile.parentFile
                                    if (parent != null && !parent.exists()) {
                                        parent.mkdirs()
                                    }
                                    val tempTarget = File(targetFile.parentFile, "${targetFile.name}.tmp")
                                    FileOutputStream(tempTarget).use { output ->
                                        input.copyTo(output)
                                    }
                                    if (tempTarget.exists() && tempTarget.length() > 30_000L) {
                                        if (targetFile.exists()) targetFile.delete()
                                        tempTarget.renameTo(targetFile)
                                        return true
                                    } else {
                                        tempTarget.delete()
                                    }
                                }
                            }
                        }
                    }
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {}
        return false
    }

    private fun migrateToPrimaryStorage(sourceFile: File, targetFile: File): Boolean {
        if (sourceFile.absolutePath == targetFile.absolutePath) return true
        return try {
            val parent = targetFile.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }
            if (sourceFile.canRead()) {
                sourceFile.copyTo(targetFile, overwrite = true)
                targetFile.exists() && targetFile.length() > 30_000L
            } else {
                false
            }
        } catch (_: Throwable) {
            false
        }
    }

    fun getLocalSurahFile(context: Context, surah: Surah): File {
        // 1. Fast cache lookup
        val cached = resolvedFiles[surah.number]
        if (cached != null && cached.exists() && cached.length() > 30_000L && cached.canRead()) {
            return cached
        }

        val primaryName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3", surah.number, surah.arabicName)
        val appMusicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val primaryFile = File(appMusicDir, primaryName)

        // 2. Primary app-specific file check
        try {
            if (primaryFile.exists() && primaryFile.length() > 30_000L && primaryFile.canRead()) {
                resolvedFiles[surah.number] = primaryFile
                return primaryFile
            }
        } catch (_: Throwable) {}

        // 3. Check candidate filenames in appMusicDir
        for (cand in getCandidateFileNames(surah)) {
            try {
                val f = File(appMusicDir, cand)
                if (f.exists() && f.length() > 30_000L && f.canRead()) {
                    resolvedFiles[surah.number] = f
                    return f
                }
            } catch (_: Throwable) {}
        }

        // 4. Search across all candidate directories
        val candidateDirs = getCandidateSearchDirectories(context)
        for (dir in candidateDirs) {
            try {
                if (!dir.exists() || !dir.isDirectory) continue

                // Check candidate names in this directory
                for (candName in getCandidateFileNames(surah)) {
                    val candFile = File(dir, candName)
                    if (candFile.exists() && candFile.length() > 30_000L && candFile.canRead()) {
                        val migrated = migrateToPrimaryStorage(candFile, primaryFile)
                        val result = if (migrated) primaryFile else candFile
                        resolvedFiles[surah.number] = result
                        return result
                    }
                }

                // If not found by candidate name, check directory listing
                val files = dir.listFiles() ?: continue
                for (f in files) {
                    if (isFileForSurah(f, surah) && f.canRead()) {
                        val migrated = migrateToPrimaryStorage(f, primaryFile)
                        val result = if (migrated) primaryFile else f
                        resolvedFiles[surah.number] = result
                        return result
                    }
                }
            } catch (_: Throwable) {}
        }

        // 5. Fallback: Query MediaStore
        try {
            if (querySurahInMediaStore(context, surah, primaryFile)) {
                resolvedFiles[surah.number] = primaryFile
                return primaryFile
            }
        } catch (_: Throwable) {}

        try {
            if (!appMusicDir.exists()) {
                appMusicDir.mkdirs()
            }
        } catch (_: Throwable) {}

        return primaryFile
    }

    fun getPartSurahFile(context: Context, surah: Surah): File {
        val fileName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3.part", surah.number, surah.arabicName)
        val appMusicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val primaryPart = File(appMusicDir, fileName)
        try {
            if (primaryPart.exists()) {
                return primaryPart
            }
        } catch (_: Throwable) {}

        try {
            val pubDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "مصحف المنشاوي 1967")
            val pubPart = File(pubDir, fileName)
            if (pubPart.exists()) {
                return pubPart
            }
        } catch (_: Throwable) {}

        try {
            if (!appMusicDir.exists()) {
                appMusicDir.mkdirs()
            }
        } catch (_: Throwable) {}
        return primaryPart
    }

    fun isSurahDownloaded(context: Context, surah: Surah): Boolean {
        return try {
            val file = getLocalSurahFile(context, surah)
            file.exists() && file.length() > 30_000L && file.canRead()
        } catch (_: Throwable) {
            false
        }
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
                if (localFile.exists() && localFile.length() > 30_000L && localFile.canRead()) {
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
                    if (partFile.exists() && partFile.length() > 30_000L) {
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
        val existingBytes = if (partFile.exists()) partFile.length() else 0L
        val isResuming = existingBytes > 0L

        if (isResuming) {
            val current = currentState
            val total = current?.totalBytes?.takeIf { it > 0 } ?: (15L * 1024 * 1024)
            val pct = ((existingBytes * 100) / total).toInt().coerceIn(1, 99)
            Toast.makeText(context, "جاري استئناف تنزيل سورة ${surah.arabicName} من $pct%...", Toast.LENGTH_SHORT).show()
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
                    val currentPartLen = if (partFile.exists()) partFile.length() else 0L

                    connection = openUrlConnection(streamUrl, currentPartLen)
                    if (connection == null) continue

                    val responseCode = connection.responseCode
                    val isPartial = (responseCode == HttpURLConnection.HTTP_PARTIAL) // 206
                    val isOk = (responseCode == HttpURLConnection.HTTP_OK) // 200

                    if (!isPartial && !isOk) {
                        try { connection.disconnect() } catch (_: Exception) {}
                        continue
                    }

                    activeConnections[surah.number] = connection

                    val contentRange = connection.getHeaderField("Content-Range")
                    val totalFromRange = contentRange?.substringAfterLast("/")?.trim()?.toLongOrNull()
                    val contentLength = connection.contentLengthLong.takeIf { it > 0 } ?: (15L * 1024 * 1024)
                    val totalBytes = totalFromRange ?: if (isPartial) (currentPartLen + contentLength) else contentLength

                    val startOffset = if (isPartial) currentPartLen else 0L
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
                    if (partFile.exists() && partFile.length() > 30_000L) {
                        if (targetFile.exists()) targetFile.delete()
                        partFile.renameTo(targetFile)
                        resolvedFiles[surah.number] = targetFile

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
        while (redirects < 6) {
            val url = URL(currentUrl)
            connection = url.openConnection() as HttpURLConnection
            // Crucial: false prevents Java from silently dropping the HTTP Range header across cross-host 302 redirects!
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 20000
            connection.readTimeout = 30000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) MinshawiQuran1967App")
            if (rangeStart > 0L) {
                connection.setRequestProperty("Range", "bytes=$rangeStart-")
            }
            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_MOVED_PERM || 
                status == HttpURLConnection.HTTP_MOVED_TEMP || 
                status == HttpURLConnection.HTTP_SEE_OTHER ||
                status == 307 || status == 308) {
                val location = connection.getHeaderField("Location") ?: break
                currentUrl = if (location.startsWith("http")) location else URL(url, location).toString()
                try { connection.disconnect() } catch (_: Exception) {}
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
        val job = activeJobs.remove(surahNumber)
        val conn = activeConnections.remove(surahNumber)
        try { conn?.disconnect() } catch (_: Exception) {}
        job?.cancel()

        val partFile = context?.let { QuranRepository.getSurah(surahNumber)?.let { s -> getPartSurahFile(it, s) } }
        val partLen = if (partFile?.exists() == true) partFile.length() else 0L

        val current = _downloadStates.value[surahNumber]
        val total = current?.totalBytes?.takeIf { it > 0 } ?: (15L * 1024 * 1024)
        val pct = if (partLen > 0) ((partLen * 100) / total).toInt().coerceIn(1, 99) else (current?.percentage ?: 0)
        val frac = if (partLen > 0) (partLen.toFloat() / total.toFloat()).coerceIn(0.01f, 0.99f) else (current?.progress ?: 0f)

        _downloadStates.value = _downloadStates.value + (surahNumber to DownloadProgress(
            surahNumber = surahNumber,
            progress = frac,
            percentage = pct,
            speedFormatted = "متوقف مؤقتاً",
            downloadedFormatted = String.format(Locale.US, "%.1f / %.1f MB", partLen / (1024f * 1024f), total / (1024f * 1024f)),
            isDownloading = false,
            isPaused = true,
            totalBytes = total,
            downloadedBytes = partLen
        ))

        if (context != null) {
            val name = surahName.ifEmpty { QuranRepository.getSurah(surahNumber)?.arabicName ?: "" }
            Toast.makeText(context, "تم إيقاف تنزيل سورة $name مؤقتاً عند $pct% (اضغط للاستئناف)", Toast.LENGTH_SHORT).show()
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
        resolvedFiles.remove(surah.number)
        val targetFile = getLocalSurahFile(context, surah)
        val partFile = getPartSurahFile(context, surah)

        if (targetFile.exists()) targetFile.delete()
        if (partFile.exists()) partFile.delete()

        // Also clean any candidate files in candidate locations
        try {
            val candidateDirs = getCandidateSearchDirectories(context)
            for (dir in candidateDirs) {
                if (!dir.exists() || !dir.isDirectory) continue
                for (candName in getCandidateFileNames(surah)) {
                    val f = File(dir, candName)
                    if (f.exists()) f.delete()
                    val part = File(dir, "$candName.part")
                    if (part.exists()) part.delete()
                }
            }
        } catch (_: Throwable) {}

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

