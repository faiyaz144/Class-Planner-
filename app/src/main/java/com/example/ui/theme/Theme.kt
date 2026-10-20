package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeCategory(val label: String) {
  ALL("All"),
  DARK("Dark & OLED"),
  LIGHT("Light & Clean")
}

enum class AppThemeMode(
  val id: String,
  val displayName: String,
  val subtitle: String,
  val accentColor: Color,
  val previewBackground: Color,
  val isDark: Boolean,
  val category: ThemeCategory = if (isDark) ThemeCategory.DARK else ThemeCategory.LIGHT,
  val badgeTag: String? = null
) {
  MIDNIGHT_OBSIDIAN(
    id = "midnight_obsidian",
    displayName = "Midnight Obsidian",
    subtitle = "Deep obsidian & neon lilac",
    accentColor = Color(0xFF9E86EB),
    previewBackground = Color(0xFF090A10),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "POPULAR"
  ),
  OLED_PITCH_BLACK(
    id = "oled_pitch_black",
    displayName = "OLED Pitch Black",
    subtitle = "Pure #000000 black & electric violet",
    accentColor = Color(0xFFA855F7),
    previewBackground = Color(0xFF000000),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "OLED"
  ),
  CYBER_EMERALD(
    id = "cyber_emerald",
    displayName = "Cyber Emerald",
    subtitle = "High-tech carbon & neon emerald",
    accentColor = Color(0xFF10B981),
    previewBackground = Color(0xFF060B08),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "VIBRANT"
  ),
  NORDIC_FROST(
    id = "nordic_frost",
    displayName = "Nordic Frost",
    subtitle = "Deep navy slate & icy cyan",
    accentColor = Color(0xFF38BDF8),
    previewBackground = Color(0xFF090F1A),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "COOL"
  ),
  SYNTHWAVE_NEON(
    id = "synthwave_neon",
    displayName = "Synthwave 80s",
    subtitle = "Arcade noir, magenta & electric cyan",
    accentColor = Color(0xFFFF2A85),
    previewBackground = Color(0xFF0D0618),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "RETRO"
  ),
  SUNSET_AMBER(
    id = "sunset_amber",
    displayName = "Sunset Amber",
    subtitle = "Warm espresso & amber gold",
    accentColor = Color(0xFFF59E0B),
    previewBackground = Color(0xFF120B07),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "WARM"
  ),
  TOKYO_SAKURA(
    id = "tokyo_sakura",
    displayName = "Tokyo Sakura",
    subtitle = "Plum noir & blossom rose",
    accentColor = Color(0xFFF472B6),
    previewBackground = Color(0xFF120815),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "SWEET"
  ),
  MATCHA_BOTANICAL(
    id = "matcha_botanical",
    displayName = "Matcha Botanical",
    subtitle = "Deep cedar forest & soothing matcha",
    accentColor = Color(0xFF4ADE80),
    previewBackground = Color(0xFF09120B),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "CALM"
  ),
  CRIMSON_ROYALE(
    id = "crimson_royale",
    displayName = "Crimson Royale",
    subtitle = "Velvet mahogany noir & radiant ruby",
    accentColor = Color(0xFFF43F5E),
    previewBackground = Color(0xFF13070A),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "LUXE"
  ),
  SOLAR_FLAME(
    id = "solar_flame",
    displayName = "Solar Flame",
    subtitle = "Volcanic charcoal & electric tangerine",
    accentColor = Color(0xFFFF6B35),
    previewBackground = Color(0xFF100B08),
    isDark = true,
    category = ThemeCategory.DARK,
    badgeTag = "ENERGY"
  ),
  PAPER_MINIMAL(
    id = "paper_minimal",
    displayName = "Paper Minimal",
    subtitle = "Crisp daylight slate & royal indigo",
    accentColor = Color(0xFF4F46E5),
    previewBackground = Color(0xFFF8FAFC),
    isDark = false,
    category = ThemeCategory.LIGHT,
    badgeTag = "CLEAN"
  ),
  PASTEL_BLOSSOM(
    id = "pastel_blossom",
    displayName = "Pastel Blossom",
    subtitle = "Warm latte ivory, coral & terracotta",
    accentColor = Color(0xFFE11D48),
    previewBackground = Color(0xFFFFFBF7),
    isDark = false,
    category = ThemeCategory.LIGHT,
    badgeTag = "WARM"
  ),
  ARCTIC_DAYLIGHT(
    id = "arctic_daylight",
    displayName = "Arctic Daylight",
    subtitle = "Glacier frosted white & sky cyan",
    accentColor = Color(0xFF0284C7),
    previewBackground = Color(0xFFF0F9FF),
    isDark = false,
    category = ThemeCategory.LIGHT,
    badgeTag = "FRESH"
  ),
  CREAM_COFFEE(
    id = "cream_coffee",
    displayName = "Café Macchiato",
    subtitle = "Warm cappuccino cream & rich mocha amber",
    accentColor = Color(0xFFB45309),
    previewBackground = Color(0xFFFDFBF7),
    isDark = false,
    category = ThemeCategory.LIGHT,
    badgeTag = "COZY"
  );

  companion object {
    fun fromId(id: String?): AppThemeMode {
      return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MIDNIGHT_OBSIDIAN
    }
  }
}

