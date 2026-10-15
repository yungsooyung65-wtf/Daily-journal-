package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DailyJournalColorScheme = lightColorScheme(
    primary = PastelPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelPinkBackground,
    onPrimaryContainer = PastelPrimaryDark,
    secondary = PastelSecondary,
    onSecondary = Color.White,
    secondaryContainer = PastelLavenderAccent,
    onSecondaryContainer = CuteDarkText,
    tertiary = PastelTertiary,
    onTertiary = Color.White,
    background = PastelPinkBackground,
    onBackground = CuteDarkText,
    surface = PastelCardBackground,
    onSurface = CuteDarkText,
    surfaceVariant = PastelSurface,
    onSurfaceVariant = CuteSubtleText,
    outline = Color(0xFFECDCE3)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DailyJournalColorScheme,
        typography = AppTypography,
        content = content
    )
}
