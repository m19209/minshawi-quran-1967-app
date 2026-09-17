package com.minshawi.quran1967.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldCard,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldLight,
    onSecondary = TextLight,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = EmeraldSurface,
    onSurface = TextLight,
    surfaceVariant = EmeraldCard,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MinshawiQuranTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
