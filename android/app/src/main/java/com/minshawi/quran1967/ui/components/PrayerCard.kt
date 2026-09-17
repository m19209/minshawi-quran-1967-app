package com.minshawi.quran1967.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minshawi.quran1967.R
import com.minshawi.quran1967.prayer.DayPrayerSchedule
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.CardBorder
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldLight
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextMuted
import com.minshawi.quran1967.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

fun getFormattedHijriDate(): String {
    return try {
        val hijrahDate = java.time.chrono.HijrahDate.now()
        val day = hijrahDate.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
        val month = hijrahDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
        val year = hijrahDate.get(java.time.temporal.ChronoField.YEAR)

        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = if (month in 1..12) hijriMonths[month - 1] else "هـ"
        "$day $monthName $year هـ"
    } catch (e: Exception) {
        "التقويم الهجري"
    }
}

@Composable
fun PrayerCard(
    schedule: DayPrayerSchedule,
    onSelectLocationClicked: () -> Unit,
    onSimulateAzanClicked: () -> Unit = {},
    onRefreshSchedule: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 1-second reactive ticker for the countdown
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(schedule.nextPrayerTime) {
        while (isActive) {
            delay(1000L)
            val updated = System.currentTimeMillis()
            nowMillis = updated
            if (updated >= schedule.nextPrayerTime.time) {
                onRefreshSchedule()
            }
        }
    }

    val remainingMillis = (schedule.nextPrayerTime.time - nowMillis).coerceAtLeast(0L)
    val remainingSeconds = remainingMillis / 1000
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val countdownText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    val cardBrush = remember {
        Brush.verticalGradient(
            colors = listOf(EmeraldCard, EmeraldSurface)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBrush)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Top Row: Location & Change button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.5f))
                        .clickable { onSelectLocationClicked() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_city),
                        contentDescription = "الدولة والمدينة",
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = schedule.location.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Hijri Calendar Date Badge (التقويم الهجري)
                val hijriDateText = remember { getFormattedHijriDate() }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAccent.copy(alpha = 0.16f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = hijriDateText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Info: Next Prayer & Countdown (كبير وواضح للمستخدم)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDark.copy(alpha = 0.75f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الصلاة القادمة",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = schedule.nextPrayerName,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = TextLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp
                            )
                        )
                    }

                    // Digital Countdown (كبير وواضح)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "الوقت المتبقي للأذان",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = AmberGlow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5 Prayers Horizontal Pill Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                schedule.items.forEach { item ->
                    val isNext = item.isNext
                    val isCurrent = item.isCurrent

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isNext -> GoldAccent.copy(alpha = 0.25f)
                                    isCurrent -> EmeraldLight.copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isNext) 1.dp else 0.dp,
                                color = if (isNext) GoldAccent else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = item.arabicName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isNext) GoldLight else TextMuted,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.formattedTime,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 11.sp,
                                color = if (isNext) TextLight else TextSecondary,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}
