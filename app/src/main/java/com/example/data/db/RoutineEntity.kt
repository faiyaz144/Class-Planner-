package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ClassRoutine

@Entity(tableName = "class_routines")
data class RoutineEntity(
  @PrimaryKey
  val id: String,
  val courseCode: String,
  val courseTitle: String,
  val dayOfWeek: String,
  val startTime: String,
  val endTime: String,
  val room: String,
  val instructor: String,
  val section: String,
  val colorHex: Long,
  val notes: String
) {
  fun toClassRoutine(): ClassRoutine = ClassRoutine(
    id = id,
    courseCode = courseCode,
    courseTitle = courseTitle,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime,
    room = room,
    instructor = instructor,
    section = section,
    colorHex = colorHex,
    notes = notes
  )

  companion object {
    fun fromClassRoutine(routine: ClassRoutine): RoutineEntity = RoutineEntity(
      id = routine.id,
      courseCode = routine.courseCode,
      courseTitle = routine.courseTitle,
      dayOfWeek = routine.dayOfWeek,
      startTime = routine.startTime,
      endTime = routine.endTime,
      room = routine.room,
      instructor = routine.instructor,
      section = routine.section,
      colorHex = routine.colorHex,
      notes = routine.notes
    )
  }
}
