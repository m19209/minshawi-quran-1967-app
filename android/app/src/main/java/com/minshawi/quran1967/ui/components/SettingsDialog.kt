package com.minshawi.quran1967.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.batoulapps.adhan.CalculationMethod
import com.minshawi.quran1967.R
import com.minshawi.quran1967.prayer.CityLocation
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextSecondary

private data class AzanVoiceItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isDefault: Boolean = false
)

private enum class CountryCategory(val label: String) {
    ALL("الكل (22 دولة)"),
    EGYPT("مصر 🇪🇬"),
    SAUDI("السعودية 🇸🇦"),
    GULF("دول الخليج 🌴"),
    LEVANT("فلسطين والشام 🕌"),
    IRAQ("العراق 🏛️"),
    MAGHREB("المغرب العربي 🇲🇦"),
    SUDAN_YEMEN("السودان واليمن 🌍"),
    HORN("القرن الأفريقي 🧭")
}

private fun formatCalculationMethodArabic(method: CalculationMethod): String {
    return when (method) {
        CalculationMethod.EGYPTIAN -> "الهيئة المصرية العامة للمساحة"
        CalculationMethod.UMM_AL_QURA -> "جامعة أم القرى (مكة المكرمة)"
        CalculationMethod.DUBAI -> "دائرة الشؤون الإسلامية (دبي)"
        CalculationMethod.QATAR -> "وزارة الأوقاف القطرية"
        CalculationMethod.KUWAIT -> "وزارة الأوقاف (الكويت)"
        CalculationMethod.MUSLIM_WORLD_LEAGUE -> "رابطة العالم الإسلامي"
        else -> method.name
    }
}

