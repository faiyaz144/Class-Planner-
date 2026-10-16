package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClassCueDarkColorScheme = darkColorScheme(
  primary = AccentLilac,
  onPrimary = Color.Black,
  primaryContainer = AccentLilacDark,
  onPrimaryContainer = AccentLilacLight,
  secondary = Color(0xFF6366F1),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF1E1F35),
  onSecondaryContainer = Color(0xFFC7D2FE),
  tertiary = Color(0xFF38BDF8),
  background = DarkAppBackground,
  onBackground = TextPrimaryWhite,
  surface = DarkSurfaceCard,
  onSurface = TextPrimaryWhite,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondaryGray,
  outline = DarkBorder,
  outlineVariant = Color(0xFF1D1F30)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ClassCueDarkColorScheme,
    typography = Typography,
    content = content
  )
}
