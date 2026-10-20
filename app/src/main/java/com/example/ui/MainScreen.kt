package com.example.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClassRoutine
import com.example.ui.components.ClassCueDrawerContent
import com.example.ui.components.ThemeSelectionDialog
import com.example.ui.screens.AddEditClassDialog
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.FacultyScreen
import com.example.ui.screens.ScanExamScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WeeklyScreen
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray
import com.example.util.rememberAppHaptic
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = rememberAppHaptic()
  val coroutineScope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

  val studentName by viewModel.studentName.collectAsStateWithLifecycle()
  val routines by viewModel.allRoutines.collectAsStateWithLifecycle()
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
  val weekOffset by viewModel.weekOffset.collectAsStateWithLifecycle()
  val weekCapsules by viewModel.weekCapsules.collectAsStateWithLifecycle()
  val nextClass by viewModel.nextClass.collectAsStateWithLifecycle()
  val exams by viewModel.exams.collectAsStateWithLifecycle()
  val facultyList by viewModel.facultyList.collectAsStateWithLifecycle()
  val attendanceCount by viewModel.attendanceCount.collectAsStateWithLifecycle()
  val scanState by viewModel.scanState.collectAsStateWithLifecycle()
  val showScannerModal by viewModel.showScannerModal.collectAsStateWithLifecycle()
  val examScanState by viewModel.examScanState.collectAsStateWithLifecycle()
  val showExamScannerModal by viewModel.showExamScannerModal.collectAsStateWithLifecycle()
  val showSettingsModal by viewModel.showSettingsModal.collectAsStateWithLifecycle()
  val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
  val oledPitchBlackEnabled by viewModel.oledPitchBlackEnabled.collectAsStateWithLifecycle()
  val accentOverrideHex by viewModel.accentOverrideHex.collectAsStateWithLifecycle()
  val showThemeModal by viewModel.showThemeModal.collectAsStateWithLifecycle()
  val editingRoutine by viewModel.editingRoutine.collectAsStateWithLifecycle()
  val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
  val notificationLeadMinutes by viewModel.notificationLeadMinutes.collectAsStateWithLifecycle()

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { _ -> }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  var isAddingNewClass by remember { mutableStateOf(false) }
  var showAttendanceDialog by remember { mutableStateOf(false) }
  var showNewSemesterDialog by remember { mutableStateOf(false) }
  var showBreaksDialog by remember { mutableStateOf(false) }

  // Handle hardware / gesture back button
  BackHandler(enabled = drawerState.isOpen || currentTab != AppTab.TODAY) {
    if (drawerState.isOpen) {
      coroutineScope.launch { drawerState.close() }
    } else {
      viewModel.setTab(AppTab.TODAY)
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(310.dp)
      ) {
        ClassCueDrawerContent(
          studentName = studentName,
          totalClassesWeekly = routines.size,
          attendanceCount = attendanceCount,
          currentThemeName = currentTheme.displayName,
          onOpenAttendance = { showAttendanceDialog = true },
          onNewSemester = { showNewSemesterDialog = true },
          onOpenBreaks = { showBreaksDialog = true },
          onOpenExams = { viewModel.setTab(AppTab.EXAMS) },
          onOpenShare = {
            val text = viewModel.getScheduleAsFormattedText()
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, text)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Routine"))
          },
          onImportRoutine = { viewModel.openScanner() },
          onScanExamRoutine = { viewModel.openExamScanner() },
          onOpenSettings = { viewModel.openSettings() },
          onOpenThemes = { viewModel.openThemeModal() },
          onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
        )
      }
    }
  ) {
    Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        // Top App Bar matching Images 1, 2, 3
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Hamburger menu icon button in dark rounded squircle (Image 1 & 3)
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable {
                haptic.click()
                coroutineScope.launch { drawerState.open() }
              }
              .testTag("hamburger_menu_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Menu,
              contentDescription = "Menu",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }

          // Center title on Week View
          if (currentTab == AppTab.WEEK) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "This week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
              )
              val range = if (weekCapsules.size >= 7) {
                "${weekCapsules.first().dateNumber}–${weekCapsules.last().dateNumber} ${weekCapsules.last().monthShort}"
              } else "Current"
              Text(
                text = range,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
              )
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          // Right: Theme Palette Button + Scanner Button + Circular FAB button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Theme Options Button
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                  haptic.click()
                  viewModel.openThemeModal()
                }
                .testTag("top_theme_btn"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Palette,
                contentDescription = "Theme Options",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }

            // Scanner Button
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                  haptic.click()
                  viewModel.openScanner()
                }
                .testTag("top_scan_routine_btn"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.DocumentScanner,
                contentDescription = "Scan Routine",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }

            // Add Class FAB
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                  haptic.click()
                  isAddingNewClass = true
                }
                .testTag("top_add_class_fab"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Add,
                contentDescription = "Add Class",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      },
      bottomBar = {
        // Bottom Navigation Bar matching Image 1, 2, 3: Today, Week, Exams, Faculty
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.background,
          tonalElevation = 0.dp,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          // 1. Today Tab
          NavigationBarItem(
            selected = currentTab == AppTab.TODAY,
            onClick = {
              haptic.tick()
              viewModel.setTab(AppTab.TODAY)
            },
            icon = {
              Icon(
                Icons.Default.CalendarToday,
                contentDescription = "Today",
                modifier = Modifier.size(20.dp)
              )
            },
            label = { Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_today")
          )

          // 2. Week Tab
          NavigationBarItem(
            selected = currentTab == AppTab.WEEK,
            onClick = {
              haptic.tick()
              viewModel.setTab(AppTab.WEEK)
            },
            icon = {
              Icon(
                Icons.Default.TableChart,
                contentDescription = "Week",
                modifier = Modifier.size(20.dp)
              )
            },
            label = { Text("Week", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_week")
          )

          // 3. Exams Tab
          NavigationBarItem(
            selected = currentTab == AppTab.EXAMS,
            onClick = {
              haptic.tick()
              viewModel.setTab(AppTab.EXAMS)
            },
            icon = {
              Icon(
                Icons.AutoMirrored.Filled.FactCheck,
                contentDescription = "Exams",
                modifier = Modifier.size(20.dp)
              )
            },
            label = { Text("Exams", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_exams")
          )

          // 4. Faculty Tab
          NavigationBarItem(
            selected = currentTab == AppTab.FACULTY,
            onClick = {
              haptic.tick()
              viewModel.setTab(AppTab.FACULTY)
            },
            icon = {
              Icon(
                Icons.Default.People,
                contentDescription = "Faculty",
                modifier = Modifier.size(20.dp)
              )
            },
            label = { Text("Faculty", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_faculty")
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentTab) {
          AppTab.TODAY -> {
            TodayScreen(
              studentName = studentName,
              selectedDay = selectedDay,
              weekCapsules = weekCapsules,
              nextClass = nextClass,
              routines = routines,
              onSelectDay = {
                haptic.tick()
                viewModel.selectDay(it)
              },
              onPreviousWeek = {
                haptic.tick()
                viewModel.previousWeek()
              },
              onNextWeek = {
                haptic.tick()
                viewModel.nextWeek()
              },
              onAddClassClick = {
                haptic.click()
                isAddingNewClass = true
              },
              onScanRoutineClick = {
                haptic.click()
                viewModel.openScanner()
              },
              onEditRoutine = {
                haptic.click()
                viewModel.setEditingRoutine(it)
              },
              onDeleteRoutine = {
                haptic.reject()
                viewModel.deleteRoutine(it)
              }
            )
          }

          AppTab.WEEK -> {
            WeeklyScreen(
              routines = routines,
              weekCapsules = weekCapsules,
              weekOffset = weekOffset,
              onPreviousWeek = {
                haptic.tick()
                viewModel.previousWeek()
              },
              onNextWeek = {
                haptic.tick()
                viewModel.nextWeek()
              },
              onAddClick = {
                haptic.click()
                isAddingNewClass = true
              },
              onEditRoutine = {
                haptic.click()
                viewModel.setEditingRoutine(it)
              },
              onDeleteRoutine = {
                haptic.reject()
                viewModel.deleteRoutine(it)
              }
            )
          }

          AppTab.EXAMS -> {
            ExamsScreen(
              exams = exams,
              onAddExam = {
                haptic.confirm()
                viewModel.addExam(it)
              },
              onDeleteExam = {
                haptic.reject()
                viewModel.deleteExam(it)
              },
              onScanExamRoutine = {
                haptic.click()
                viewModel.openExamScanner()
              }
            )
          }

          AppTab.FACULTY -> {
            FacultyScreen(
              facultyList = facultyList
            )
          }
        }
      }
    }
  }

  // Class Routine Scanner Modal
  if (showScannerModal) {
    Dialog(
      onDismissRequest = { viewModel.closeScanner() },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkAppBackground
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .statusBarsPadding()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Import Class Routine",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryWhite
            )

            IconButton(onClick = { viewModel.closeScanner() }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimaryWhite)
            }
          }

          ScanScreen(
            scanState = scanState,
            onImageSelected = { viewModel.onImageUriSelected(it) },
            onBitmapCaptured = { viewModel.onBitmapCaptured(it) },
            onImportTextRoutine = { viewModel.onImportTextRoutine(it) },
            onStartScan = {
              haptic.heavy()
              viewModel.startScan()
            },
            onConfirmRoutines = { list, replace ->
              haptic.confirm()
              viewModel.confirmScannedRoutines(list, replace)
            },
            onResetScan = {
              haptic.click()
              viewModel.resetScanState()
            },
            onSampleClick = {
              haptic.click()
              viewModel.onSampleImageSelected()
            }
          )
        }
      }
    }
  }

  // Dedicated Exam Routine Scanner Modal
  if (showExamScannerModal) {
    Dialog(
      onDismissRequest = { viewModel.closeExamScanner() },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkAppBackground
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .statusBarsPadding()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Import Exam Routine",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryWhite
            )

            IconButton(onClick = { viewModel.closeExamScanner() }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimaryWhite)
            }
          }

          ScanExamScreen(
            scanState = examScanState,
            onImageSelected = { viewModel.onExamImageUriSelected(it) },
            onBitmapCaptured = { viewModel.onExamBitmapCaptured(it) },
            onImportTextRoutine = { viewModel.onImportTextExamRoutine(it) },
            onStartScan = {
              haptic.heavy()
              viewModel.startExamScan()
            },
            onConfirmExams = { list, replace ->
              haptic.confirm()
              viewModel.confirmScannedExams(list, replace)
            },
            onResetScan = {
              haptic.click()
              viewModel.resetExamScanState()
            }
          )
        }
      }
    }
  }

  // Add / Edit Class Dialog
  if (isAddingNewClass || editingRoutine != null) {
    AddEditClassDialog(
      initialRoutine = editingRoutine,
      defaultDay = selectedDay,
      onDismiss = {
        isAddingNewClass = false
        viewModel.setEditingRoutine(null)
      },
      onSave = { routine ->
        haptic.confirm()
        viewModel.saveRoutine(routine)
        isAddingNewClass = false
      }
    )
  }

  // Settings / Profile Customizer Dialog
  if (showSettingsModal) {
    var tempName by remember { mutableStateOf(studentName) }
    AlertDialog(
      onDismissRequest = { viewModel.closeSettings() },
      containerColor = DarkSurfaceCard,
      title = { Text("Settings", color = TextPrimaryWhite, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Student Name", style = MaterialTheme.typography.bodySmall, color = TextSecondaryGray)
          OutlinedTextField(
            value = tempName,
            onValueChange = { tempName = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(4.dp))

          // App Theme & Palette Section
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF181928),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                haptic.click()
                viewModel.openThemeModal()
              }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentTheme.previewBackground)
                    .border(1.dp, currentTheme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(14.dp)
                      .clip(CircleShape)
                      .background(currentTheme.accentColor)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Theme & Appearance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryWhite
                  )
                  Text(
                    text = currentTheme.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = currentTheme.accentColor
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = currentTheme.accentColor.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Change",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = currentTheme.accentColor,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Notification Settings Section
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF181928),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Class Reminders",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryWhite
                  )
                  Text(
                    text = "Alerts before each lecture begins",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryGray
                  )
                }

                Switch(
                  checked = notificationsEnabled,
                  onCheckedChange = {
                    haptic.tick()
                    viewModel.toggleNotifications(it)
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = AccentLilac
                  )
                )
              }

              if (notificationsEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Reminder timing before class:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(5, 10, 15, 30).forEach { mins ->
                    val isSelected = notificationLeadMinutes == mins
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) AccentLilac else Color(0xFF222338),
                      modifier = Modifier
                        .weight(1f)
                        .clickable {
                          haptic.tick()
                          viewModel.setNotificationLeadMinutes(mins)
                        }
                    ) {
                      Box(
                        modifier = Modifier.padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = "${mins}m",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) Color.Black else TextPrimaryWhite
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Test Class Notification Button
                Button(
                  onClick = {
                    haptic.click()
                    viewModel.sendTestClassNotification()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23253C)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "Test Class Notification Popup",
                    fontSize = 12.sp,
                    color = AccentLilac,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Total registered classes: ${routines.size}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryGray
          )

          Button(
            onClick = {
              haptic.reject()
              viewModel.clearAllRoutines()
              viewModel.closeSettings()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Clear Entire Schedule", color = Color.White)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            haptic.confirm()
            viewModel.setStudentName(tempName)
            viewModel.closeSettings()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Save", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.closeSettings() }) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    )
  }

  // Attendance Dialog
  if (showAttendanceDialog) {
    AlertDialog(
      onDismissRequest = { showAttendanceDialog = false },
      containerColor = MaterialTheme.colorScheme.surface,
      title = { Text("Attendance Tracker", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("You have $attendanceCount missed/pending attendance records.", color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(8.dp))
          Text("All lectures this week are in regular standing (95% attendance rate).", color = MaterialTheme.colorScheme.onSurface)
        }
      },
      confirmButton = {
        Button(
          onClick = { showAttendanceDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("OK", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  // New Semester Dialog
  if (showNewSemesterDialog) {
    AlertDialog(
      onDismissRequest = { showNewSemesterDialog = false },
      containerColor = MaterialTheme.colorScheme.surface,
      title = { Text("Start New Semester?", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
      text = {
        Text("This will clear your previous routine and let you scan or create a fresh timetable for your new semester.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearAllRoutines()
            showNewSemesterDialog = false
            viewModel.openScanner()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Start Fresh", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewSemesterDialog = false }) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    )
  }

  // Breaks Dialog
  if (showBreaksDialog) {
    AlertDialog(
      onDismissRequest = { showBreaksDialog = false },
      containerColor = MaterialTheme.colorScheme.surface,
      title = { Text("Breaks & Holidays", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("• Semester Break: Dec 20 – Jan 5", color = MaterialTheme.colorScheme.onSurface)
          Text("• Mid-term Recess: Oct 28 – Nov 2", color = MaterialTheme.colorScheme.onSurface)
          Text("• National Holiday: Dec 16", color = MaterialTheme.colorScheme.onSurface)
        }
      },
      confirmButton = {
        Button(
          onClick = { showBreaksDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Close", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  // Theme & Appearance Customizer Dialog
  if (showThemeModal) {
    ThemeSelectionDialog(
      currentTheme = currentTheme,
      oledPitchBlackEnabled = oledPitchBlackEnabled,
      accentOverrideHex = accentOverrideHex,
      onThemeSelected = { mode ->
        viewModel.setTheme(mode)
      },
      onOledPitchBlackToggled = { enabled ->
        viewModel.setOledPitchBlack(enabled)
      },
      onAccentOverrideSelected = { hex ->
        viewModel.setAccentOverride(hex)
      },
      onDismiss = {
        viewModel.closeThemeModal()
      }
    )
  }
}
