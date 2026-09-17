package com.minshawi.quran1967

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MinshawiApp : Application() {

    companion object {
        const val CHANNEL_QURAN_PLAYBACK = "quran_playback_channel"
        const val CHANNEL_AZAN_ALARM = "azan_alarm_channel"
        lateinit var instance: MinshawiApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Media Playback Channel for Quran
            val quranChannel = NotificationChannel(
                CHANNEL_QURAN_PLAYBACK,
                "تلاوات القرآن الكريم (ختمة 1967)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعار التحكم في تشغيل تلاوات الشيخ المنشاوي في الخلفية"
                setShowBadge(false)
            }

            // High Priority Channel for Azan
            val azanChannel = NotificationChannel(
                CHANNEL_AZAN_ALARM,
                "أذان مواقيت الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات وصوت الأذان عند حلول أوقات الصلاة"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(quranChannel)
            notificationManager.createNotificationChannel(azanChannel)
        }
    }
}