@Composable
fun SettingsDialog(
    currentLocation: CityLocation,
    onLocationSelected: (CityLocation) -> Unit,
    onDismiss: () -> Unit,
    onSimulateAzanClicked: () -> Unit = {}
) {
    var selectedVoiceId by remember { mutableStateOf("minshawi") }
    var selectedCategory by remember { mutableStateOf(CountryCategory.ALL) }
    var citySearchQuery by remember { mutableStateOf("") }

    val azanVoices = remember {
        listOf(
            AzanVoiceItem(
                id = "minshawi",
                title = "أذان الشيخ محمد صديق المنشاوي رحمه الله",
                subtitle = "تسجيل إذاعي نقي ونادر بمقام البيات",
                isDefault = true
            ),
            AzanVoiceItem(
                id = "makkah",
                title = "أذان الحرم المكي الشريف",
                subtitle = "تسجيل الحرم المكي بأداء خاشع مهيب",
                isDefault = false
            ),
            AzanVoiceItem(
                id = "madinah",
                title = "أذان الحرم النبوي الشريف",
                subtitle = "تسجيل المسجد النبوي الشريف بأداء ندي",
                isDefault = false
            )
        )
    }

    val filteredCities = remember(selectedCategory, citySearchQuery) {
        val q = citySearchQuery.trim().lowercase()
        CityLocation.DEFAULT_CITIES.filter { city ->
            val matchesCategory = when (selectedCategory) {
                CountryCategory.ALL -> true
                CountryCategory.EGYPT -> city.countryName == "مصر"
                CountryCategory.SAUDI -> city.countryName.contains("السعودية")
                CountryCategory.GULF -> city.countryName in listOf("الإمارات العربية المتحدة", "الكويت", "قطر", "سلطنة عمان", "البحرين")
                CountryCategory.LEVANT -> city.countryName in listOf("فلسطين", "الأردن", "سوريا", "لبنان")
                CountryCategory.IRAQ -> city.countryName == "العراق"
                CountryCategory.MAGHREB -> city.countryName in listOf("المغرب", "الجزائر", "تونس", "ليبيا", "موريتانيا")
                CountryCategory.SUDAN_YEMEN -> city.countryName in listOf("السودان", "اليمن")
                CountryCategory.HORN -> city.countryName in listOf("الصومال", "جيبوتي", "جزر القمر")
            }
            val matchesSearch = q.isEmpty() ||
                city.cityName.lowercase().contains(q) ||
                city.countryName.lowercase().contains(q)

            matchesCategory && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EmeraldCard,
            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldSurface, EmeraldDark)
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark)
                                .border(1.dp, GoldAccent.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إعدادات الأذان والمواقيت",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "العالم العربي كاملاً (22 دولة)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EmeraldDark.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TextLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Active City Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.75f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_city),
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الموقع الحالي: ${currentLocation.displayName}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "طريقة الحساب: ${formatCalculationMethodArabic(currentLocation.calculationMethod)}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: City Selection & Easy Country Filter
                Text(
                    text = "اختر مدينتك من دول العالم العربي (22 دولة):",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Search Bar for Instant Filtering
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldDark)
                        .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = GoldAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (citySearchQuery.isEmpty()) {
                            Text(
                                text = "ابحث بالاسم (مثال: القدس، دبي، وهران، طرابلس...)",
                                style = TextStyle(color = TextSecondary, fontSize = 12.sp)
                            )
                        }
                        BasicTextField(
                            value = citySearchQuery,
                            onValueChange = { citySearchQuery = it },
                            textStyle = TextStyle(color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(GoldAccent),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (citySearchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { citySearchQuery = "" },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "مسح",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Country Category Chips for Quick 1-Tap Access
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountryCategory.values().forEach { category ->
                        val isCatSelected = category == selectedCategory
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isCatSelected) GoldAccent else EmeraldDark.copy(alpha = 0.8f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCatSelected) GoldAccent else GoldAccent.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isCatSelected) EmeraldDark else TextLight,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cities List (Filtered & Beautifully Spaced without cutoffs)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.4f))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (filteredCities.isEmpty()) {
                        Text(
                            text = "لا توجد نتائج مطابقة للبحث",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        filteredCities.forEach { city ->
                            val isCitySelected = (city.cityName == currentLocation.cityName && city.countryName == currentLocation.countryName)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isCitySelected) GoldAccent.copy(alpha = 0.2f) else EmeraldSurface
                                    )
                                    .border(
                                        width = if (isCitySelected) 1.dp else 0.dp,
                                        color = if (isCitySelected) GoldAccent else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onLocationSelected(city) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_location_city),
                                        contentDescription = null,
                                        tint = if (isCitySelected) GoldAccent else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = city.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (isCitySelected) GoldLight else TextLight,
                                                fontWeight = if (isCitySelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        Text(
                                            text = "طريقة الحساب: ${formatCalculationMethodArabic(city.calculationMethod)}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                        )
                                    }
                                }

                                if (isCitySelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Azan Voice Selector
                Text(
                    text = "اختر صوت الأذان المفضل:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    azanVoices.forEach { voice ->
                        val isVoiceSelected = voice.id == selectedVoiceId

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isVoiceSelected) GoldAccent.copy(alpha = 0.16f) else EmeraldSurface
                                )
                                .border(
                                    width = if (isVoiceSelected) 1.5.dp else 0.8.dp,
                                    color = if (isVoiceSelected) GoldAccent else EmeraldDark.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedVoiceId = voice.id }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Dedicated isolated RadioButton slot - impossible to collide with badges
                            RadioButton(
                                selected = isVoiceSelected,
                                onClick = { selectedVoiceId = voice.id },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldAccent,
                                    unselectedColor = TextSecondary
                                ),
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = voice.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isVoiceSelected) GoldLight else TextLight,
                                            fontWeight = if (isVoiceSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                    if (voice.isDefault) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GoldAccent.copy(alpha = 0.22f),
                                            border = BorderStroke(0.5.dp, GoldAccent)
                                        ) {
                                            Text(
                                                text = "الافتراضي",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GoldLight,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = voice.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test / Simulate Azan Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.75f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            onDismiss()
                            onSimulateAzanClicked()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AmberGlow.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications_active),
                                contentDescription = null,
                                tint = AmberGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "تجربة محاكاة الأذان المختار الآن",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                            Text(
                                text = "اختبار ميزة إيقاف واستئناف التلاوة تلقائياً",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_play_arrow),
                        contentDescription = "تشغيل المحاكاة",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save and Close Footer Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "حفظ وإغلاق",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    )
                }
            }
        }
    }
}
