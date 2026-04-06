package com.vjpro.tindow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NutriMealColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = WhiteCard,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = GreenLight,
    onSecondary = WhiteCard,
    secondaryContainer = GreenSurface,
    onSecondaryContainer = GreenDark,
    background = BeigeBackground,
    onBackground = TextPrimary,
    surface = WhiteCard,
    onSurface = TextPrimary,
    surfaceVariant = GreenSurface,
    onSurfaceVariant = TextSecondary,
    outline = TextSecondary
)

@Composable
fun TindowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NutriMealColorScheme,
        typography = Typography,
        content = content
    )
}