data class AppThemeColors(
  val mode: AppThemeMode,
  val primary: Color,
  val primaryContainer: Color,
  val onPrimary: Color,
  val onPrimaryContainer: Color,
  val secondary: Color,
  val onSecondary: Color,
  val secondaryContainer: Color,
  val onSecondaryContainer: Color,
  val tertiary: Color,
  val background: Color,
  val onBackground: Color,
  val surface: Color,
  val onSurface: Color,
  val surfaceVariant: Color,
  val onSurfaceVariant: Color,
  val surfaceCapsule: Color,
  val outline: Color,
  val outlineVariant: Color,
  val isDark: Boolean
)

fun getThemeColors(
  mode: AppThemeMode,
  forceOledBlack: Boolean = false,
  accentOverrideHex: String? = null
): AppThemeColors {
  val baseColors = when (mode) {
    AppThemeMode.MIDNIGHT_OBSIDIAN -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF9E86EB),
      primaryContainer = Color(0xFF282548),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFD6CBFA),
      secondary = Color(0xFF6366F1),
      onSecondary = Color.White,
      secondaryContainer = Color(0xFF1E1F35),
      onSecondaryContainer = Color(0xFFC7D2FE),
      tertiary = Color(0xFF38BDF8),
      background = Color(0xFF090A10),
      onBackground = Color(0xFFF9FAFB),
      surface = Color(0xFF13141F),
      onSurface = Color(0xFFF9FAFB),
      surfaceVariant = Color(0xFF181928),
      onSurfaceVariant = Color(0xFF9499B0),
      surfaceCapsule = Color(0xFF161724),
      outline = Color(0xFF232538),
      outlineVariant = Color(0xFF1D1F30),
      isDark = true
    )
    AppThemeMode.OLED_PITCH_BLACK -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFA855F7),
      primaryContainer = Color(0xFF2E1065),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFE9D5FF),
      secondary = Color(0xFFC084FC),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF1E0B3C),
      onSecondaryContainer = Color(0xFFF3E8FF),
      tertiary = Color(0xFF38BDF8),
      background = Color(0xFF000000),
      onBackground = Color(0xFFFFFFFF),
      surface = Color(0xFF090909),
      onSurface = Color(0xFFFFFFFF),
      surfaceVariant = Color(0xFF121212),
      onSurfaceVariant = Color(0xFFA3A3A3),
      surfaceCapsule = Color(0xFF0E0E0E),
      outline = Color(0xFF262626),
      outlineVariant = Color(0xFF1A1A1A),
      isDark = true
    )
    AppThemeMode.CYBER_EMERALD -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF10B981),
      primaryContainer = Color(0xFF093725),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFA7F3D0),
      secondary = Color(0xFF14B8A6),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF112922),
      onSecondaryContainer = Color(0xFF99F6E4),
      tertiary = Color(0xFF06B6D4),
      background = Color(0xFF060B08),
      onBackground = Color(0xFFF0FDF4),
      surface = Color(0xFF0E1712),
      onSurface = Color(0xFFF0FDF4),
      surfaceVariant = Color(0xFF14221A),
      onSurfaceVariant = Color(0xFF86EFAC),
      surfaceCapsule = Color(0xFF0F1C14),
      outline = Color(0xFF1D3528),
      outlineVariant = Color(0xFF13281E),
      isDark = true
    )
    AppThemeMode.NORDIC_FROST -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF38BDF8),
      primaryContainer = Color(0xFF0C2B45),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFBAE6FD),
      secondary = Color(0xFF60A5FA),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF132238),
      onSecondaryContainer = Color(0xFFBFDBFE),
      tertiary = Color(0xFF818CF8),
      background = Color(0xFF090F1A),
      onBackground = Color(0xFFF0F9FF),
      surface = Color(0xFF111A2C),
      onSurface = Color(0xFFF0F9FF),
      surfaceVariant = Color(0xFF17243C),
      onSurfaceVariant = Color(0xFF93C5FD),
      surfaceCapsule = Color(0xFF131F35),
      outline = Color(0xFF1E3A5F),
      outlineVariant = Color(0xFF152A44),
      isDark = true
    )
    AppThemeMode.SYNTHWAVE_NEON -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFFF2A85),
      primaryContainer = Color(0xFF4A0A26),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFFFFD1E4),
      secondary = Color(0xFF00F0FF),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF00383D),
      onSecondaryContainer = Color(0xFFB3FBFF),
      tertiary = Color(0xFFA855F7),
      background = Color(0xFF0D0618),
      onBackground = Color(0xFFFFF1F7),
      surface = Color(0xFF190C2C),
      onSurface = Color(0xFFFFF1F7),
      surfaceVariant = Color(0xFF24103E),
      onSurfaceVariant = Color(0xFFF472B6),
      surfaceCapsule = Color(0xFF1F0D35),
      outline = Color(0xFF3E1964),
      outlineVariant = Color(0xFF2C1147),
      isDark = true
    )
    AppThemeMode.SUNSET_AMBER -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFF59E0B),
      primaryContainer = Color(0xFF3D2403),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFFDE68A),
      secondary = Color(0xFFFB923C),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF331D10),
      onSecondaryContainer = Color(0xFFFED7AA),
      tertiary = Color(0xFFF87171),
      background = Color(0xFF120B07),
      onBackground = Color(0xFFFFFBEB),
      surface = Color(0xFF1C130D),
      onSurface = Color(0xFFFFFBEB),
      surfaceVariant = Color(0xFF291B12),
      onSurfaceVariant = Color(0xFFFCD34D),
      surfaceCapsule = Color(0xFF22170F),
      outline = Color(0xFF3D291B),
      outlineVariant = Color(0xFF2B1C12),
      isDark = true
    )
    AppThemeMode.TOKYO_SAKURA -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFF472B6),
      primaryContainer = Color(0xFF3D0E25),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFFBCFE8),
      secondary = Color(0xFFE879F9),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF301036),
      onSecondaryContainer = Color(0xFFF5D0FE),
      tertiary = Color(0xFFA78BFA),
      background = Color(0xFF120815),
      onBackground = Color(0xFFFDF2F8),
      surface = Color(0xFF1C0D20),
      onSurface = Color(0xFFFDF2F8),
      surfaceVariant = Color(0xFF29132E),
      onSurfaceVariant = Color(0xFFF9A8D4),
      surfaceCapsule = Color(0xFF231028),
      outline = Color(0xFF3E1C45),
      outlineVariant = Color(0xFF2B1230),
      isDark = true
    )
    AppThemeMode.MATCHA_BOTANICAL -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF4ADE80),
      primaryContainer = Color(0xFF0F381B),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFBBF7D0),
      secondary = Color(0xFF2DD4BF),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF0D332D),
      onSecondaryContainer = Color(0xFF99F6E4),
      tertiary = Color(0xFFA3E635),
      background = Color(0xFF09120B),
      onBackground = Color(0xFFF0FDF4),
      surface = Color(0xFF111F15),
      onSurface = Color(0xFFF0FDF4),
      surfaceVariant = Color(0xFF182A1D),
      onSurfaceVariant = Color(0xFF86EFAC),
      surfaceCapsule = Color(0xFF142419),
      outline = Color(0xFF22422C),
      outlineVariant = Color(0xFF183220),
      isDark = true
    )
    AppThemeMode.CRIMSON_ROYALE -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFF43F5E),
      primaryContainer = Color(0xFF4C0519),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFFFFE4E6),
      secondary = Color(0xFFFB7185),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF370714),
      onSecondaryContainer = Color(0xFFFECDD3),
      tertiary = Color(0xFFFB923C),
      background = Color(0xFF13070A),
      onBackground = Color(0xFFFFF1F2),
      surface = Color(0xFF1C0D12),
      onSurface = Color(0xFFFFF1F2),
      surfaceVariant = Color(0xFF27131A),
      onSurfaceVariant = Color(0xFFFDA4AF),
      surfaceCapsule = Color(0xFF200F15),
      outline = Color(0xFF451925),
      outlineVariant = Color(0xFF301019),
      isDark = true
    )
    AppThemeMode.SOLAR_FLAME -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFFF6B35),
      primaryContainer = Color(0xFF4A1A05),
      onPrimary = Color.Black,
      onPrimaryContainer = Color(0xFFFFD8CC),
      secondary = Color(0xFFFFA000),
      onSecondary = Color.Black,
      secondaryContainer = Color(0xFF3D2300),
      onSecondaryContainer = Color(0xFFFFECB3),
      tertiary = Color(0xFFEF4444),
      background = Color(0xFF100B08),
      onBackground = Color(0xFFFFF7ED),
      surface = Color(0xFF1A130F),
      onSurface = Color(0xFFFFF7ED),
      surfaceVariant = Color(0xFF251B15),
      onSurfaceVariant = Color(0xFFFDBA74),
      surfaceCapsule = Color(0xFF1F1612),
      outline = Color(0xFF3D291E),
      outlineVariant = Color(0xFF2A1C14),
      isDark = true
    )
    AppThemeMode.PAPER_MINIMAL -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF4F46E5),
      primaryContainer = Color(0xFFEEF2FF),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFF312E81),
      secondary = Color(0xFF0284C7),
      onSecondary = Color.White,
      secondaryContainer = Color(0xFFE0F2FE),
      onSecondaryContainer = Color(0xFF0369A1),
      tertiary = Color(0xFF059669),
      background = Color(0xFFF8FAFC),
      onBackground = Color(0xFF0F172A),
      surface = Color(0xFFFFFFFF),
      onSurface = Color(0xFF0F172A),
      surfaceVariant = Color(0xFFF1F5F9),
      onSurfaceVariant = Color(0xFF475569),
      surfaceCapsule = Color(0xFFE2E8F0),
      outline = Color(0xFFCBD5E1),
      outlineVariant = Color(0xFFE2E8F0),
      isDark = false
    )
    AppThemeMode.PASTEL_BLOSSOM -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFE11D48),
      primaryContainer = Color(0xFFFFE4E6),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFF881337),
      secondary = Color(0xFFEA580C),
      onSecondary = Color.White,
      secondaryContainer = Color(0xFFFFEDD5),
      onSecondaryContainer = Color(0xFF9A3412),
      tertiary = Color(0xFF9333EA),
      background = Color(0xFFFFFBF7),
      onBackground = Color(0xFF291417),
      surface = Color(0xFFFFFFFF),
      onSurface = Color(0xFF291417),
      surfaceVariant = Color(0xFFFDF2F4),
      onSurfaceVariant = Color(0xFF6E4048),
      surfaceCapsule = Color(0xFFFCE7EA),
      outline = Color(0xFFF3CBD1),
      outlineVariant = Color(0xFFFAE0E4),
      isDark = false
    )
    AppThemeMode.ARCTIC_DAYLIGHT -> AppThemeColors(
      mode = mode,
      primary = Color(0xFF0284C7),
      primaryContainer = Color(0xFFE0F2FE),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFF075985),
      secondary = Color(0xFF0D9488),
      onSecondary = Color.White,
      secondaryContainer = Color(0xFFCCFBF1),
      onSecondaryContainer = Color(0xFF115E59),
      tertiary = Color(0xFF6366F1),
      background = Color(0xFFF0F9FF),
      onBackground = Color(0xFF082F49),
      surface = Color(0xFFFFFFFF),
      onSurface = Color(0xFF082F49),
      surfaceVariant = Color(0xFFE0F2FE),
      onSurfaceVariant = Color(0xFF336384),
      surfaceCapsule = Color(0xFFBAE6FD),
      outline = Color(0xFFB9E6FE),
      outlineVariant = Color(0xFFD0EFFE),
      isDark = false
    )
    AppThemeMode.CREAM_COFFEE -> AppThemeColors(
      mode = mode,
      primary = Color(0xFFB45309),
      primaryContainer = Color(0xFFFEF3C7),
      onPrimary = Color.White,
      onPrimaryContainer = Color(0xFF78350F),
      secondary = Color(0xFF92400E),
      onSecondary = Color.White,
      secondaryContainer = Color(0xFFFFEDD5),
      onSecondaryContainer = Color(0xFF7C2D12),
      tertiary = Color(0xFF059669),
      background = Color(0xFFFDFBF7),
      onBackground = Color(0xFF291B12),
      surface = Color(0xFFFFFFFF),
      onSurface = Color(0xFF291B12),
      surfaceVariant = Color(0xFFF7F1E7),
      onSurfaceVariant = Color(0xFF634D3C),
      surfaceCapsule = Color(0xFFEFE6D8),
      outline = Color(0xFFE4D5C1),
      outlineVariant = Color(0xFFEEE3D3),
      isDark = false
    )
  }

  // Handle OLED Black override for dark themes
  val oledApplied = if (baseColors.isDark && forceOledBlack) {
    baseColors.copy(
      background = Color(0xFF000000),
      surface = Color(0xFF080808),
      surfaceVariant = Color(0xFF121212),
      surfaceCapsule = Color(0xFF0E0E0E),
      outline = Color(0xFF242424),
      outlineVariant = Color(0xFF181818)
    )
  } else {
    baseColors
  }

  // Handle optional custom accent tint override
  return if (!accentOverrideHex.isNullOrBlank()) {
    try {
      val parsedAccent = Color(android.graphics.Color.parseColor(accentOverrideHex))
      oledApplied.copy(
        primary = parsedAccent,
        onPrimary = if (oledApplied.isDark) Color.Black else Color.White
      )
    } catch (_: Exception) {
      oledApplied
    }
  } else {
    oledApplied
  }
}

