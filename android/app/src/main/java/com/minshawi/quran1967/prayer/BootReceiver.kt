package com.minshawi.quran1967.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // Re-schedule prayer alarms upon restart or time change
            AzanScheduler.scheduleAllPrayers(context, CityLocation.EGYPT_CAIRO)
        }
    }
}
