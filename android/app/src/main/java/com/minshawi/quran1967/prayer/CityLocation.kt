package com.minshawi.quran1967.prayer

import com.batoulapps.adhan.CalculationMethod

data class CityLocation(
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN
) {
    val displayName: String
        get() = "$countryName - $cityName"

    companion object {
        val EGYPT_CAIRO = CityLocation("القاهرة", "مصر", 30.0444, 31.2357, CalculationMethod.EGYPTIAN)
        val EGYPT_ALEXANDRIA = CityLocation("الإسكندرية", "مصر", 31.2001, 29.9187, CalculationMethod.EGYPTIAN)
        val EGYPT_GIZA = CityLocation("الجيزة", "مصر", 30.0131, 31.2089, CalculationMethod.EGYPTIAN)
        val EGYPT_MANSOURA = CityLocation("المنصورة", "مصر", 31.0409, 31.3785, CalculationMethod.EGYPTIAN)
        val EGYPT_ASWAN = CityLocation("أسوان", "مصر", 24.0889, 32.8998, CalculationMethod.EGYPTIAN)
        
        val SAUDI_MAKKAH = CityLocation("مكة المكرمة", "المملكة العربية السعودية", 21.3891, 39.8579, CalculationMethod.UMM_AL_QURA)
        val SAUDI_MADINAH = CityLocation("المدينة المنورة", "المملكة العربية السعودية", 24.5247, 39.5692, CalculationMethod.UMM_AL_QURA)
        val SAUDI_RIYADH = CityLocation("الرياض", "المملكة العربية السعودية", 24.7136, 46.6753, CalculationMethod.UMM_AL_QURA)
        
        val UAE_DUBAI = CityLocation("دبي", "الإمارات العربية المتحدة", 25.2048, 55.2708, CalculationMethod.DUBAI)
        val UAE_ABU_DHABI = CityLocation("أبوظبي", "الإمارات العربية المتحدة", 24.4539, 54.3773, CalculationMethod.DUBAI)
        
        val JORDAN_AMMAN = CityLocation("عمان", "الأردن", 31.9454, 35.9284, CalculationMethod.MUSLIM_WORLD_LEAGUE)
        val PALESTINE_JERUSALEM = CityLocation("القدس الشريف", "فلسطين", 31.7683, 35.2137, CalculationMethod.MUSLIM_WORLD_LEAGUE)
        val SYRIA_DAMASCUS = CityLocation("دمشق", "سوريا", 33.5138, 36.2765, CalculationMethod.MUSLIM_WORLD_LEAGUE)
        val IRAQ_BAGHDAD = CityLocation("بغداد", "العراق", 33.3152, 44.3661, CalculationMethod.MUSLIM_WORLD_LEAGUE)
        val KUWAIT_CITY = CityLocation("الكويت", "الكويت", 29.3759, 47.9774, CalculationMethod.KUWAIT)
        val QATAR_DOHA = CityLocation("الدوحة", "قطر", 25.2854, 51.5310, CalculationMethod.QATAR)
        val MOROCCO_RABAT = CityLocation("الرباط", "المغرب", 34.0209, -6.8416, CalculationMethod.MUSLIM_WORLD_LEAGUE)

        val DEFAULT_CITIES: List<CityLocation> = listOf(
            EGYPT_CAIRO,
            EGYPT_ALEXANDRIA,
            EGYPT_GIZA,
            EGYPT_MANSOURA,
            EGYPT_ASWAN,
            SAUDI_MAKKAH,
            SAUDI_MADINAH,
            SAUDI_RIYADH,
            UAE_DUBAI,
            UAE_ABU_DHABI,
            JORDAN_AMMAN,
            PALESTINE_JERUSALEM,
            SYRIA_DAMASCUS,
            IRAQ_BAGHDAD,
            KUWAIT_CITY,
            QATAR_DOHA,
            MOROCCO_RABAT
        )
    }
}
