package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealGreenLight,
    onPrimary = NavySlate,
    primaryContainer = TealGreen,
    onPrimaryContainer = Color.White,
    secondary = VibrantBlueLight,
    onSecondary = Color.White,
    secondaryContainer = Slate700,
    onSecondaryContainer = Slate100,
    tertiary = GoldLight,
    onTertiary = NavySlate,
    tertiaryContainer = GoldEarnings,
    onTertiaryContainer = Color.White,
    background = Color(0xFF090D16),
    onBackground = Slate100,
    surface = NavySlate,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate200,
    outline = Slate700,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TealGreen,
    onPrimary = Color.White,
    primaryContainer = TealGreenBg,
    onPrimaryContainer = NavySlate,
    secondary = VibrantBlue,
    onSecondary = Color.White,
    secondaryContainer = VibrantBlueBg,
    onSecondaryContainer = NavySlate,
    tertiary = GoldEarnings,
    onTertiary = Color.White,
    tertiaryContainer = GoldBg,
    onTertiaryContainer = NavySlate,
    background = Slate50,
    onBackground = NavySlate,
    surface = Color.White,
    onSurface = NavySlate,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional brand theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

