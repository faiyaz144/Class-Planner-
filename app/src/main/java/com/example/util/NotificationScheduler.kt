package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.ClassRoutine
import com.example.receiver.ClassReminderReceiver
import java.util.Calendar

object NotificationScheduler {

  private fun dayNameToCalendarDay(dayName: String): Int {
    return when (dayName.lowercase()) {
      "sunday" -> Calendar.SUNDAY
      "monday" -> Calendar.MONDAY
      "tuesday" -> Calendar.TUESDAY
      "wednesday" -> Calendar.WEDNESDAY
      "thursday" -> Calendar.THURSDAY
      "friday" -> Calendar.FRIDAY
      "saturday" -> Calendar.SATURDAY
      else -> Calendar.MONDAY
    }
  }

  fun scheduleClassReminder(
    context: Context,
    routine: ClassRoutine,
    leadMinutes: Int = 10
  ) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val targetDayOfWeek = dayNameToCalendarDay(routine.dayOfWeek)
    val startMinutes = routine.startMinutes
    val notifyMinutes = (startMinutes - leadMinutes).coerceAtLeast(0)

    val targetHour = notifyMinutes / 60
    val targetMinute = notifyMinutes % 60

    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, targetHour)
      set(Calendar.MINUTE, targetMinute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)

      val currentDayOfWeek = get(Calendar.DAY_OF_WEEK)
      var daysUntil = (targetDayOfWeek - currentDayOfWeek + 7) % 7
      if (daysUntil == 0 && timeInMillis <= System.currentTimeMillis()) {
        daysUntil = 7
      }
      add(Calendar.DAY_OF_MONTH, daysUntil)
    }

    val intent = Intent(context, ClassReminderReceiver::class.java).apply {
      putExtra(ClassReminderReceiver.EXTRA_ROUTINE_ID, routine.id)
      putExtra(ClassReminderReceiver.EXTRA_COURSE_CODE, routine.courseCode)
      putExtra(ClassReminderReceiver.EXTRA_COURSE_TITLE, routine.courseTitle)
      putExtra(ClassReminderReceiver.EXTRA_START_TIME, routine.startTime)
      putExtra(ClassReminderReceiver.EXTRA_ROOM, routine.room)
      putExtra(ClassReminderReceiver.EXTRA_LEAD_MINUTES, leadMinutes)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      routine.id.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          pendingIntent
        )
      } else {
        alarmManager.setExact(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          pendingIntent
        )
      }
    } catch (_: SecurityException) {
      alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
      )
    }
  }

  fun cancelClassReminder(context: Context, routineId: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ClassReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      routineId.hashCode(),
      intent,
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()
    }
  }

  fun rescheduleAll(
    context: Context,
    routines: List<ClassRoutine>,
    leadMinutes: Int = 10,
    notificationsEnabled: Boolean = true
  ) {
    routines.forEach { routine ->
      cancelClassReminder(context, routine.id)
      if (notificationsEnabled) {
        scheduleClassReminder(context, routine, leadMinutes)
      }
    }
  }

  fun sendImmediateTestNotification(
    context: Context,
    leadMinutes: Int = 10,
    courseCode: String = "CSC 497",
    courseTitle: String = "Compiler Design",
    startTime: String = "9:35 AM",
    room: String = "Room 607"
  ) {
    val intent = Intent(context, ClassReminderReceiver::class.java).apply {
      putExtra(ClassReminderReceiver.EXTRA_ROUTINE_ID, "test_notification_${System.currentTimeMillis()}")
      putExtra(ClassReminderReceiver.EXTRA_COURSE_CODE, courseCode)
      putExtra(ClassReminderReceiver.EXTRA_COURSE_TITLE, courseTitle)
      putExtra(ClassReminderReceiver.EXTRA_START_TIME, startTime)
      putExtra(ClassReminderReceiver.EXTRA_ROOM, room)
      putExtra(ClassReminderReceiver.EXTRA_LEAD_MINUTES, leadMinutes)
    }
    context.sendBroadcast(intent)
  }
}
