package com.minshawi.quran1967.prayer

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.Prayer
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PrayerTimeItem(
    val prayer: Prayer,
    val arabicName: String,
    val time: Date,
    val formattedTime: String,
    val isCurrent: Boolean = false,
    val isNext: Boolean = false
)

data class DayPrayerSchedule(
    val location: CityLocation,
    val items: List<PrayerTimeItem>,
    val currentPrayerName: String,
    val nextPrayerName: String,
    val nextPrayerTime: Date,
    val remainingMillis: Long
)

object PrayerCalculator {

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale("ar")).apply {
        // Arabic AM/PM formatting
    }

    fun calculateTodayPrayers(location: CityLocation, now: Date = Date()): DayPrayerSchedule {
        val calendar = Calendar.getInstance().apply { time = now }
        val dateComponents = DateComponents.from(calendar.time)
        val coordinates = Coordinates(location.latitude, location.longitude)
        
        val params = location.calculationMethod.parameters.apply {
            madhab = Madhab.SHAFI
        }

        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        // Tomorrow's Fajr for after Isha countdown
        val tomorrowCal = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowComponents = DateComponents.from(tomorrowCal.time)
        val tomorrowPrayers = PrayerTimes(coordinates, tomorrowComponents, params)

        val prayerList = listOf(
            Prayer.FAJR to ("الفجر" to prayerTimes.fajr),
            Prayer.SUNRISE to ("الشروق" to prayerTimes.sunrise),
            Prayer.DHUHR to ("الظهر" to prayerTimes.dhuhr),
            Prayer.ASR to ("العصر" to prayerTimes.asr),
            Prayer.MAGHRIB to ("المغرب" to prayerTimes.maghrib),
            Prayer.ISHA to ("العشاء" to prayerTimes.isha)
        )

        val currentPrayerType = prayerTimes.currentPrayer()
        val nextPrayerType = prayerTimes.nextPrayer()

        val nextPrayerName: String
        val nextPrayerDate: Date

        if (nextPrayerType == Prayer.NONE) {
            // After Isha, next prayer is Tomorrow's Fajr
            nextPrayerName = "الفجر"
            nextPrayerDate = tomorrowPrayers.fajr
        } else {
            nextPrayerName = getArabicPrayerName(nextPrayerType)
            nextPrayerDate = prayerTimes.timeForPrayer(nextPrayerType) ?: tomorrowPrayers.fajr
        }

        val currentPrayerName = if (currentPrayerType == Prayer.NONE) {
            "العشاء"
        } else {
            getArabicPrayerName(currentPrayerType)
        }

        val remainingMillis = (nextPrayerDate.time - now.time).coerceAtLeast(0L)

        val items = prayerList.map { (prayer, info) ->
            val (arabic, date) = info
            PrayerTimeItem(
                prayer = prayer,
                arabicName = arabic,
                time = date,
                formattedTime = formatTimeInArabic(date),
                isCurrent = (prayer == currentPrayerType),
                isNext = (prayer == nextPrayerType || (nextPrayerType == Prayer.NONE && prayer == Prayer.FAJR))
            )
        }

        return DayPrayerSchedule(
            location = location,
            items = items,
            currentPrayerName = currentPrayerName,
            nextPrayerName = nextPrayerName,
            nextPrayerTime = nextPrayerDate,
            remainingMillis = remainingMillis
        )
    }

    fun getArabicPrayerName(prayer: Prayer): String {
        return when (prayer) {
            Prayer.FAJR -> "الفجر"
            Prayer.SUNRISE -> "الشروق"
            Prayer.DHUHR -> "الظهر"
            Prayer.ASR -> "العصر"
            Prayer.MAGHRIB -> "المغرب"
            Prayer.ISHA -> "العشاء"
            Prayer.NONE -> "لا يوجد"
        }
    }

    private fun formatTimeInArabic(date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        var hour = cal.get(Calendar.HOUR)
        if (hour == 0) hour = 12
        val minute = cal.get(Calendar.MINUTE)
        val isAm = cal.get(Calendar.AM_PM) == Calendar.AM
        val period = if (isAm) "ص" else "م"
        return String.format(Locale("ar"), "%d:%02d %s", hour, minute, period)
    }
}
