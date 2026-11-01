package com.example.mypocketguide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink, onPrimary = White, secondary = Blue,
    background = Background, onBackground = Ink,
    surface = White, onSurface = Ink,
    surfaceVariant = Ghost, onSurfaceVariant = Gray
)

@Composable
fun MyPocketGuideTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = MyPocketGuideTypography, content = content)
}