val LocalAppThemeColors = staticCompositionLocalOf {
  getThemeColors(AppThemeMode.MIDNIGHT_OBSIDIAN)
}

object ClassPlannerTheme {
  val colors: AppThemeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemeColors.current
}

private fun toM3ColorScheme(colors: AppThemeColors): ColorScheme {
  return if (colors.isDark) {
    darkColorScheme(
      primary = colors.primary,
      onPrimary = colors.onPrimary,
      primaryContainer = colors.primaryContainer,
      onPrimaryContainer = colors.onPrimaryContainer,
      secondary = colors.secondary,
      onSecondary = colors.onSecondary,
      secondaryContainer = colors.secondaryContainer,
      onSecondaryContainer = colors.onSecondaryContainer,
      tertiary = colors.tertiary,
      background = colors.background,
      onBackground = colors.onBackground,
      surface = colors.surface,
      onSurface = colors.onSurface,
      surfaceVariant = colors.surfaceVariant,
      onSurfaceVariant = colors.onSurfaceVariant,
      outline = colors.outline,
      outlineVariant = colors.outlineVariant
    )
  } else {
    lightColorScheme(
      primary = colors.primary,
      onPrimary = colors.onPrimary,
      primaryContainer = colors.primaryContainer,
      onPrimaryContainer = colors.onPrimaryContainer,
      secondary = colors.secondary,
      onSecondary = colors.onSecondary,
      secondaryContainer = colors.secondaryContainer,
      onSecondaryContainer = colors.onSecondaryContainer,
      tertiary = colors.tertiary,
      background = colors.background,
      onBackground = colors.onBackground,
      surface = colors.surface,
      onSurface = colors.onSurface,
      surfaceVariant = colors.surfaceVariant,
      onSurfaceVariant = colors.onSurfaceVariant,
      outline = colors.outline,
      outlineVariant = colors.outlineVariant
    )
  }
}

@Composable
fun MyApplicationTheme(
  themeMode: AppThemeMode = AppThemeMode.MIDNIGHT_OBSIDIAN,
  forceOledBlack: Boolean = false,
  accentOverrideHex: String? = null,
  content: @Composable () -> Unit,
) {
  val themeColors = getThemeColors(
    mode = themeMode,
    forceOledBlack = forceOledBlack,
    accentOverrideHex = accentOverrideHex
  )
  val m3ColorScheme = toM3ColorScheme(themeColors)

  CompositionLocalProvider(LocalAppThemeColors provides themeColors) {
    MaterialTheme(
      colorScheme = m3ColorScheme,
      typography = Typography,
      content = content
    )
  }
}
