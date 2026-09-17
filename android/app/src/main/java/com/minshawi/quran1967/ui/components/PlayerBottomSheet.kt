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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.minshawi.quran1967.R
import com.minshawi.quran1967.audio.AudioPlaybackManager
import com.minshawi.quran1967.audio.RepeatMode
import com.minshawi.quran1967.data.Surah
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun PlayerBottomSheet(
    surah: Surah,
    isPlaying: Boolean,
    isLoading: Boolean,
    onPlayPauseClicked: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNextClicked: () -> Unit,
    onPrevClicked: () -> Unit,
    onRepeatClicked: () -> Unit,
    onDownloadClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentPosition by AudioPlaybackManager.currentPosition.collectAsState()
    val duration by AudioPlaybackManager.duration.collectAsState()
    val repeatMode by AudioPlaybackManager.repeatMode.collectAsState()

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(0f) }

    val displayedPosition = if (isDragging && duration > 0) {
        (dragProgress * duration).toLong()
    } else {
        currentPosition
    }

    val sliderValue = if (duration > 0) {
        if (isDragging) dragProgress else (currentPosition.toFloat() / duration)
    } else 0f

    val sheetBgBrush = remember {
        Brush.verticalGradient(
            listOf(EmeraldCard, EmeraldDark)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(sheetBgBrush)
            .border(1.dp, GoldAccent.copy(alpha = 0.2f), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GoldAccent.copy(alpha = 0.4f))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Badge: 1967 Pure Edition
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(GoldAccent.copy(alpha = 0.15f))
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "ختمة 1967 النقية النادرة",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Clean static header badge (no rotating element)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GoldAccent.copy(alpha = 0.15f))
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%03d", surah.number),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${surah.ayahCount} آية",
                    style = MaterialTheme.typography.labelSmall.copy(color = GoldLight.copy(alpha = 0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Surah details
        Text(
            text = "سورة ${surah.arabicName} (${surah.englishName})",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = TextLight,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "القارئ الشيخ محمد صديق المنشاوي - ${if (surah.isMakki) "مكية" else "مدنية"}",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Equalizer / Sound Wave (ترددات صوتية حية متحركة)
        AnimatedEqualizer(
            isPlaying = isPlaying,
            barColor = GoldAccent,
            barWidth = 3.2.dp,
            modifier = Modifier.size(48.dp, 22.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Seek Bar (Ultra-smooth dragging & instant seek response)
        Slider(
            value = sliderValue.coerceIn(0f, 1f),
            onValueChange = { percent ->
                isDragging = true
                dragProgress = percent
            },
            onValueChangeFinished = {
                val newPos = (dragProgress * duration).toLong()
                onSeekTo(newPos)
                isDragging = false
            },
            colors = SliderDefaults.colors(
                thumbColor = GoldAccent,
                activeTrackColor = GoldAccent,
                inactiveTrackColor = EmeraldSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Time indicators: Elapsed counts up (00:00...), Remaining counts down smoothly
        val remainingTime = if (duration > 0) (duration - displayedPosition).coerceAtLeast(0L) else 0L
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(displayedPosition),
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
            Text(
                text = formatDuration(remainingTime),
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Player Controls: Prev, SeekBack, Play/Pause, SeekForward, Next (Fixed standard LTR ordering)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Surah
                IconButton(onClick = onPrevClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_previous),
                        contentDescription = "السورة السابقة",
                        tint = TextLight,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Seek Backward 10s
                IconButton(onClick = { AudioPlaybackManager.seekBackward(10000L) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_fast_rewind),
                        contentDescription = "ترجيع 10 ثواني",
                        tint = GoldLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Central Play / Pause Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AmberGlow, GoldAccent)
                            )
                        )
                        .clickable { onPlayPauseClicked() }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = EmeraldDark,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow),
                            contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                            tint = EmeraldDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Seek Forward 10s
                IconButton(onClick = { AudioPlaybackManager.seekForward(10000L) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_fast_forward),
                        contentDescription = "تقديم 10 ثواني",
                        tint = GoldLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Next Surah
                IconButton(onClick = onNextClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_next),
                        contentDescription = "السورة التالية",
                        tint = TextLight,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom Extras: Repeat Mode & Download Recitation Locally (Symmetrical, Elegant, and Spacious)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Repeat Toggle Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldSurface)
                    .border(
                        width = 1.dp,
                        color = if (repeatMode != RepeatMode.OFF) GoldAccent.copy(alpha = 0.5f) else GoldAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onRepeatClicked() }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Icon(
                    painter = painterResource(if (repeatMode == RepeatMode.ONE) R.drawable.ic_repeat_one else R.drawable.ic_repeat),
                    contentDescription = "التكرار",
                    tint = if (repeatMode != RepeatMode.OFF) GoldAccent else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (repeatMode) {
                        RepeatMode.OFF -> "تكرار: معطل"
                        RepeatMode.ALL -> "تكرار: الكل"
                        RepeatMode.ONE -> "تكرار: السورة"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (repeatMode != RepeatMode.OFF) GoldLight else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Download Locally Button (تحميل التلاوة على الجهاز)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldSurface)
                    .border(
                        width = 1.dp,
                        color = GoldAccent.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDownloadClicked() }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "تحميل التلاوة على الجهاز",
                    tint = GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تحميل السورة",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
