package com.minshawi.quran1967.prayer

import com.batoulapps.adhan.CalculationMethod

data class CityLocation(
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    val region: String = ""
) {
    val displayName: String
        get() = "$countryName - $cityName"

    companion object {
        // 1. مصر (Egypt)
        val EGYPT_CAIRO = CityLocation("القاهرة", "مصر", 30.0444, 31.2357, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_ALEXANDRIA = CityLocation("الإسكندرية", "مصر", 31.2001, 29.9187, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_GIZA = CityLocation("الجيزة", "مصر", 30.0131, 31.2089, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_MANSOURA = CityLocation("المنصورة", "مصر", 31.0409, 31.3785, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_ASWAN = CityLocation("أسوان", "مصر", 24.0889, 32.8998, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_TANTA = CityLocation("طنطا", "مصر", 30.7865, 31.0004, CalculationMethod.EGYPTIAN, "مصر")
        val EGYPT_PORT_SAID = CityLocation("بورسعيد", "مصر", 31.2653, 32.3019, CalculationMethod.EGYPTIAN, "مصر")

        // 2. المملكة العربية السعودية (Saudi Arabia)
        val SAUDI_MAKKAH = CityLocation("مكة المكرمة", "المملكة العربية السعودية", 21.3891, 39.8579, CalculationMethod.UMM_AL_QURA, "الخليج")
        val SAUDI_MADINAH = CityLocation("المدينة المنورة", "المملكة العربية السعودية", 24.5247, 39.5692, CalculationMethod.UMM_AL_QURA, "الخليج")
        val SAUDI_RIYADH = CityLocation("الرياض", "المملكة العربية السعودية", 24.7136, 46.6753, CalculationMethod.UMM_AL_QURA, "الخليج")
        val SAUDI_JEDDAH = CityLocation("جدة", "المملكة العربية السعودية", 21.4858, 39.1925, CalculationMethod.UMM_AL_QURA, "الخليج")
        val SAUDI_DAMMAM = CityLocation("الدمام", "المملكة العربية السعودية", 26.4207, 50.0888, CalculationMethod.UMM_AL_QURA, "الخليج")

        // 3. الإمارات العربية المتحدة (UAE)
        val UAE_DUBAI = CityLocation("دبي", "الإمارات العربية المتحدة", 25.2048, 55.2708, CalculationMethod.DUBAI, "الخليج")
        val UAE_ABU_DHABI = CityLocation("أبوظبي", "الإمارات العربية المتحدة", 24.4539, 54.3773, CalculationMethod.DUBAI, "الخليج")
        val UAE_SHARJAH = CityLocation("الشارقة", "الإمارات العربية المتحدة", 25.3463, 55.4209, CalculationMethod.DUBAI, "الخليج")

        // 4. الكويت (Kuwait)
        val KUWAIT_CITY = CityLocation("مدينة الكويت", "الكويت", 29.3759, 47.9774, CalculationMethod.KUWAIT, "الخليج")

        // 5. قطر (Qatar)
        val QATAR_DOHA = CityLocation("الدوحة", "قطر", 25.2854, 51.5310, CalculationMethod.QATAR, "الخليج")

        // 6. سلطنة عمان (Oman)
        val OMAN_MUSCAT = CityLocation("مسقط", "سلطنة عمان", 23.5880, 58.3829, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الخليج")
        val OMAN_SALALAH = CityLocation("صلالة", "سلطنة عمان", 17.0151, 54.0924, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الخليج")

        // 7. البحرين (Bahrain)
        val BAHRAIN_MANAMA = CityLocation("المنامة", "البحرين", 26.2285, 50.5860, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الخليج")

        // 8. فلسطين (Palestine)
        val PALESTINE_JERUSALEM = CityLocation("القدس الشريف", "فلسطين", 31.7683, 35.2137, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val PALESTINE_GAZA = CityLocation("غزة", "فلسطين", 31.5017, 34.4668, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val PALESTINE_RAMALLAH = CityLocation("رام الله", "فلسطين", 31.9038, 35.2034, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val PALESTINE_NABLUS = CityLocation("نابلس", "فلسطين", 32.2227, 35.2621, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")

        // 9. الأردن (Jordan)
        val JORDAN_AMMAN = CityLocation("عمان", "الأردن", 31.9454, 35.9284, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val JORDAN_IRBID = CityLocation("إربد", "الأردن", 32.5568, 35.8469, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val JORDAN_ZARQA = CityLocation("الزرقاء", "الأردن", 32.0608, 36.0942, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")

        // 10. سوريا (Syria)
        val SYRIA_DAMASCUS = CityLocation("دمشق", "سوريا", 33.5138, 36.2765, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val SYRIA_ALEPPO = CityLocation("حلب", "سوريا", 36.2021, 37.1343, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val SYRIA_HOMS = CityLocation("حمص", "سوريا", 34.7324, 36.7137, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")

        // 11. لبنان (Lebanon)
        val LEBANON_BEIRUT = CityLocation("بيروت", "لبنان", 33.8938, 35.5018, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")
        val LEBANON_TRIPOLI = CityLocation("طرابلس", "لبنان", 34.4367, 35.8497, CalculationMethod.MUSLIM_WORLD_LEAGUE, "الشام")

        // 12. العراق (Iraq)
        val IRAQ_BAGHDAD = CityLocation("بغداد", "العراق", 33.3152, 44.3661, CalculationMethod.MUSLIM_WORLD_LEAGUE, "العراق")
        val IRAQ_BASRA = CityLocation("البصرة", "العراق", 30.5081, 47.7835, CalculationMethod.MUSLIM_WORLD_LEAGUE, "العراق")
        val IRAQ_MOSUL = CityLocation("الموصل", "العراق", 36.3489, 43.1577, CalculationMethod.MUSLIM_WORLD_LEAGUE, "العراق")
        val IRAQ_ERBIL = CityLocation("أربيل", "العراق", 36.1901, 44.0091, CalculationMethod.MUSLIM_WORLD_LEAGUE, "العراق")

        // 13. اليمن (Yemen)
        val YEMEN_SANAA = CityLocation("صنعاء", "اليمن", 15.3694, 44.1910, CalculationMethod.MUSLIM_WORLD_LEAGUE, "اليمن")
        val YEMEN_ADEN = CityLocation("عدن", "اليمن", 12.7855, 45.0187, CalculationMethod.MUSLIM_WORLD_LEAGUE, "اليمن")
        val YEMEN_TAIZ = CityLocation("تعز", "اليمن", 13.5795, 44.0209, CalculationMethod.MUSLIM_WORLD_LEAGUE, "اليمن")

        // 14. السودان (Sudan)
        val SUDAN_KHARTOUM = CityLocation("الخرطوم", "السودان", 15.5007, 32.5599, CalculationMethod.EGYPTIAN, "أفريقيا")
        val SUDAN_OMDURMAN = CityLocation("أم درمان", "السودان", 15.6500, 32.4833, CalculationMethod.EGYPTIAN, "أفريقيا")
        val SUDAN_PORT_SUDAN = CityLocation("بورتسودان", "السودان", 19.6175, 37.2164, CalculationMethod.EGYPTIAN, "أفريقيا")

        // 15. ليبيا (Libya)
        val LIBYA_TRIPOLI = CityLocation("طرابلس الغرب", "ليبيا", 32.8872, 13.1913, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val LIBYA_BENGHAZI = CityLocation("بنغازي", "ليبيا", 32.1167, 20.0667, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val LIBYA_MISRATA = CityLocation("مصراتة", "ليبيا", 32.3754, 15.0925, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")

        // 16. تونس (Tunisia)
        val TUNISIA_TUNIS = CityLocation("تونس العاصمة", "تونس", 36.8065, 10.1815, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val TUNISIA_SFAX = CityLocation("صفاقس", "تونس", 34.7406, 10.7603, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val TUNISIA_SOUSSE = CityLocation("سوسة", "تونس", 35.8256, 10.6369, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")

        // 17. الجزائر (Algeria)
        val ALGERIA_ALGIERS = CityLocation("الجزائر العاصمة", "الجزائر", 36.7538, 3.0588, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val ALGERIA_ORAN = CityLocation("وهران", "الجزائر", 35.6987, -0.6349, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val ALGERIA_CONSTANTINE = CityLocation("قسنطينة", "الجزائر", 36.3650, 6.6147, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")

        // 18. المغرب (Morocco)
        val MOROCCO_RABAT = CityLocation("الرباط", "المغرب", 34.0209, -6.8416, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val MOROCCO_CASABLANCA = CityLocation("الدار البيضاء", "المغرب", 33.5731, -7.5898, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val MOROCCO_FEZ = CityLocation("فاس", "المغرب", 34.0181, -5.0078, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")
        val MOROCCO_MARRAKESH = CityLocation("مراكش", "المغرب", 31.6295, -7.9811, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")

        // 19. موريتانيا (Mauritania)
        val MAURITANIA_NOUAKCHOTT = CityLocation("نواكشوط", "موريتانيا", 18.0735, -15.9582, CalculationMethod.MUSLIM_WORLD_LEAGUE, "المغرب العربي")

        // 20. الصومال (Somalia)
        val SOMALIA_MOGADISHU = CityLocation("مقديشو", "الصومال", 2.0469, 45.3182, CalculationMethod.MUSLIM_WORLD_LEAGUE, "أفريقيا")

        // 21. جيبوتي (Djibouti)
        val DJIBOUTI_CITY = CityLocation("مدينة جيبوتي", "جيبوتي", 11.5721, 43.1456, CalculationMethod.MUSLIM_WORLD_LEAGUE, "أفريقيا")

        // 22. جزر القمر (Comoros)
        val COMOROS_MORONI = CityLocation("موروني", "جزر القمر", -11.7172, 43.2473, CalculationMethod.MUSLIM_WORLD_LEAGUE, "أفريقيا")

        val DEFAULT_CITIES: List<CityLocation> = listOf(
            // مصر
            EGYPT_CAIRO, EGYPT_ALEXANDRIA, EGYPT_GIZA, EGYPT_MANSOURA, EGYPT_ASWAN, EGYPT_TANTA, EGYPT_PORT_SAID,
            // السعودية
            SAUDI_MAKKAH, SAUDI_MADINAH, SAUDI_RIYADH, SAUDI_JEDDAH, SAUDI_DAMMAM,
            // الإمارات
            UAE_DUBAI, UAE_ABU_DHABI, UAE_SHARJAH,
            // الكويت وقطر وعمان والبحرين
            KUWAIT_CITY, QATAR_DOHA, OMAN_MUSCAT, OMAN_SALALAH, BAHRAIN_MANAMA,
            // فلسطين والشام
            PALESTINE_JERUSALEM, PALESTINE_GAZA, PALESTINE_RAMALLAH, PALESTINE_NABLUS,
            JORDAN_AMMAN, JORDAN_IRBID, JORDAN_ZARQA,
            SYRIA_DAMASCUS, SYRIA_ALEPPO, SYRIA_HOMS,
            LEBANON_BEIRUT, LEBANON_TRIPOLI,
            // العراق
            IRAQ_BAGHDAD, IRAQ_BASRA, IRAQ_MOSUL, IRAQ_ERBIL,
            // اليمن
            YEMEN_SANAA, YEMEN_ADEN, YEMEN_TAIZ,
            // السودان
            SUDAN_KHARTOUM, SUDAN_OMDURMAN, SUDAN_PORT_SUDAN,
            // المغرب العربي (ليبيا، تونس، الجزائر، المغرب، موريتانيا)
            LIBYA_TRIPOLI, LIBYA_BENGHAZI, LIBYA_MISRATA,
            TUNISIA_TUNIS, TUNISIA_SFAX, TUNISIA_SOUSSE,
            ALGERIA_ALGIERS, ALGERIA_ORAN, ALGERIA_CONSTANTINE,
            MOROCCO_RABAT, MOROCCO_CASABLANCA, MOROCCO_FEZ, MOROCCO_MARRAKESH,
            MAURITANIA_NOUAKCHOTT,
            // القرن الأفريقي (الصومال، جيبوتي، جزر القمر)
            SOMALIA_MOGADISHU, DJIBOUTI_CITY, COMOROS_MORONI
        )
    }
}
