package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ThemeCategory
import com.example.ui.theme.getThemeColors
import com.example.util.rememberAppHaptic

data class AccentPreset(
  val label: String,
  val hex: String?,
  val color: Color
)

val ACCENT_PRESETS = listOf(
  AccentPreset("Default", null, Color.Transparent),
  AccentPreset("Lilac", "#9E86EB", Color(0xFF9E86EB)),
  AccentPreset("Violet", "#A855F7", Color(0xFFA855F7)),
  AccentPreset("Emerald", "#10B981", Color(0xFF10B981)),
  AccentPreset("Cyan", "#38BDF8", Color(0xFF38BDF8)),
  AccentPreset("Amber", "#F59E0B", Color(0xFFF59E0B)),
  AccentPreset("Sakura", "#F472B6", Color(0xFFF472B6)),
  AccentPreset("Ruby", "#F43F5E", Color(0xFFF43F5E)),
  AccentPreset("Tangerine", "#FF6B35", Color(0xFFFF6B35)),
  AccentPreset("Indigo", "#4F46E5", Color(0xFF4F46E5))
)

@Composable
fun ThemeSelectionDialog(
  currentTheme: AppThemeMode,
  oledPitchBlackEnabled: Boolean = false,
  accentOverrideHex: String? = null,
  onThemeSelected: (AppThemeMode) -> Unit,
  onOledPitchBlackToggled: (Boolean) -> Unit = {},
  onAccentOverrideSelected: (String?) -> Unit = {},
  onDismiss: () -> Unit
) {
  val haptic = rememberAppHaptic()
  var selectedCategory by remember { mutableStateOf(ThemeCategory.ALL) }

  val filteredThemes = remember(selectedCategory) {
    when (selectedCategory) {
      ThemeCategory.ALL -> AppThemeMode.entries.toList()
      ThemeCategory.DARK -> AppThemeMode.entries.filter { it.isDark }
      ThemeCategory.LIGHT -> AppThemeMode.entries.filter { !it.isDark }
    }
  }

  // Active theme preview colors
  val previewColors = remember(currentTheme, oledPitchBlackEnabled, accentOverrideHex) {
    getThemeColors(currentTheme, oledPitchBlackEnabled, accentOverrideHex)
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("theme_selection_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Palette,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Theme & Aesthetics",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${AppThemeMode.entries.size} themes · OLED & Custom accents",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(
          onClick = {
            haptic.click()
            onDismiss()
          }
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // ITEM 1: Live Interactive Preview Card
        item {
          LiveThemePreviewCard(
            colors = previewColors,
            themeName = currentTheme.displayName
          )
        }

        // ITEM 2: Category Filter Chips
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ThemeCategory.entries.forEach { category ->
              val isCatSelected = selectedCategory == category
              val count = when (category) {
                ThemeCategory.ALL -> AppThemeMode.entries.size
                ThemeCategory.DARK -> AppThemeMode.entries.count { it.isDark }
                ThemeCategory.LIGHT -> AppThemeMode.entries.count { !it.isDark }
              }

              FilterChip(
                selected = isCatSelected,
                onClick = {
                  haptic.tick()
                  selectedCategory = category
                },
                label = {
                  Text(
                    text = "${category.label} ($count)",
                    fontSize = 12.sp,
                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
        }

        // ITEM 3: OLED Pitch Black Switch (shown for dark themes)
        if (currentTheme.isDark) {
          item {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(Color.Black)
                      .border(1.dp, Color(0xFF404040), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Brightness4,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "Pure OLED Black",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                      ) {
                        Text(
                          text = "BATTERY SAVER",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF10B981),
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                    }
                    Text(
                      text = "True #000000 pitch black display",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Switch(
                  checked = oledPitchBlackEnabled,
                  onCheckedChange = {
                    haptic.tick()
                    onOledPitchBlackToggled(it)
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                  )
                )
              }
            }
          }
        }

        // ITEM 4: Accent Color Tone Customizer
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.ColorLens,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Accent Highlight Tint",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              if (accentOverrideHex != null) {
                Text(
                  text = "Reset",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.clickable {
                    haptic.tick()
                    onAccentOverrideSelected(null)
                  }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              ACCENT_PRESETS.forEach { preset ->
                val isAccentActive = if (preset.hex == null) {
                  accentOverrideHex == null
                } else {
                  accentOverrideHex.equals(preset.hex, ignoreCase = true)
                }

                val swatchColor = if (preset.hex == null) currentTheme.accentColor else preset.color

                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
                    .border(
                      width = if (isAccentActive) 2.5.dp else 1.dp,
                      color = if (isAccentActive) MaterialTheme.colorScheme.onSurface else swatchColor.copy(alpha = 0.5f),
                      shape = CircleShape
                    )
                    .clickable {
                      haptic.tick()
                      onAccentOverrideSelected(preset.hex)
                    },
                  contentAlignment = Alignment.Center
                ) {
                  if (preset.hex == null && !isAccentActive) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = if (currentTheme.isDark) Color.Black else Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  } else if (isAccentActive) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Selected",
                      tint = if (swatchColor == Color.White) Color.Black else Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            }
          }
        }

        item {
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 4.dp)
          )
        }

        // ITEM 5: List of Theme Cards
        items(filteredThemes, key = { it.id }) { themeMode ->
          val isSelected = themeMode == currentTheme

          ThemeOptionCard(
            themeMode = themeMode,
            isSelected = isSelected,
            onClick = {
              haptic.confirm()
              onThemeSelected(themeMode)
            }
          )
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          haptic.click()
          onDismiss()
        }
      ) {
        Text(
          text = "Done",
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  )
}

