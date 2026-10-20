package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamItem
import com.example.ui.ExamScanUiState
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray

import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.util.rememberAppHaptic

@Composable
fun ScanExamScreen(
  scanState: ExamScanUiState,
  onImageSelected: (android.net.Uri) -> Unit,
  onBitmapCaptured: (Bitmap) -> Unit,
  onImportTextRoutine: (String) -> Unit,
  onStartScan: () -> Unit,
  onConfirmExams: (List<ExamItem>, Boolean) -> Unit,
  onResetScan: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = rememberAppHaptic()
  var selectedInputMethod by remember { mutableIntStateOf(0) } // 0: Photo/Camera, 1: Paste Text
  var rawTextRoutine by remember { mutableStateOf("") }

  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null) {
      onImageSelected(uri)
    }
  }

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      onBitmapCaptured(bitmap)
    }
  }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      cameraLauncher.launch(null)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkAppBackground)
      .testTag("scan_exam_screen_scroll"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Banner
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color(0xFF7C3AED),
                  Color(0xFF4F46E5)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Exam Schedule AI Scanner",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Scan Exam Routine",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Upload or take a photo of your university examination schedule notice to automatically extract all exam dates, times, and exam halls.",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
        }
      }
    }

    // Input Mode Switcher: Photo/Camera vs Paste Text
    item {
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
          selected = selectedInputMethod == 0,
          onClick = {
            haptic.tick()
            selectedInputMethod = 0
          },
          shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
          modifier = Modifier.testTag("exam_method_photo")
        ) {
          Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Scan Photo")
        }
        SegmentedButton(
          selected = selectedInputMethod == 1,
          onClick = {
            haptic.tick()
            selectedInputMethod = 1
          },
          shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
          modifier = Modifier.testTag("exam_method_text")
        ) {
          Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Paste Circular")
        }
      }
    }

    if (selectedInputMethod == 0) {
      // Photo / Camera Source Selection
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Choose Exam Routine Image",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryWhite
            )
            Text(
              text = "Photograph the notice board exam circular or pick an image file.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryGray
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  haptic.click()
                  filePickerLauncher.launch("image/*")
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("upload_exam_image_btn"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Image")
              }

              OutlinedButton(
                onClick = {
                  haptic.click()
                  val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                  ) == PackageManager.PERMISSION_GRANTED

                  if (hasPermission) {
                    cameraLauncher.launch(null)
                  } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("camera_exam_capture_btn"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Take Photo")
              }
            }
          }
        }
      }

      // Scanning States
      when (scanState) {
        is ExamScanUiState.Idle -> {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  Icons.Default.EventNote,
                  contentDescription = null,
                  modifier = Modifier.size(48.dp),
                  tint = AccentLilac.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Ready to Scan Exam Routine",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimaryWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Take a photo of your university's examination schedule or upload a saved photo to extract exam dates, times, and exam halls.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryGray,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }

        is ExamScanUiState.ImageSelected -> {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              elevation = CardDefaults.cardElevation(3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "Exam Routine Image Ready",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimaryWhite
                    )
                    Text(
                      text = "Ready to analyze with Gemini AI",
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondaryGray
                    )
                  }

                  IconButton(
                    onClick = {
                      haptic.click()
                      onResetScan()
                    }
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextSecondaryGray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF222336), RoundedCornerShape(12.dp))
                ) {
                  Image(
                    bitmap = scanState.bitmap.asImageBitmap(),
                    contentDescription = "Exam routine preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                  onClick = {
                    haptic.heavy()
                    onStartScan()
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_exam_scan_ai_btn"),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = AccentLilac)
                ) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Scan Exam Routine with Gemini",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                  )
                }
              }
            }
          }
        }

        is ExamScanUiState.Scanning -> {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(44.dp),
                  color = AccentLilac,
                  strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = "Scanning Exam Circular...",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = scanState.progressText,
                  style = MaterialTheme.typography.bodyMedium,
                  color = AccentLilac,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }

        is ExamScanUiState.Success -> {
          item {
            val editableList = remember(scanState.extractedExams) {
              mutableStateListOf(*scanState.extractedExams.toTypedArray())
            }

            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(text = "Exams Extracted!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryWhite)
                      Text(text = "Found ${editableList.size} examination sessions", style = MaterialTheme.typography.bodySmall, color = TextSecondaryGray)
                    }
                  }

                  IconButton(onClick = onResetScan) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryGray)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                editableList.forEachIndexed { index, exam ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF181928),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = AccentLilac.copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = exam.courseCode,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AccentLilac,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = exam.courseTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryWhite)
                        Text(
                          text = "📅 ${exam.examDate} • ⏰ ${exam.time} ${if (exam.room.isNotBlank()) "• 📍 ${exam.room}" else ""}",
                          style = MaterialTheme.typography.bodySmall,
                          color = TextSecondaryGray
                        )
                      }

                      IconButton(
                        onClick = {
                          haptic.reject()
                          editableList.removeAt(index)
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = {
                      haptic.confirm()
                      onConfirmExams(editableList.toList(), true)
                    },
                    modifier = Modifier.weight(1f).testTag("save_replace_exams_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLilac)
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Exams", color = Color.Black, fontWeight = FontWeight.Bold)
                  }

                  OutlinedButton(
                    onClick = {
                      haptic.confirm()
                      onConfirmExams(editableList.toList(), false)
                    },
                    modifier = Modifier.weight(1f).testTag("append_exams_btn"),
                    shape = RoundedCornerShape(12.dp)
                  ) {
                    Text("Append Exams")
                  }
                }
              }
            }
          }
        }

        is ExamScanUiState.Error -> {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF261317)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFEF4444), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Scan Failed", fontWeight = FontWeight.Bold, color = TextPrimaryWhite)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = scanState.message, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFCA5A5), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  Button(onClick = onStartScan, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry Scan")
                  }
                  OutlinedButton(onClick = onResetScan) {
                    Text("Choose Another")
                  }
                }
              }
            }
          }
        }
      }
    } else {
      // Paste Circular Text Tab
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Paste Exam Circular Notice",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryWhite
            )
            Text(
              text = "Paste text from your university's examination schedule circular or PDF notice.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryGray
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = rawTextRoutine,
              onValueChange = { rawTextRoutine = it },
              placeholder = {
                Text(
                  "Example:\n22 Oct 2026\nCSC 497 Compiler Design 10:00 AM - 12:00 PM Room 607\n\n26 Oct 2026\nCSE 4383 Computer Graphics 11:30 AM - 12:30 PM Room 620"
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("paste_exam_textfield"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                if (rawTextRoutine.isNotBlank()) {
                  onImportTextRoutine(rawTextRoutine)
                }
              },
              enabled = rawTextRoutine.isNotBlank(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("parse_exam_text_btn"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = AccentLilac)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generate Exam Schedule from Text", color = Color.Black, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
