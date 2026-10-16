package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.model.ClassRoutine
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassDialog(
  initialRoutine: ClassRoutine?,
  defaultDay: String = "Monday",
  onDismiss: () -> Unit,
  onSave: (ClassRoutine) -> Unit
) {
  var courseCode by remember { mutableStateOf(initialRoutine?.courseCode.orEmpty()) }
  var courseTitle by remember { mutableStateOf(initialRoutine?.courseTitle.orEmpty()) }
  var dayOfWeek by remember { mutableStateOf(initialRoutine?.dayOfWeek ?: defaultDay) }
  var startTime by remember { mutableStateOf(initialRoutine?.startTime ?: "09:00 AM") }
  var endTime by remember { mutableStateOf(initialRoutine?.endTime ?: "10:30 AM") }
  var room by remember { mutableStateOf(initialRoutine?.room.orEmpty()) }
  var instructor by remember { mutableStateOf(initialRoutine?.instructor.orEmpty()) }
  var section by remember { mutableStateOf(initialRoutine?.section.orEmpty()) }
  var selectedColorHex by remember {
    mutableLongStateOf(initialRoutine?.colorHex ?: ClassRoutine.PRESET_COLORS.first())
  }
  var notes by remember { mutableStateOf(initialRoutine?.notes.orEmpty()) }

  val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
  var dayDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF13141F),
    title = {
      Text(
        text = if (initialRoutine == null) "Add New Class" else "Edit Class",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF9FAFB)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Course Code & Title
        OutlinedTextField(
          value = courseCode,
          onValueChange = { courseCode = it },
          label = { Text("Course Code (e.g. CS101)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_course_code")
        )

        OutlinedTextField(
          value = courseTitle,
          onValueChange = { courseTitle = it },
          label = { Text("Course Title (e.g. Data Structures)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_course_title")
        )

        // Day of Week Dropdown
        ExposedDropdownMenuBox(
          expanded = dayDropdownExpanded,
          onExpandedChange = { dayDropdownExpanded = !dayDropdownExpanded }
        ) {
          OutlinedTextField(
            value = dayOfWeek,
            onValueChange = {},
            readOnly = true,
            label = { Text("Day of Week") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayDropdownExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("input_day_of_week")
          )
          ExposedDropdownMenu(
            expanded = dayDropdownExpanded,
            onDismissRequest = { dayDropdownExpanded = false }
          ) {
            days.forEach { day ->
              DropdownMenuItem(
                text = { Text(day) },
                onClick = {
                  dayOfWeek = day
                  dayDropdownExpanded = false
                }
              )
            }
          }
        }

        // Start & End Times
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Start Time") },
            placeholder = { Text("08:30 AM") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_start_time")
          )

          OutlinedTextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("End Time") },
            placeholder = { Text("10:00 AM") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_end_time")
          )
        }

        // Room & Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room / Hall") },
            singleLine = true,
            modifier = Modifier
              .weight(1.2f)
              .testTag("input_room")
          )

          OutlinedTextField(
            value = section,
            onValueChange = { section = it },
            label = { Text("Section") },
            singleLine = true,
            modifier = Modifier
              .weight(0.8f)
              .testTag("input_section")
          )
        }

        // Instructor
        OutlinedTextField(
          value = instructor,
          onValueChange = { instructor = it },
          label = { Text("Instructor / Professor") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_instructor")
        )

        // Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes (optional)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Color selector
        Text(
          text = "Badge Color",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ClassRoutine.PRESET_COLORS) { colorHex ->
            val isSelected = selectedColorHex == colorHex
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(colorHex))
                .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedColorHex = colorHex }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val routine = ClassRoutine(
            id = initialRoutine?.id ?: UUID.randomUUID().toString(),
            courseCode = if (courseCode.isBlank()) "GEN101" else courseCode.trim().uppercase(),
            courseTitle = if (courseTitle.isBlank()) "General Lecture" else courseTitle.trim(),
            dayOfWeek = dayOfWeek,
            startTime = if (startTime.isBlank()) "09:00 AM" else startTime.trim(),
            endTime = if (endTime.isBlank()) "10:30 AM" else endTime.trim(),
            room = room.trim(),
            instructor = instructor.trim(),
            section = section.trim(),
            colorHex = selectedColorHex,
            notes = notes.trim()
          )
          onSave(routine)
        },
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
          containerColor = Color(0xFF9E86EB),
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("save_class_dialog_btn")
      ) {
        Text("Save Class", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
