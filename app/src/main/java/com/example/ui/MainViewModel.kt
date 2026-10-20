package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiRoutineParser
import com.example.data.db.RoutineDatabase
import com.example.data.model.ClassRoutine
import com.example.data.model.DayCapsule
import com.example.data.model.ExamItem
import com.example.data.model.FacultyMember
import com.example.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import android.content.Context
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AppTab {
  TODAY,
  WEEK,
  EXAMS,
  FACULTY
}

sealed class ScanUiState {
  object Idle : ScanUiState()
  data class ImageSelected(val bitmap: Bitmap, val isSample: Boolean = false) : ScanUiState()
  data class Scanning(val progressText: String, val bitmap: Bitmap) : ScanUiState()
  data class Success(val extractedRoutines: List<ClassRoutine>, val bitmap: Bitmap) : ScanUiState()
  data class Error(val message: String, val bitmap: Bitmap?) : ScanUiState()
}

sealed class ExamScanUiState {
  object Idle : ExamScanUiState()
  data class ImageSelected(val bitmap: Bitmap) : ExamScanUiState()
  data class Scanning(val progressText: String, val bitmap: Bitmap) : ExamScanUiState()
  data class Success(val extractedExams: List<ExamItem>, val bitmap: Bitmap) : ExamScanUiState()
  data class Error(val message: String, val bitmap: Bitmap?) : ExamScanUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: RoutineRepository
  private val parser: GeminiRoutineParser

  init {
    val db = RoutineDatabase.getDatabase(application)
    repository = RoutineRepository(db.routineDao())
    parser = GeminiRoutineParser(application)
  }

  val allRoutines: StateFlow<List<ClassRoutine>> = repository.allRoutines
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _studentName = MutableStateFlow("Faiyaz")
  val studentName: StateFlow<String> = _studentName.asStateFlow()

  private val _currentTab = MutableStateFlow(AppTab.TODAY)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _selectedDay = MutableStateFlow(getTodayDayOfWeek())
  val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

  private val _weekOffset = MutableStateFlow(0)
  val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()

  // Class Routine Scanning State
  private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
  val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

  private val _showScannerModal = MutableStateFlow(false)
  val showScannerModal: StateFlow<Boolean> = _showScannerModal.asStateFlow()

  // Exam Routine Scanning State (Dedicated separate scanner)
  private val _examScanState = MutableStateFlow<ExamScanUiState>(ExamScanUiState.Idle)
  val examScanState: StateFlow<ExamScanUiState> = _examScanState.asStateFlow()

  private val _showExamScannerModal = MutableStateFlow(false)
  val showExamScannerModal: StateFlow<Boolean> = _showExamScannerModal.asStateFlow()

  private val prefs = application.getSharedPreferences("class_planner_prefs", Context.MODE_PRIVATE)

  private val _currentTheme = MutableStateFlow(
    AppThemeMode.fromId(prefs.getString("app_theme_mode", AppThemeMode.MIDNIGHT_OBSIDIAN.id))
  )
  val currentTheme: StateFlow<AppThemeMode> = _currentTheme.asStateFlow()

  private val _oledPitchBlackEnabled = MutableStateFlow(
    prefs.getBoolean("oled_pitch_black_enabled", false)
  )
  val oledPitchBlackEnabled: StateFlow<Boolean> = _oledPitchBlackEnabled.asStateFlow()

  private val _accentOverrideHex = MutableStateFlow<String?>(
    prefs.getString("app_accent_override", null)
  )
  val accentOverrideHex: StateFlow<String?> = _accentOverrideHex.asStateFlow()

  private val _showThemeModal = MutableStateFlow(false)
  val showThemeModal: StateFlow<Boolean> = _showThemeModal.asStateFlow()

  fun openThemeModal() {
    _showThemeModal.value = true
  }

  fun closeThemeModal() {
    _showThemeModal.value = false
  }

  fun setTheme(mode: AppThemeMode) {
    _currentTheme.value = mode
    prefs.edit().putString("app_theme_mode", mode.id).apply()
  }

  fun setOledPitchBlack(enabled: Boolean) {
    _oledPitchBlackEnabled.value = enabled
    prefs.edit().putBoolean("oled_pitch_black_enabled", enabled).apply()
  }

  fun setAccentOverride(hex: String?) {
    _accentOverrideHex.value = hex
    if (hex != null) {
      prefs.edit().putString("app_accent_override", hex).apply()
    } else {
      prefs.edit().remove("app_accent_override").apply()
    }
  }

