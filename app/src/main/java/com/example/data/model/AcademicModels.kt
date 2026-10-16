package com.example.data.model

import java.util.UUID

data class ExamItem(
  val id: String = UUID.randomUUID().toString(),
  val courseCode: String,
  val courseTitle: String,
  val examDate: String, // e.g. "2026-10-25" or "Oct 25"
  val time: String,     // e.g. "10:00 AM - 12:00 PM"
  val room: String,     // e.g. "Hall 607"
  val seatNumber: String = ""
)

data class FacultyMember(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val designation: String = "Professor",
  val department: String = "Computer Science",
  val email: String = "",
  val roomOffice: String = "",
  val courses: List<String> = emptyList()
)

data class DayCapsule(
  val dayShort: String,  // "SAT", "SUN", etc.
  val dayFull: String,   // "Saturday", "Sunday", etc.
  val dateNumber: Int,   // 10, 11, etc.
  val monthShort: String,// "Oct"
  val hasClasses: Boolean = false,
  val classDotColor: Long = 0xFFEF4444
)
