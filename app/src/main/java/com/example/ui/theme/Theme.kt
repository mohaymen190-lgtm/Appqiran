package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = DarkNavyBackground,
    primaryContainer = SpotifyGreen.copy(alpha = 0.2f),
    onPrimaryContainer = SpotifyGreenLight,
    secondary = GoldAccent,
    onSecondary = DarkNavyBackground,
    background = DarkNavyBackground,
    onBackground = PureWhite,
    surface = DarkNavySurface,
    onSurface = PureWhite,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkNavyElevated
)

private val LightColorScheme = lightColorScheme(
    primary = SpotifyGreen,
    onPrimary = PureWhite,
    primaryContainer = SpotifyGreen.copy(alpha = 0.15f),
    onPrimaryContainer = SpotifyGreen,
    secondary = GoldAccent,
    onSecondary = PureWhite,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun QuranAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
