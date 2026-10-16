package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

data class ClassRoutine(
  val id: String = UUID.randomUUID().toString(),
  val courseCode: String,
  val courseTitle: String,
  val dayOfWeek: String, // "Monday", "Tuesday", etc.
  val startTime: String, // e.g. "08:30 AM" or "08:30"
  val endTime: String,   // e.g. "10:00 AM" or "10:00"
  val room: String = "",
  val instructor: String = "",
  val section: String = "",
  val colorHex: Long = 0xFF4F46E5,
  val notes: String = ""
) {
  val startMinutes: Int
    get() = parseTimeToMinutes(startTime)

  val endMinutes: Int
    get() = parseTimeToMinutes(endTime)

  fun overlapsWith(other: ClassRoutine): Boolean {
    if (this.id == other.id) return false
    if (!this.dayOfWeek.equals(other.dayOfWeek, ignoreCase = true)) return false
    val start1 = this.startMinutes
    val end1 = if (this.endMinutes <= start1) start1 + 60 else this.endMinutes
    val start2 = other.startMinutes
    val end2 = if (other.endMinutes <= start2) start2 + 60 else other.endMinutes

    return start1 < end2 && start2 < end1
  }

  companion object {
    val PRESET_COLORS = listOf(
      0xFF7568E2, // Lavender Purple
      0xFFF59E0B, // Amber Orange
      0xFF2563EB, // Electric Blue
      0xFFD946EF, // Magenta
      0xFFEF4444, // Coral Red
      0xFF0D9488, // Teal Mint
      0xFF10B981  // Emerald
    )

    fun colorForCourse(code: String): Long {
      val hash = code.uppercase().hashCode()
      val index = Math.abs(hash) % PRESET_COLORS.size
      return PRESET_COLORS[index]
    }

    fun parseTimeToMinutes(timeStr: String): Int {
      val trimmed = timeStr.trim().uppercase()
      val formats = listOf(
        SimpleDateFormat("hh:mm a", Locale.US),
        SimpleDateFormat("h:mm a", Locale.US),
        SimpleDateFormat("HH:mm", Locale.US),
        SimpleDateFormat("H:mm", Locale.US)
      )
      for (fmt in formats) {
        try {
          val date = fmt.parse(trimmed)
          if (date != null) {
            val cal = java.util.Calendar.getInstance()
            cal.time = date
            return cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
          }
        } catch (_: Exception) {}
      }

      // Fallback regex parser
      val regex = Regex("""(\d{1,2})[:.]?(\d{2})?\s*(AM|PM)?""")
      val match = regex.find(trimmed)
      if (match != null) {
        var hour = match.groupValues[1].toIntOrNull() ?: 8
        val min = match.groupValues[2].toIntOrNull() ?: 0
        val ampm = match.groupValues[3]
        if (ampm == "PM" && hour < 12) hour += 12
        if (ampm == "AM" && hour == 12) hour = 0
        return hour * 60 + min
      }

      return 8 * 60 // default 8:00 AM
    }
  }
}
