package com.minshawi.quran1967.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.minshawi.quran1967.ui.theme.GoldAccent

/**
 * Animated Sound Wave Equalizer (علامة ترددات صوتية حية)
 *
 * Highly optimized:
 * - Uses Canvas drawRoundRect directly, avoiding expensive layout passes.
 * - When isPlaying == false, 0 animation is running (0% CPU, static resting heights).
 * - When isPlaying == true, 5 bars oscillate smoothly with varied durations and phase shifts.
 */
@Composable
fun AnimatedEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = GoldAccent,
    barWidth: Dp = 3.dp
) {
    if (!isPlaying) {
        // Resting state: classic stationary frequencies shape (like ic_graphic_eq)
        Canvas(modifier = modifier) {
            val totalW = size.width
            val totalH = size.height
            val w = barWidth.toPx()
            val r = CornerRadius(w / 2f, w / 2f)
            val restingFractions = floatArrayOf(0.25f, 0.65f, 0.95f, 0.65f, 0.25f)
            val count = restingFractions.size
            val spacing = if (count > 1) (totalW - (count * w)) / (count - 1) else 0f

            for (i in 0 until count) {
                val h = (totalH * restingFractions[i]).coerceAtLeast(w)
                val x = i * (w + spacing)
                val y = (totalH - h) / 2f
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(w, h),
                    cornerRadius = r
                )
            }
        }
    } else {
        // Active playback state: 5 dynamic pulsing frequencies
        val transition = rememberInfiniteTransition(label = "equalizerTransition")

        val b1 by transition.animateFloat(
            initialValue = 0.20f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 420, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "b1"
        )
        val b2 by transition.animateFloat(
            initialValue = 0.75f,
            targetValue = 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 340, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "b2"
        )
        val b3 by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "b3"
        )
        val b4 by transition.animateFloat(
            initialValue = 0.85f,
            targetValue = 0.30f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "b4"
        )
        val b5 by transition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.70f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "b5"
        )

        Canvas(modifier = modifier) {
            val totalW = size.width
            val totalH = size.height
            val w = barWidth.toPx()
            val r = CornerRadius(w / 2f, w / 2f)
            val fractions = floatArrayOf(b1, b2, b3, b4, b5)
            val count = fractions.size
            val spacing = if (count > 1) (totalW - (count * w)) / (count - 1) else 0f

            for (i in 0 until count) {
                val h = (totalH * fractions[i]).coerceAtLeast(w)
                val x = i * (w + spacing)
                val y = (totalH - h) / 2f
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(w, h),
                    cornerRadius = r
                )
            }
        }
    }
}
