package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ClassRoutine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Class Planner", appName)
  }

  @Test
  fun `test time parsing and clash detection`() {
    val routine1 = ClassRoutine(
      courseCode = "CS101",
      courseTitle = "Data Structures",
      dayOfWeek = "Monday",
      startTime = "08:30 AM",
      endTime = "10:00 AM"
    )

    val routine2 = ClassRoutine(
      courseCode = "MATH201",
      courseTitle = "Calculus I",
      dayOfWeek = "Monday",
      startTime = "09:30 AM",
      endTime = "11:00 AM"
    )

    val routine3 = ClassRoutine(
      courseCode = "PHY102",
      courseTitle = "Physics",
      dayOfWeek = "Monday",
      startTime = "11:00 AM",
      endTime = "12:30 PM"
    )

    val routineTuesday = ClassRoutine(
      courseCode = "ENG105",
      courseTitle = "English",
      dayOfWeek = "Tuesday",
      startTime = "08:30 AM",
      endTime = "10:00 AM"
    )

    // routine1 (8:30 - 10:00) overlaps with routine2 (9:30 - 11:00) on Monday
    assertTrue(routine1.overlapsWith(routine2))
    assertTrue(routine2.overlapsWith(routine1))

    // routine1 does NOT overlap with routine3 (11:00 - 12:30)
    assertFalse(routine1.overlapsWith(routine3))

    // routine1 does NOT overlap with routineTuesday on a different day
    assertFalse(routine1.overlapsWith(routineTuesday))

    // Check minutes calculation
    assertEquals(8 * 60 + 30, routine1.startMinutes)
    assertEquals(10 * 60, routine1.endMinutes)
  }

  @Test
  fun `test exam item structure`() {
    val exam = com.example.data.model.ExamItem(
      courseCode = "CSC 497",
      courseTitle = "Compiler Design Midterm",
      examDate = "Thu, 22 Oct",
      time = "10:00 AM - 12:00 PM",
      room = "Room 607"
    )
    assertEquals("CSC 497", exam.courseCode)
    assertEquals("Room 607", exam.room)
    assertEquals("Thu, 22 Oct", exam.examDate)
  }

  @Test
  fun `test today schedule chronological ordering`() {
    val morningClass = ClassRoutine(
      courseCode = "CSC 497",
      courseTitle = "Compiler Design",
      dayOfWeek = "Thursday",
      startTime = "09:35 AM",
      endTime = "10:55 AM"
    )
    val afternoonClass = ClassRoutine(
      courseCode = "MAT 205",
      courseTitle = "Linear Algebra",
      dayOfWeek = "Thursday",
      startTime = "02:00 PM",
      endTime = "03:20 PM"
    )
    val middayClass = ClassRoutine(
      courseCode = "PHY 102",
      courseTitle = "Physics II",
      dayOfWeek = "Thursday",
      startTime = "11:15 AM",
      endTime = "12:35 PM"
    )

    val list = listOf(afternoonClass, morningClass, middayClass)
    val sorted = list.sortedBy { it.startMinutes }

    assertEquals("CSC 497", sorted[0].courseCode)
    assertEquals("PHY 102", sorted[1].courseCode)
    assertEquals("MAT 205", sorted[2].courseCode)
  }
}

