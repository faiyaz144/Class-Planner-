package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassRoutine
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray
import com.example.util.rememberAppHaptic
import java.util.Calendar

enum class TimelineClassStatus {
  COMPLETED,
  ONGOING,
  NEXT_UP,
  UPCOMING
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScheduleDashboardWidget(
  dayName: String,
  dateFormatted: String,
  routines: List<ClassRoutine>,
  onAddClassClick: () -> Unit,
  onScanRoutineClick: () -> Unit,
  onEditRoutine: (ClassRoutine) -> Unit,
  onDeleteRoutine: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = rememberAppHaptic()

  val sortedRoutines = remember(routines) {
    routines.sortedBy { it.startMinutes }
  }

  val cal = Calendar.getInstance()
  val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

  // Determine status for each routine
  val routineStatuses = remember(sortedRoutines, currentMinutes) {
    var foundNext = false
    sortedRoutines.map { routine ->
      when {
        currentMinutes >= routine.endMinutes && routine.endMinutes > 0 -> {
          TimelineClassStatus.COMPLETED
        }
        currentMinutes in routine.startMinutes until routine.endMinutes -> {
          TimelineClassStatus.ONGOING
        }
        routine.startMinutes > currentMinutes && !foundNext -> {
          foundNext = true
          TimelineClassStatus.NEXT_UP
        }
        else -> {
          TimelineClassStatus.UPCOMING
        }
      }
    }
  }

  val completedCount = routineStatuses.count { it == TimelineClassStatus.COMPLETED }
  val ongoingCount = routineStatuses.count { it == TimelineClassStatus.ONGOING }
  val totalCount = sortedRoutines.size

  val progressFraction = if (totalCount > 0) {
    (completedCount + (ongoingCount * 0.5f)) / totalCount.toFloat()
  } else {
    0f
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("today_schedule_dashboard_widget"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Widget Header Row: "Today's Schedule" + Focus Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.TrackChanges,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Today's Schedule",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryWhite,
              fontSize = 20.sp
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "$dayName · $dateFormatted",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryGray,
            fontSize = 12.sp
          )
        }

        // Status Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF1E2034)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            if (ongoingCount > 0) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF10B981))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "IN SESSION",
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            } else if (totalCount > 0 && completedCount == totalCount) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "DONE",
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            } else {
              Text(
                text = if (totalCount == 1) "1 CLASS" else "$totalCount CLASSES",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Progress Tracker Bar (if user has scheduled classes)
      if (totalCount > 0) {
        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (completedCount == totalCount) {
              "All $totalCount classes completed today! Great job."
            } else {
              "$completedCount of $totalCount completed · ${(progressFraction * 100).toInt()}% progress"
            },
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = TextSecondaryGray
          )

          if (ongoingCount > 0) {
            Text(
              text = "Live Now",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF10B981)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progressFraction },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = Color(0xFF22243A)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Chronological List Body
      if (sortedRoutines.isEmpty()) {
        // Free day empty state
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF1E2034)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "No classes",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "No Classes Today",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Your schedule is clear. Use this time to focus on study, prep, or relax.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryGray,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.clickable {
                haptic.click()
                onScanRoutineClick()
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Icon(
                  Icons.Default.DocumentScanner,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Timetable", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFF1E2034),
              modifier = Modifier.clickable {
                haptic.click()
                onAddClassClick()
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Icon(
                  Icons.Default.Add,
                  contentDescription = null,
                  tint = TextPrimaryWhite,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Class", color = TextPrimaryWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
              }
            }
          }
        }
      } else {
        // Chronological List with Timeline Connectors
        Column(modifier = Modifier.fillMaxWidth()) {
          sortedRoutines.forEachIndexed { index, routine ->
            val status = routineStatuses[index]
            val isLast = index == sortedRoutines.size - 1

            ChronologicalClassItem(
              routine = routine,
              status = status,
              currentMinutes = currentMinutes,
              isLast = isLast,
              onEdit = { onEditRoutine(routine) },
              onDelete = { onDeleteRoutine(routine.id) }
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChronologicalClassItem(
  routine: ClassRoutine,
  status: TimelineClassStatus,
  currentMinutes: Int,
  isLast: Boolean,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val haptic = rememberAppHaptic()
  val accentColor = Color(routine.colorHex)
  val isCompleted = status == TimelineClassStatus.COMPLETED
  val isOngoing = status == TimelineClassStatus.ONGOING
  val isNextUp = status == TimelineClassStatus.NEXT_UP

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    // Left: Chronological Time & Node Column
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(68.dp)
    ) {
      Text(
        text = routine.startTime,
        fontWeight = if (isOngoing || isNextUp) FontWeight.Bold else FontWeight.Medium,
        fontSize = 12.sp,
        color = when {
          isOngoing -> Color(0xFF10B981)
          isNextUp -> AccentLilac
          isCompleted -> TextSecondaryGray
          else -> TextPrimaryWhite
        }
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Timeline Node Indicator
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(
            when (status) {
              TimelineClassStatus.ONGOING -> Color(0xFF10B981)
              TimelineClassStatus.NEXT_UP -> MaterialTheme.colorScheme.primary
              TimelineClassStatus.COMPLETED -> Color(0xFF26283E)
              TimelineClassStatus.UPCOMING -> Color(0xFF1A1B2C)
            }
          )
          .border(
            width = 2.dp,
            color = when (status) {
              TimelineClassStatus.ONGOING -> Color(0xFF10B981)
              TimelineClassStatus.NEXT_UP -> MaterialTheme.colorScheme.primary
              TimelineClassStatus.COMPLETED -> Color(0xFF383A5A)
              TimelineClassStatus.UPCOMING -> Color(0xFF383A5A)
            },
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isCompleted) {
          Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = TextSecondaryGray,
            modifier = Modifier.size(10.dp)
          )
        } else if (isOngoing) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Color.White)
          )
        }
      }

      // Vertical connecting line to next item
      if (!isLast) {
        Box(
          modifier = Modifier
            .width(2.dp)
            .height(72.dp)
            .background(
              if (isCompleted) Color(0xFF26283E) else Color(0xFF2C2E47)
            )
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    // Right: Class Details Card
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = when {
        isOngoing -> Color(0xFF1B2228)
        isNextUp -> Color(0xFF1C1A2F)
        isCompleted -> Color(0xFF141522)
        else -> Color(0xFF161726)
      },
      border = if (isOngoing) {
        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
      } else if (isNextUp) {
        androidx.compose.foundation.BorderStroke(1.dp, AccentLilac.copy(alpha = 0.4f))
      } else null,
      modifier = Modifier
        .weight(1f)
        .testTag("timeline_item_${routine.courseCode}")
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // Top row: Course code pill + Status tag + Action buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = accentColor.copy(alpha = if (isCompleted) 0.12f else 0.22f)
            ) {
              Text(
                text = routine.courseCode,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isCompleted) accentColor.copy(alpha = 0.6f) else accentColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Status Pill
            when (status) {
              TimelineClassStatus.ONGOING -> {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color(0xFF10B981).copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "• NOW IN SESSION",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              TimelineClassStatus.NEXT_UP -> {
                val minsUntil = (routine.startMinutes - currentMinutes).coerceAtLeast(0)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = if (minsUntil > 0) "IN ${minsUntil}M" else "NEXT UP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              TimelineClassStatus.COMPLETED -> {
                Text(
                  text = "COMPLETED",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp,
                  color = TextSecondaryGray
                )
              }
              TimelineClassStatus.UPCOMING -> {}
            }
          }

          // Edit & Delete actions
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                haptic.click()
                onEdit()
              },
              modifier = Modifier.size(26.dp)
            ) {
              Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = TextSecondaryGray,
                modifier = Modifier.size(14.dp)
              )
            }

            IconButton(
              onClick = {
                haptic.reject()
                onDelete()
              },
              modifier = Modifier.size(26.dp)
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Course Title
        Text(
          text = routine.courseTitle,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = if (isCompleted) TextPrimaryWhite.copy(alpha = 0.6f) else TextPrimaryWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Time range & location pill row
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Time span
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AccessTime,
              contentDescription = null,
              tint = TextSecondaryGray,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${routine.startTime} – ${routine.endTime}",
              fontSize = 11.sp,
              color = TextSecondaryGray
            )
          }

          // Room
          if (routine.room.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = AccentLilac,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = routine.room,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryWhite
              )
            }
          }

          // Instructor
          if (routine.instructor.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = TextSecondaryGray,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = routine.instructor,
                fontSize = 11.sp,
                color = TextSecondaryGray
              )
            }
          }
        }
      }
    }
  }
}