@Composable
fun LiveThemePreviewCard(
  colors: com.example.ui.theme.AppThemeColors,
  themeName: String
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = colors.background
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(colors.primary)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LIVE TIMETABLE PREVIEW",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            letterSpacing = 0.5.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = colors.surfaceCapsule
        ) {
          Text(
            text = themeName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Mini simulated Class Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .width(4.dp)
              .height(38.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(colors.primary)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CSC 497",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = colors.primary.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "NOW",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.primary,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Compiler Design · Room 402",
              fontSize = 11.sp,
              color = colors.onSurfaceVariant
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surfaceCapsule
          ) {
            Text(
              text = "9:35 AM",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.onSurface,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun ThemeOptionCard(
  themeMode: AppThemeMode,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) themeMode.accentColor else MaterialTheme.colorScheme.outlineVariant,
    animationSpec = tween(durationMillis = 200),
    label = "borderColor"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() }
      .testTag("theme_card_${themeMode.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Palette Visual Swatch Preview Box
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(themeMode.previewBackground)
          .border(
            width = 1.dp,
            color = themeMode.accentColor.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp)
          )
          .padding(6.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Mini class card mock
          Box(
            modifier = Modifier
              .width(36.dp)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(themeMode.accentColor)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(themeMode.accentColor)
            )
            Box(
              modifier = Modifier
                .width(16.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                  if (themeMode.isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f)
                )
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = themeMode.displayName,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))

          // Badge tag
          if (themeMode.badgeTag != null) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = themeMode.accentColor.copy(alpha = 0.18f)
            ) {
              Text(
                text = themeMode.badgeTag,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = themeMode.accentColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
          }

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (themeMode.isDark) Color(0xFF1E1F30) else Color(0xFFE2E8F0)
          ) {
            Text(
              text = if (themeMode.isDark) "DARK" else "LIGHT",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = if (themeMode.isDark) Color(0xFF9499B0) else Color(0xFF475569),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = themeMode.subtitle,
          style = MaterialTheme.typography.bodySmall,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Checkmark Radio Badge
      Box(
        modifier = Modifier
          .size(26.dp)
          .clip(CircleShape)
          .background(
            if (isSelected) themeMode.accentColor else Color.Transparent
          )
          .border(
            width = if (isSelected) 0.dp else 1.5.dp,
            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = if (themeMode.isDark) Color.Black else Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
