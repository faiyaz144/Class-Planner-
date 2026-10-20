package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassRoutine
import com.example.data.model.DayCapsule
import com.example.ui.components.ClassCard
import com.example.ui.components.TodayScheduleDashboardWidget
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.util.rememberAppHaptic

@Composable
fun TodayScreen(
  studentName: String,
  selectedDay: String,
  weekCapsules: List<DayCapsule>,
  nextClass: ClassRoutine?,
  routines: List<ClassRoutine>,
  onSelectDay: (String) -> Unit,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
  onAddClassClick: () -> Unit,
  onScanRoutineClick: () -> Unit,
  onEditRoutine: (ClassRoutine) -> Unit,
  onDeleteRoutine: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = rememberAppHaptic()

  val greeting = remember {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    when (hour) {
      in 4..11 -> "Good morning"
      in 12..16 -> "Good afternoon"
      else -> "Good evening"
    }
  }

  val todayFormatted = remember {
    SimpleDateFormat("EEEE, d MMMM", Locale.US).format(Date())
  }

  val filteredRoutines = remember(routines, selectedDay) {
    routines.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
      .sortedBy { it.startMinutes }
  }

  val weekRangeTitle = remember(weekCapsules) {
    if (weekCapsules.size >= 7) {
      val start = weekCapsules.first()
      val end = weekCapsules.last()
      "This week · ${start.dateNumber}–${end.dateNumber} ${end.monthShort}"
    } else {
      "This week"
    }
  }

  val dayShortName = remember(selectedDay) {
    when (selectedDay.lowercase()) {
      "saturday" -> "Sat"
      "sunday" -> "Sun"
      "monday" -> "Mon"
      "tuesday" -> "Tue"
      "wednesday" -> "Wed"
      "thursday" -> "Thu"
      "friday" -> "Fri"
      else -> selectedDay.take(3)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("today_screen_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Greeting Header: "Good afternoon, Faiyaz" + "Thursday, 15 October"
    item {
      Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
        Text(
          text = "$greeting, $studentName",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = todayFormatted,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Next Class Hero Card (Matching Image 1)
    item {
      NextClassHeroCard(
        nextClass = nextClass,
        onAddClassClick = onAddClassClick
      )
    }

    // Week / Day Selector Bar with arrows and capsules
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Week Header: < This week · 10–16 Oct >
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              haptic.tick()
              onPreviousWeek()
            },
            modifier = Modifier
              .size(36.dp)
              .testTag("prev_week_arrow")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.KeyboardArrowLeft,
              contentDescription = "Previous week",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = weekRangeTitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          IconButton(
            onClick = {
              haptic.tick()
              onNextWeek()
            },
            modifier = Modifier
              .size(36.dp)
              .testTag("next_week_arrow")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = "Next week",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 7 Day Capsules Row (SAT 10, SUN 11, MON 12, TUE 13, WED 14, THU 15, FRI 16)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          weekCapsules.forEach { capsule ->
            val isSelected = capsule.dayFull.equals(selectedDay, ignoreCase = true)
            DayCapsulePill(
              capsule = capsule,
              isSelected = isSelected,
              onClick = { onSelectDay(capsule.dayFull) },
              modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
            )
          }
        }
      }
    }

    // Today's Schedule Dashboard Widget (Chronological Focus List)
    item {
      TodayScheduleDashboardWidget(
        dayName = selectedDay,
        dateFormatted = todayFormatted,
        routines = filteredRoutines,
        onAddClassClick = onAddClassClick,
        onScanRoutineClick = onScanRoutineClick,
        onEditRoutine = onEditRoutine,
        onDeleteRoutine = onDeleteRoutine
      )
    }
  }
}

@Composable
fun NextClassHeroCard(
  nextClass: ClassRoutine?,
  onAddClassClick: () -> Unit
) {
  val haptic = rememberAppHaptic()
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("next_class_hero_card")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(
              MaterialTheme.colorScheme.primaryContainer,
              MaterialTheme.colorScheme.surfaceVariant,
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(20.dp)
    ) {
      if (nextClass != null) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Top row: "NEXT CLASS" pill + Room Pill on right
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            // Next class time badge
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "NEXT CLASS",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${nextClass.dayOfWeek.take(3)}, ${nextClass.startTime}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontSize = 11.sp
                )
              }
            }

            // Room badge on the right
            if (nextClass.room.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "ROOM",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                  )
                  Text(
                    text = nextClass.room.removePrefix("Room").removePrefix("ROOM").trim(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Course Code: e.g. "CSC 497"
          Text(
            text = nextClass.courseCode,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp
          )

          // Course Title: "Compiler Design"
          Text(
            text = nextClass.courseTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Bottom line: "9:35 AM - 10:35 AM · Sec A"
          Text(
            text = "${nextClass.startTime} – ${nextClass.endTime}${if (nextClass.section.isNotBlank()) " · ${nextClass.section}" else ""}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        // When no next class scheduled
        Column(modifier = Modifier.fillMaxWidth()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
          ) {
            Text(
              text = "NEXT CLASS · ALL CAUGHT UP",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "No Upcoming Class",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "Add classes or scan your timetable to see your next lecture here.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "+ Tap the top-right button to add a class",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
              haptic.click()
              onAddClassClick()
            }
          )
        }
      }
    }
  }
}

@Composable
fun DayCapsulePill(
  capsule: DayCapsule,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = rememberAppHaptic()
  val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
  val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
  val subTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = backgroundColor,
    modifier = modifier
      .clickable {
        haptic.tick()
        onClick()
      }
      .testTag("capsule_${capsule.dayShort}")
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 2.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = capsule.dayShort,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = subTextColor
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${capsule.dateNumber}",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = textColor
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Dot indicating classes
      Box(
        modifier = Modifier
          .size(5.dp)
          .clip(CircleShape)
          .background(
            if (isSelected) Color.Transparent
            else if (capsule.hasClasses) Color(capsule.classDotColor)
            else Color.Transparent
          )
      )
    }
  }
}
