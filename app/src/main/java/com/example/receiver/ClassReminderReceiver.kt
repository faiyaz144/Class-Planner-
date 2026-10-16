package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class ClassReminderReceiver : BroadcastReceiver() {

  companion object {
    const val CHANNEL_ID = "class_reminders_channel"
    const val CHANNEL_NAME = "Class Reminders"

    const val EXTRA_ROUTINE_ID = "extra_routine_id"
    const val EXTRA_COURSE_CODE = "extra_course_code"
    const val EXTRA_COURSE_TITLE = "extra_course_title"
    const val EXTRA_START_TIME = "extra_start_time"
    const val EXTRA_ROOM = "extra_room"
    const val EXTRA_LEAD_MINUTES = "extra_lead_minutes"
  }

  override fun onReceive(context: Context, intent: Intent) {
    val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID) ?: "default_class"
    val courseCode = intent.getStringExtra(EXTRA_COURSE_CODE) ?: "Class"
    val courseTitle = intent.getStringExtra(EXTRA_COURSE_TITLE) ?: "Scheduled Lecture"
    val startTime = intent.getStringExtra(EXTRA_START_TIME) ?: "Soon"
    val room = intent.getStringExtra(EXTRA_ROOM).orEmpty()
    val leadMinutes = intent.getIntExtra(EXTRA_LEAD_MINUTES, 10)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create Notification Channel for Android O+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
      if (existingChannel == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = "Timely reminders before scheduled university classes"
          enableLights(true)
          enableVibration(true)
          vibrationPattern = longArrayOf(0, 300, 200, 300)
        }
        notificationManager.createNotificationChannel(channel)
      }
    }

    // Launch MainActivity when tapped
    val tapIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      putExtra("open_tab", "today")
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      routineId.hashCode(),
      tapIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val roomText = if (room.isNotBlank()) " in $room" else ""
    val titleText = "Class in $leadMinutes mins: $courseCode"
    val contentText = "$courseTitle starts at $startTime$roomText"

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(titleText)
      .setContentText(contentText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setSound(defaultSoundUri)
      .setVibrate(longArrayOf(0, 300, 200, 300))
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    val notificationId = routineId.hashCode()
    notificationManager.notify(notificationId, notification)
  }
}
