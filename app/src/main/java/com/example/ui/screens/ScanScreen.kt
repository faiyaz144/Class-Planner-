package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.ClassRoutine
import com.example.ui.ScanUiState

@Composable
fun ScanScreen(
  scanState: ScanUiState,
  onImageSelected: (android.net.Uri) -> Unit,
  onBitmapCaptured: (Bitmap) -> Unit,
  onImportTextRoutine: (String) -> Unit,
  onStartScan: () -> Unit,
  onConfirmRoutines: (List<ClassRoutine>, Boolean) -> Unit,
  onResetScan: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedInputMethod by remember { mutableIntStateOf(0) } // 0: Photo/Camera Scan, 1: Paste Text
  var rawTextRoutine by remember { mutableStateOf("") }

  // Generic file & image picker (works reliably across all Android platforms & emulators)
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null) {
      onImageSelected(uri)
    }
  }

  // Camera capture launcher
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      onBitmapCaptured(bitmap)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF090A10))
      .testTag("scan_screen_scroll"),
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
                  MaterialTheme.colorScheme.primary,
                  Color(0xFF6366F1)
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
                    text = "Gemini 4 Argon AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Scan Class Routine",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Take a photo or upload an image of your class routine to build your weekly schedule automatically.",
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
          onClick = { selectedInputMethod = 0 },
          shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
          modifier = Modifier.testTag("method_photo")
        ) {
          Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Scan Photo")
        }
        SegmentedButton(
          selected = selectedInputMethod == 1,
          onClick = { selectedInputMethod = 1 },
          shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
          modifier = Modifier.testTag("method_text")
        ) {
          Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Paste Text")
        }
      }
    }

    if (selectedInputMethod == 0) {
      // Photo / Camera Source Selection
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Capture or Select Routine",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Snap a picture of your paper timetable or select an image file from your device.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Upload Button
              Button(
                onClick = { filePickerLauncher.launch("image/*") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("upload_image_btn"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Image")
              }

              // Camera Button
              OutlinedButton(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("camera_capture_btn"),
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
        is ScanUiState.Idle -> {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  Icons.Default.DocumentScanner,
                  contentDescription = null,
                  modifier = Modifier.size(48.dp),
                  tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "No Routine Selected",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Choose 'Select Image' or 'Take Photo' above to scan your class schedule with Gemini AI.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }

        is ScanUiState.ImageSelected -> {
          item {
            SelectedImageCard(
              bitmap = scanState.bitmap,
              onStartScan = onStartScan,
              onCancel = onResetScan
            )
          }
        }

        is ScanUiState.Scanning -> {
          item {
            ScanningProgressCard(
              progressText = scanState.progressText,
              bitmap = scanState.bitmap
            )
          }
        }

        is ScanUiState.Success -> {
          item {
            ExtractedResultsSection(
              extractedRoutines = scanState.extractedRoutines,
              bitmap = scanState.bitmap,
              onConfirm = onConfirmRoutines,
              onCancel = onResetScan
            )
          }
        }

        is ScanUiState.Error -> {
          item {
            ErrorCard(
              errorMessage = scanState.message,
              onRetry = onStartScan,
              onCancel = onResetScan
            )
          }
        }
      }
    } else {
      // Paste Raw Text Routine
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Paste Timetable Text",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Paste text from your university portal, syllabus, or email to create your schedule.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = rawTextRoutine,
              onValueChange = { rawTextRoutine = it },
              placeholder = {
                Text(
                  "Example:\nMonday\nCS101 08:30 AM - 10:00 AM Room 302\nMATH201 11:30 AM - 01:00 PM Hall B\n\nTuesday\nENG105 10:00 AM - 11:30 AM Room 204"
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("paste_routine_textfield"),
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
                .testTag("parse_text_routine_btn"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generate Schedule from Text")
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

@Composable
fun SelectedImageCard(
  bitmap: Bitmap,
  onStartScan: () -> Unit,
  onCancel: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            text = "Routine Photo Ready",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Tap below to scan with Gemini AI",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = onCancel) {
          Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
      ) {
        Image(
          bitmap = bitmap.asImageBitmap(),
          contentDescription = "Routine image preview",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onStartScan,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("start_scan_ai_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Scan Routine with Gemini AI",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }
    }
  }
}

@Composable
fun ScanningProgressCard(
  progressText: String,
  bitmap: Bitmap
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(44.dp),
          color = MaterialTheme.colorScheme.primary,
          strokeWidth = 4.dp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Scanning Your Routine...",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = progressText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Multimodal Engine: Gemini 4 Argon / 3.8 Flash",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun ExtractedResultsSection(
  extractedRoutines: List<ClassRoutine>,
  bitmap: Bitmap,
  onConfirm: (List<ClassRoutine>, Boolean) -> Unit,
  onCancel: () -> Unit
) {
  val editableList = remember(extractedRoutines) {
    mutableStateListOf(*extractedRoutines.toTypedArray())
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Classes Extracted!",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Detected ${editableList.size} classes from your image",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onCancel) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      editableList.forEachIndexed { index, routine ->
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(routine.colorHex).copy(alpha = 0.15f)
                ) {
                  Text(
                    text = routine.courseCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(routine.colorHex),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = routine.dayOfWeek,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = routine.courseTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${routine.startTime} - ${routine.endTime} ${if (routine.room.isNotBlank()) "• ${routine.room}" else ""} ${if (routine.instructor.isNotBlank()) "• ${routine.instructor}" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            IconButton(
              onClick = { editableList.removeAt(index) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "Remove class",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
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
          onClick = { onConfirm(editableList.toList(), true) },
          modifier = Modifier
            .weight(1f)
            .testTag("save_replace_schedule_btn"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save to Schedule")
        }

        OutlinedButton(
          onClick = { onConfirm(editableList.toList(), false) },
          modifier = Modifier
            .weight(1f)
            .testTag("append_schedule_btn"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Append Classes")
        }
      }
    }
  }
}

@Composable
fun ErrorCard(
  errorMessage: String,
  onRetry: () -> Unit,
  onCancel: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        Icons.Default.Warning,
        contentDescription = "Error",
        tint = Color(0xFFDC2626),
        modifier = Modifier.size(40.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "Scan Failed",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF991B1B)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = errorMessage,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF7F1D1D),
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(16.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = onRetry,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Retry Scan")
        }
        OutlinedButton(onClick = onCancel) {
          Text("Choose Another Image")
        }
      }
    }
  }
}
