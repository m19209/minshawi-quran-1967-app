package com.minshawi.quran1967.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.res.painterResource
import com.minshawi.quran1967.R
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Locale

@Composable
fun PrayerCard(
    schedule: DayPrayerSchedule,
    onSelectLocationClicked: () -> Unit,
    onSimulateAzanClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Calculate formatted countdown HH:MM:SS
    val remainingSeconds = (schedule.remainingMillis / 1000).coerceAtLeast(0)
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val countdownText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(EmeraldCard, EmeraldSurface)
                )
            )
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
                        imageVector = Icons.Default.LocationOn,
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

                // Current Prayer Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "الصلاة الآن: ${schedule.currentPrayerName}",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Info: Next Prayer & Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "الصلاة القادمة",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Text(
                        text = schedule.nextPrayerName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = TextLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Digital Countdown Pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "متبقي للأذان",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Text(
                        text = countdownText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = AmberGlow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.scale(if (hours == 0L && minutes < 5) pulseScale else 1f)
                    )
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

            Spacer(modifier = Modifier.height(14.dp))

            // Azan Simulation Button (Test interruption & auto-resume immediately)
            Button(
                onClick = onSimulateAzanClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldDark.copy(alpha = 0.8f),
                    contentColor = GoldAccent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications_active),
                    contentDescription = null,
                    tint = AmberGlow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تجربة محاكاة الأذان الآن (اختبار إيقاف واستئناف التلاوة)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = GoldLight
                    )
                )
            }
        }
    }
}
