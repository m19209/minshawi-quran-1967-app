package com.minshawi.quran1967.prayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.batoulapps.adhan.Prayer
import java.util.Date

object AzanScheduler {

    const val ACTION_AZAN = "com.minshawi.quran1967.ACTION_AZAN_ALARM"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"

    fun scheduleAllPrayers(context: Context, location: CityLocation) {
        val schedule = PrayerCalculator.calculateTodayPrayers(location)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()

        for (item in schedule.items) {
            // Do not schedule for Sunrise, only the 5 prayers
            if (item.prayer == Prayer.SUNRISE) continue

            val triggerTime = item.time.time
            if (triggerTime > now) {
                scheduleExactAlarm(context, alarmManager, triggerTime, item.arabicName, item.prayer.ordinal)
            }
        }
    }

    private fun scheduleExactAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        prayerName: String,
        requestCode: Int
    ) {
        val intent = Intent(context, AzanReceiver::class.java).apply {
            action = ACTION_AZAN
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // On Android 12+, exact alarm permission might need user approval
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    /**
     * Helper to simulate an Azan right now for testing
     */
    fun triggerTestAzan(context: Context, prayerName: String = "الظهر") {
        val intent = Intent(context, AzanPlaybackService::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
