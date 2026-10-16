package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassRoutine

enum class ClassLiveStatus {
  ONGOING,
  UPCOMING,
  COMPLETED,
  NONE
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassCard(
  routine: ClassRoutine,
  modifier: Modifier = Modifier,
  liveStatus: ClassLiveStatus = ClassLiveStatus.NONE,
  onEdit: (() -> Unit)? = null,
  onDelete: (() -> Unit)? = null
) {
  val accentColor = Color(routine.colorHex)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("class_card_${routine.courseCode}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
    ) {
      // Left color accent bar
      Box(
        modifier = Modifier
          .width(6.dp)
          .background(accentColor)
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Top Header: Course code + Title + Live status + Edit actions
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
              shape = RoundedCornerShape(8.dp),
              color = accentColor.copy(alpha = 0.15f),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Text(
                text = routine.courseCode,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            if (routine.section.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
              ) {
                Text(
                  text = routine.section,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          // Live status badge if applicable
          if (liveStatus == ClassLiveStatus.ONGOING) {
            Surface(
              shape = CircleShape,
              color = Color(0xFF10B981)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "NOW",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
            }
          }

          // Actions
          if (onEdit != null || onDelete != null) {
            Row(horizontalArrangement = Arrangement.End) {
              if (onEdit != null) {
                IconButton(
                  onClick = onEdit,
                  modifier = Modifier
                    .size(32.dp)
                    .testTag("edit_button_${routine.courseCode}")
                ) {
                  Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit class",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              if (onDelete != null) {
                IconButton(
                  onClick = onDelete,
                  modifier = Modifier
                    .size(32.dp)
                    .testTag("delete_button_${routine.courseCode}")
                ) {
                  Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete class",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Course Title
        Text(
          text = routine.courseTitle,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Metadata Pills: Time, Room, Instructor
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // Time Badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${routine.startTime} - ${routine.endTime}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Room Badge
          if (routine.room.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  Icons.Default.LocationOn,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = routine.room,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Instructor Badge
          if (routine.instructor.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  Icons.Default.Person,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = routine.instructor,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        if (routine.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "📝 ${routine.notes}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }
      }
    }
  }
}
