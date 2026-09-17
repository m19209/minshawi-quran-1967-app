package com.minshawi.quran1967.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.minshawi.quran1967.data.Surah
import java.util.Locale

object DownloadHelper {

    fun downloadSurah(context: Context, surah: Surah) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager == null) {
                Toast.makeText(context, "تعذر الوصول إلى خدمة التنزيل في الجهاز", Toast.LENGTH_SHORT).show()
                return
            }

            val fileName = String.format(Locale.US, "%03d - %s - المنشاوي 1967.mp3", surah.number, surah.arabicName)
            val subPath = "مصحف المنشاوي 1967/" + fileName

            val request = DownloadManager.Request(Uri.parse(surah.audioUrl1967))
                .setTitle("سورة " + surah.arabicName + " - المنشاوي 1967")
                .setDescription("جاري تنزيل التلاوة المرتلة النقية 1967")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            downloadManager.enqueue(request)

            Toast.makeText(
                context,
                "بدأ تنزيل سورة " + surah.arabicName + " إلى مجلد التنزيلات...",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "حدث خطأ أثناء بدء التنزيل: " + (e.localizedMessage ?: ""),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
