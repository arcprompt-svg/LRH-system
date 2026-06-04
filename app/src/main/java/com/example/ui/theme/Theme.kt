package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = LRH_Accent,
    secondary = LRH_Blue,
    tertiary = LRH_Purple,
    background = LRH_Bg,
    surface = LRH_Surface,
    onPrimary = LRH_Bg,
    onSecondary = LRH_Bg,
    onTertiary = LRH_Bg,
    onBackground = LRH_Text,
    onSurface = LRH_Text,
    surfaceVariant = LRH_Surface2,
    onSurfaceVariant = LRH_Text2,
    outline = LRH_Border
  )

@Composable
fun LRHSystemTheme(
  darkTheme: Boolean = true, // Force dark theme vibe as default
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
