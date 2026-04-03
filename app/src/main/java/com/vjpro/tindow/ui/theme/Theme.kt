package com.vjpro.tindow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PastelPurple,
    onPrimary = Color.White,
    primaryContainer = PastelLavender.copy(alpha = 0.3f),
    onPrimaryContainer = PastelTextPrimary,
    secondary = PastelLavender,
    onSecondary = PastelTextPrimary,
    secondaryContainer = PastelBlue.copy(alpha = 0.3f),
    onSecondaryContainer = PastelTextPrimary,
    tertiary = PastelPink,
    onTertiary = Color.White,
    tertiaryContainer = PastelPink.copy(alpha = 0.2f),
    onTertiaryContainer = PastelTextPrimary,
    background = PastelBgLight,
    onBackground = PastelTextPrimary,
    surface = PastelSurface,
    onSurface = PastelTextPrimary,
    surfaceVariant = PastelBgGradientEnd,
    onSurfaceVariant = PastelTextSecondary,
    error = PastelCoral,
    onError = Color.White,
    errorContainer = PastelCoral.copy(alpha = 0.2f),
    onErrorContainer = PastelTextPrimary,
    outline = PastelLavender.copy(alpha = 0.5f),
    inverseSurface = PastelTextPrimary,
    inverseOnSurface = PastelBgLight,
    inversePrimary = PastelLavender
)

private val DarkColorScheme = darkColorScheme(
    primary = PastelLavender,
    onPrimary = PastelTextPrimary,
    primaryContainer = PastelPurple.copy(alpha = 0.4f),
    onPrimaryContainer = PastelBgLight,
    secondary = PastelBlue,
    onSecondary = PastelTextPrimary,
    secondaryContainer = PastelBlue.copy(alpha = 0.3f),
    onSecondaryContainer = PastelBgLight,
    tertiary = PastelPink,
    onTertiary = Color.White,
    tertiaryContainer = PastelPink.copy(alpha = 0.3f),
    onTertiaryContainer = PastelBgLight,
    background = Color(0xFF1A1625),
    onBackground = PastelBgLight,
    surface = Color(0xFF221D30),
    onSurface = PastelBgLight,
    surfaceVariant = Color(0xFF2D2642),
    onSurfaceVariant = PastelLavender,
    error = PastelCoral,
    onError = Color.White,
    outline = PastelPurple.copy(alpha = 0.5f)
)

@Composable
fun TindowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