  private val _showSettingsModal = MutableStateFlow(false)
  val showSettingsModal: StateFlow<Boolean> = _showSettingsModal.asStateFlow()

  private val _editingRoutine = MutableStateFlow<ClassRoutine?>(null)
  val editingRoutine: StateFlow<ClassRoutine?> = _editingRoutine.asStateFlow()

  private val _attendanceCount = MutableStateFlow(2)
  val attendanceCount: StateFlow<Int> = _attendanceCount.asStateFlow()

  private val _exams = MutableStateFlow<List<ExamItem>>(emptyList())
  val exams: StateFlow<List<ExamItem>> = _exams.asStateFlow()

  // Notification Reminder System State
  private val _notificationsEnabled = MutableStateFlow(true)
  val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

  private val _notificationLeadMinutes = MutableStateFlow(10)
  val notificationLeadMinutes: StateFlow<Int> = _notificationLeadMinutes.asStateFlow()

  // Dynamic day capsules (Sat -> Fri) as in screenshot
  val weekCapsules: StateFlow<List<DayCapsule>> = combine(allRoutines, _weekOffset) { routines, offset ->
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.SATURDAY
    cal.add(Calendar.WEEK_OF_YEAR, offset)

    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val diff = (dayOfWeek - Calendar.SATURDAY + 7) % 7
    cal.add(Calendar.DAY_OF_MONTH, -diff)

    val list = mutableListOf<DayCapsule>()
    val dayNames = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val shortNames = listOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")

    for (i in 0..6) {
      val dayFull = dayNames[i]
      val dayShort = shortNames[i]
      val dateNum = cal.get(Calendar.DAY_OF_MONTH)
      val monthShort = SimpleDateFormat("MMM", Locale.US).format(cal.time)
      val dayRoutines = routines.filter { it.dayOfWeek.equals(dayFull, ignoreCase = true) }
      val hasClasses = dayRoutines.isNotEmpty()
      val dotColor = if (hasClasses) dayRoutines.first().colorHex else 0xFFEF4444

      list.add(
        DayCapsule(
          dayShort = dayShort,
          dayFull = dayFull,
          dateNumber = dateNum,
          monthShort = monthShort,
          hasClasses = hasClasses,
          classDotColor = dotColor
        )
      )
      cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    list
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Next Class calculation for the Hero Card
  val nextClass: StateFlow<ClassRoutine?> = allRoutines.combine(_selectedDay) { routines, _ ->
    if (routines.isEmpty()) return@combine null

    val cal = Calendar.getInstance()
    val currentDayIndex = when (cal.get(Calendar.DAY_OF_WEEK)) {
      Calendar.SATURDAY -> 0
      Calendar.SUNDAY -> 1
      Calendar.MONDAY -> 2
      Calendar.TUESDAY -> 3
      Calendar.WEDNESDAY -> 4
      Calendar.THURSDAY -> 5
      Calendar.FRIDAY -> 6
      else -> 0
    }
    val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    val dayOrder = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    val todayName = dayOrder[currentDayIndex]
    val todayClasses = routines.filter { it.dayOfWeek.equals(todayName, ignoreCase = true) }
      .sortedBy { it.startMinutes }

    val upcomingToday = todayClasses.firstOrNull { it.startMinutes > currentMinutes }
    if (upcomingToday != null) return@combine upcomingToday

    for (step in 1..7) {
      val nextDay = dayOrder[(currentDayIndex + step) % 7]
      val nextDayClasses = routines.filter { it.dayOfWeek.equals(nextDay, ignoreCase = true) }
        .sortedBy { it.startMinutes }
      if (nextDayClasses.isNotEmpty()) {
        return@combine nextDayClasses.first()
      }
    }

    routines.firstOrNull()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val facultyList: StateFlow<List<FacultyMember>> = allRoutines.combine(_selectedDay) { routines, _ ->
    val instructors = routines.filter { it.instructor.isNotBlank() }
      .groupBy { it.instructor }

    val list = mutableListOf<FacultyMember>()
    instructors.forEach { (name, classes) ->
      val courses = classes.map { it.courseCode }.distinct()
      val room = classes.firstOrNull { it.room.isNotBlank() }?.room.orEmpty()
      list.add(
        FacultyMember(
          name = name,
          designation = "Course Instructor",
          department = "Academic Faculty",
          roomOffice = if (room.isNotBlank()) "Office / $room" else "Faculty Office",
          courses = courses
        )
      )
    }
    list
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  fun selectDay(day: String) {
    _selectedDay.value = day
  }

  fun previousWeek() {
    _weekOffset.value -= 1
  }

  fun nextWeek() {
    _weekOffset.value += 1
  }

  fun setStudentName(name: String) {
    if (name.isNotBlank()) {
      _studentName.value = name.trim()
    }
  }

  // Class Scanner controls
  fun openScanner() {
    _showScannerModal.value = true
  }

  fun closeScanner() {
    _showScannerModal.value = false
    _scanState.value = ScanUiState.Idle
  }

  // Exam Scanner controls (Dedicated)
  fun openExamScanner() {
    _showExamScannerModal.value = true
  }

  fun closeExamScanner() {
    _showExamScannerModal.value = false
    _examScanState.value = ExamScanUiState.Idle
  }

  fun openSettings() {
    _showSettingsModal.value = true
  }

  fun closeSettings() {
    _showSettingsModal.value = false
  }

  // Class routine image scan handlers
  fun onImageUriSelected(uri: Uri) {
    viewModelScope.launch {
      val bitmap = parser.loadBitmapFromUri(uri)
      if (bitmap != null) {
        _scanState.value = ScanUiState.ImageSelected(bitmap, isSample = false)
      } else {
        _scanState.value = ScanUiState.Error("Failed to decode the selected routine image.", null)
      }
    }
  }

  fun onBitmapCaptured(bitmap: Bitmap) {
    _scanState.value = ScanUiState.ImageSelected(bitmap, isSample = false)
  }

  fun onSampleImageSelected() {
    viewModelScope.launch {
      val bitmap = parser.loadSampleRoutineBitmap()
      if (bitmap != null) {
        _scanState.value = ScanUiState.ImageSelected(bitmap, isSample = true)
      } else {
        _scanState.value = ScanUiState.Error("Sample routine image could not be loaded.", null)
      }
    }
  }

  fun onImportTextRoutine(rawText: String) {
    viewModelScope.launch {
      val parsed = parser.parseRoutineFromRawText(rawText)
      if (parsed.isNotEmpty()) {
        _scanState.value = ScanUiState.Success(
          parsed,
          Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )
      } else {
        _scanState.value = ScanUiState.Error(
          "Could not detect any classes. Please format lines with Day, Course Code (e.g. CSC 497), and Time.",
          null
        )
      }
    }
  }

  fun startScan() {
    val currentState = _scanState.value
    val bitmap = when (currentState) {
      is ScanUiState.ImageSelected -> currentState.bitmap
      is ScanUiState.Error -> currentState.bitmap ?: return
      else -> return
    }

    viewModelScope.launch {
      _scanState.value = ScanUiState.Scanning("Analyzing routine with Gemini AI...", bitmap)

      val result = parser.analyzeRoutineImage(bitmap) { progress ->
        _scanState.value = ScanUiState.Scanning(progress, bitmap)
      }

      result.onSuccess { routines ->
        _scanState.value = ScanUiState.Success(routines, bitmap)
      }.onFailure { error ->
        _scanState.value = ScanUiState.Error(
          error.message ?: "Failed to scan timetable image.",
          bitmap
        )
      }
    }
  }

  fun confirmScannedRoutines(routines: List<ClassRoutine>, replaceExisting: Boolean) {
    viewModelScope.launch {
      if (replaceExisting) {
        allRoutines.value.forEach {
          com.example.util.NotificationScheduler.cancelClassReminder(getApplication(), it.id)
        }
        repository.deleteAll()
      }
      repository.insertAll(routines)
      if (_notificationsEnabled.value) {
        com.example.util.NotificationScheduler.rescheduleAll(
          getApplication(),
          routines,
          _notificationLeadMinutes.value,
          true
        )
      }
      com.example.widget.TodayScheduleWidgetProvider.updateAllWidgets(getApplication())
      _scanState.value = ScanUiState.Idle
      _showScannerModal.value = false
      _currentTab.value = AppTab.WEEK
    }
  }

  fun resetScanState() {
    _scanState.value = ScanUiState.Idle
  }

  // Exam routine image scan handlers
  fun onExamImageUriSelected(uri: Uri) {
    viewModelScope.launch {
      val bitmap = parser.loadBitmapFromUri(uri)
      if (bitmap != null) {
        _examScanState.value = ExamScanUiState.ImageSelected(bitmap)
      } else {
        _examScanState.value = ExamScanUiState.Error("Failed to decode the selected exam routine image.", null)
      }
    }
  }

  fun onExamBitmapCaptured(bitmap: Bitmap) {
    _examScanState.value = ExamScanUiState.ImageSelected(bitmap)
  }

  fun onImportTextExamRoutine(rawText: String) {
    viewModelScope.launch {
      val parsed = parser.parseExamRoutineFromRawText(rawText)
      if (parsed.isNotEmpty()) {
        _examScanState.value = ExamScanUiState.Success(
          parsed,
          Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )
      } else {
        _examScanState.value = ExamScanUiState.Error(
          "Could not detect exams in the pasted text. Please include Course Code, Date, Time, and Hall/Room.",
          null
        )
      }
    }
  }

  fun startExamScan() {
    val currentState = _examScanState.value
    val bitmap = when (currentState) {
      is ExamScanUiState.ImageSelected -> currentState.bitmap
      is ExamScanUiState.Error -> currentState.bitmap ?: return
      else -> return
    }

    viewModelScope.launch {
      _examScanState.value = ExamScanUiState.Scanning("Analyzing exam schedule with Gemini AI...", bitmap)

      val result = parser.analyzeExamRoutineImage(bitmap) { progress ->
        _examScanState.value = ExamScanUiState.Scanning(progress, bitmap)
      }

      result.onSuccess { extractedExams ->
        _examScanState.value = ExamScanUiState.Success(extractedExams, bitmap)
      }.onFailure { error ->
        _examScanState.value = ExamScanUiState.Error(
          error.message ?: "Failed to scan exam schedule.",
          bitmap
        )
      }
    }
  }

  fun confirmScannedExams(newExams: List<ExamItem>, replaceExisting: Boolean) {
    if (replaceExisting) {
      _exams.value = newExams
    } else {
      _exams.value = _exams.value + newExams
    }
    _examScanState.value = ExamScanUiState.Idle
    _showExamScannerModal.value = false
    _currentTab.value = AppTab.EXAMS
  }

  fun resetExamScanState() {
    _examScanState.value = ExamScanUiState.Idle
  }

  fun saveRoutine(routine: ClassRoutine) {
    viewModelScope.launch {
      repository.insertRoutine(routine)
      if (_notificationsEnabled.value) {
        com.example.util.NotificationScheduler.scheduleClassReminder(
          getApplication(),
          routine,
          _notificationLeadMinutes.value
        )
      }
      com.example.widget.TodayScheduleWidgetProvider.updateAllWidgets(getApplication())
      _editingRoutine.value = null
    }
  }

  fun deleteRoutine(id: String) {
    viewModelScope.launch {
      com.example.util.NotificationScheduler.cancelClassReminder(getApplication(), id)
      repository.deleteRoutine(id)
      com.example.widget.TodayScheduleWidgetProvider.updateAllWidgets(getApplication())
    }
  }

  fun clearAllRoutines() {
    viewModelScope.launch {
      allRoutines.value.forEach {
        com.example.util.NotificationScheduler.cancelClassReminder(getApplication(), it.id)
      }
      repository.deleteAll()
      com.example.widget.TodayScheduleWidgetProvider.updateAllWidgets(getApplication())
    }
  }

  fun toggleNotifications(enabled: Boolean) {
    _notificationsEnabled.value = enabled
    com.example.util.NotificationScheduler.rescheduleAll(
      getApplication(),
      allRoutines.value,
      _notificationLeadMinutes.value,
      enabled
    )
  }

  fun setNotificationLeadMinutes(minutes: Int) {
    _notificationLeadMinutes.value = minutes
    com.example.util.NotificationScheduler.rescheduleAll(
      getApplication(),
      allRoutines.value,
      minutes,
      _notificationsEnabled.value
    )
  }

  fun sendTestClassNotification() {
    com.example.util.NotificationScheduler.sendImmediateTestNotification(
      getApplication(),
      _notificationLeadMinutes.value
    )
  }

  fun setEditingRoutine(routine: ClassRoutine?) {
    _editingRoutine.value = routine
  }

  fun addExam(exam: ExamItem) {
    _exams.value = _exams.value + exam
  }

  fun deleteExam(id: String) {
    _exams.value = _exams.value.filter { it.id != id }
  }

  fun getScheduleAsFormattedText(): String {
    return repository.exportScheduleAsText(allRoutines.value)
  }

  fun getScheduleAsIcs(): String {
    return repository.exportAsIcs(allRoutines.value)
  }

  companion object {
    fun getTodayDayOfWeek(): String {
      return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        else -> "Thursday"
      }
    }
  }
}
