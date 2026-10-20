package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassRoutine
import com.example.data.model.DayCapsule
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray
import com.example.util.rememberAppHaptic

@Composable
fun WeeklyScreen(
  routines: List<ClassRoutine>,
  weekCapsules: List<DayCapsule>,
  weekOffset: Int,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
  onAddClick: () -> Unit,
  onEditRoutine: (ClassRoutine) -> Unit,
  onDeleteRoutine: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = rememberAppHaptic()
  val horizontalScroll = rememberScrollState()
  val verticalScroll = rememberScrollState()

  // Standard hours from 9 AM to 6 PM as shown in Image 3
  val startHour = 9
  val endHour = 18
  val hourHeightDp = 64.dp
  val dayColumnWidthDp = 100.dp
  val timeLabelWidthDp = 50.dp

  val totalClassCount = routines.size

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("weekly_screen_view")
  ) {
    // Navigation bar with < X classes > (Image 3)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = {
          haptic.tick()
          onPreviousWeek()
        },
        modifier = Modifier.size(32.dp).testTag("week_grid_prev_btn")
      ) {
        Icon(
          Icons.AutoMirrored.Filled.KeyboardArrowLeft,
          contentDescription = "Previous week",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
        text = if (totalClassCount == 1) "1 class" else "$totalClassCount classes",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.width(16.dp))

      IconButton(
        onClick = {
          haptic.tick()
          onNextWeek()
        },
        modifier = Modifier.size(32.dp).testTag("week_grid_next_btn")
      ) {
        Icon(
          Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = "Next week",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Timetable Matrix (Image 3)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 76.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .horizontalScroll(horizontalScroll)
      ) {
        // Sticky Day Header Row: Sat 10, Sun 11, Mon 12, Tue 13, Wed 14, Thu 15, Fri 16
        Row(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp)
        ) {
          // Empty space above time labels
          Spacer(modifier = Modifier.width(timeLabelWidthDp))

          weekCapsules.forEach { capsule ->
            Column(
              modifier = Modifier
                .width(dayColumnWidthDp)
                .padding(horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = capsule.dayShort,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${capsule.dateNumber}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

        // Scrollable Grid Area (Time Axis + Day Columns)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(verticalScroll)
        ) {
          // Left Time Column: 9 AM, 10 AM, 11 AM, ...
          Column(modifier = Modifier.width(timeLabelWidthDp)) {
            for (hour in startHour..endHour) {
              val hourLabel = when {
                hour < 12 -> "$hour AM"
                hour == 12 -> "12 PM"
                else -> "${hour - 12} PM"
              }
              Box(
                modifier = Modifier
                  .height(hourHeightDp)
                  .fillMaxWidth()
                  .padding(end = 8.dp),
                contentAlignment = Alignment.TopEnd
              ) {
                Text(
                  text = hourLabel,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextMutedGray
                )
              }
            }
          }

          // Day Grid Area with horizontal lines and colored class blocks
          Box(
            modifier = Modifier
              .width(dayColumnWidthDp * weekCapsules.size)
              .height(hourHeightDp * (endHour - startHour + 1))
          ) {
            // Horizontal hour guideline rows
            Column(modifier = Modifier.fillMaxSize()) {
              for (h in startHour..endHour) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeightDp)
                ) {
                  HorizontalDivider(
                    color = Color(0xFF161726),
                    thickness = 0.8.dp,
                    modifier = Modifier.align(Alignment.TopStart)
                  )
                }
              }
            }

            // Vertical subtle day dividers
            Row(modifier = Modifier.fillMaxSize()) {
              weekCapsules.forEach { _ ->
                Box(
                  modifier = Modifier
                    .width(dayColumnWidthDp)
                    .fillMaxSize()
                    .border(width = 0.5.dp, color = Color(0xFF131422).copy(alpha = 0.6f))
                )
              }
            }

            // Colored Class Blocks (Image 3: CSC 497, CSE 4383, CSC 465, etc.)
            weekCapsules.forEachIndexed { dayIndex, capsule ->
              val dayClasses = routines.filter { it.dayOfWeek.equals(capsule.dayFull, ignoreCase = true) }

              dayClasses.forEach { routine ->
                val startMins = routine.startMinutes
                val endMins = if (routine.endMinutes <= startMins) startMins + 60 else routine.endMinutes

                val gridStartMins = startHour * 60
                val topOffsetMinutes = (startMins - gridStartMins).coerceAtLeast(0)
                val durationMinutes = (endMins - startMins).coerceIn(30, 240)

                val topOffsetDp = (topOffsetMinutes / 60f) * 64f
                val cardHeightDp = (durationMinutes / 60f) * 64f
                val leftOffsetDp = dayIndex * 100f

                Box(
                  modifier = Modifier
                    .offset(x = leftOffsetDp.dp + 4.dp, y = topOffsetDp.dp + 1.dp)
                    .width(dayColumnWidthDp - 8.dp)
                    .height(cardHeightDp.dp - 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(routine.colorHex))
                    .clickable {
                      haptic.click()
                      onEditRoutine(routine)
                    }
                    .padding(8.dp)
                    .testTag("grid_class_${routine.courseCode}")
                ) {
                  Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text(
                        text = routine.courseCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                      )
                    }

                    if (routine.room.isNotBlank()) {
                      Text(
                        text = routine.room.removePrefix("Room").removePrefix("ROOM").trim(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
