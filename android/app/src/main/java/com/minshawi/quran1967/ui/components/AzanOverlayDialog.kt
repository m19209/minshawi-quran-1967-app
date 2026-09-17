package com.minshawi.quran1967.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.res.painterResource
import com.minshawi.quran1967.R
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextSecondary

@Composable
fun AzanOverlayDialog(
    prayerName: String,
    onStopAzanAndResume: () -> Unit
) {
    val pulseAnim = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Dialog(onDismissRequest = { /* Keep active until finished or user taps button */ }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EmeraldCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, GoldAccent, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldSurface, EmeraldDark)
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsing Azan Bell / Minaret icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseAnim.value)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f))
                        .border(2.dp, GoldAccent, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications_active),
                        contentDescription = "الأذان",
                        tint = AmberGlow,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "الله أكبر .. الله أكبر",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "حان الآن موعد أذان $prayerName",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextLight,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "بصوت الشيخ محمد صديق المنشاوي",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GoldLight)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Information banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.6f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "تم إيقاف التلاوة مؤقتاً لحين انتهاء الأذان، وستستأنف تلقائياً من نفس اللحظة فور انتهائه دون أي تدخل منك.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button: Stop Azan & Resume Quran Immediately
                Button(
                    onClick = onStopAzanAndResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "إيقاف الأذان واستئناف التلاوة الآن",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
