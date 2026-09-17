package com.minshawi.quran1967.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AzanReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(AzanScheduler.EXTRA_PRAYER_NAME) ?: "الصلاة"

        val serviceIntent = Intent(context, AzanPlaybackService::class.java).apply {
            putExtra(AzanScheduler.EXTRA_PRAYER_NAME, prayerName)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
