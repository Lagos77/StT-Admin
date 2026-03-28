package com.example.stadmin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Navy700,
    onPrimary = CardWhite,
    primaryContainer = Navy500,
    onPrimaryContainer = CardWhite,
    secondary = Brown500,
    onSecondary = CardWhite,
    secondaryContainer = Brown300,
    onSecondaryContainer = Charcoal,
    background = WarmWhite,
    onBackground = Charcoal,
    surface = CardWhite,
    onSurface = Charcoal,
    surfaceVariant = WarmGray,
    onSurfaceVariant = GrayMid,
    outline = GrayMuted,
)

@Composable
fun STAdminTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}