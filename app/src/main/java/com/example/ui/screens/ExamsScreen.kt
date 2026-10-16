package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.model.ExamItem
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray

@Composable
fun ExamsScreen(
  exams: List<ExamItem>,
  onAddExam: (ExamItem) -> Unit,
  onDeleteExam: (String) -> Unit,
  onScanExamRoutine: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkAppBackground)
      .testTag("exams_screen_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Exam Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite
          )
          Text(
            text = "${exams.size} upcoming exam(s)",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryGray
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Scan Exam Routine Action Button
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E1F34),
            modifier = Modifier.testTag("scan_exam_routine_header_btn")
          ) {
            IconButton(onClick = onScanExamRoutine, modifier = Modifier.size(38.dp)) {
              Icon(
                Icons.Default.DocumentScanner,
                contentDescription = "Scan Exam Routine",
                tint = AccentLilac,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // Manual Add Exam Button
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AccentLilac,
            modifier = Modifier.testTag("add_exam_btn")
          ) {
            IconButton(onClick = { showAddDialog = true }, modifier = Modifier.size(38.dp)) {
              Icon(Icons.Default.Add, contentDescription = "Add Exam", tint = Color.Black, modifier = Modifier.size(22.dp))
            }
          }
        }
      }
    }

    if (exams.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          modifier = Modifier.fillMaxWidth().testTag("empty_exams_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.EventNote,
              contentDescription = null,
              tint = AccentLilac,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No upcoming exams",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Photograph your exam routine notice or circular to generate your exam dates and hall rooms automatically.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryGray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              // Scan Exam Routine Button
              Button(
                onClick = onScanExamRoutine,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentLilac),
                modifier = Modifier.testTag("empty_scan_exam_btn")
              ) {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Exam Routine", color = Color.Black, fontWeight = FontWeight.Bold)
              }

              // Manual Add Button
              Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1F30)),
                modifier = Modifier.testTag("empty_add_exam_manual_btn")
              ) {
                Text("+ Manual Add", color = TextPrimaryWhite)
              }
            }
          }
        }
      }
    } else {
      items(exams, key = { it.id }) { exam ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AccentLilac.copy(alpha = 0.15f),
                modifier = Modifier.padding(bottom = 6.dp)
              ) {
                Text(
                  text = exam.courseCode,
                  fontWeight = FontWeight.Bold,
                  color = AccentLilac,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
              Text(
                text = exam.courseTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimaryWhite
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "📅 ${exam.examDate} • ⏰ ${exam.time}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryGray
              )
              if (exam.room.isNotBlank()) {
                Text(
                  text = "📍 Room: ${exam.room}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryGray
                )
              }
            }

            IconButton(onClick = { onDeleteExam(exam.id) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
            }
          }
        }
      }
    }
  }

  if (showAddDialog) {
    var courseCode by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = DarkSurfaceCard,
      title = { Text("Add Exam", color = TextPrimaryWhite, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = courseCode, onValueChange = { courseCode = it }, label = { Text("Course Code (e.g. CSC 497)") }, singleLine = true)
          OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Exam Title (e.g. Midterm Exam)") }, singleLine = true)
          OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g. Thu, 22 Oct)") }, singleLine = true)
          OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:00 AM - 12:00 PM)") }, singleLine = true)
          OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room / Hall (e.g. Room 607)") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (courseCode.isNotBlank()) {
              onAddExam(
                ExamItem(
                  courseCode = courseCode.trim().uppercase(),
                  courseTitle = if (title.isBlank()) "Course Exam" else title.trim(),
                  examDate = if (date.isBlank()) "Next Week" else date.trim(),
                  time = if (time.isBlank()) "10:00 AM" else time.trim(),
                  room = room.trim()
                )
              )
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentLilac)
        ) {
          Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel", color = TextSecondaryGray)
        }
      }
    )
  }
}
