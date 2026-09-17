package com.minshawi.quran1967.data

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class Surah(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val ayahCount: Int,
    val isMakki: Boolean,
    val archiveFileName: String
) {
    /**
     * Primary direct stream URL: The newly broadcasted 1967 pure edition from Archive.org
     */
    val audioUrl1967: String
        get() {
            val encodedFileName = URLEncoder.encode(archiveFileName, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
            return "https://archive.org/download/a00ssssss260908ddd/$encodedFileName"
        }

    /**
     * Fallback high quality CDN mirror for Sheikh Minshawi Murattal
     */
    val fallbackAudioUrl: String
        get() = "https://server10.mp3quran.net/minsh/${String.format("%03d", number)}.mp3"
}
