package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.db.RoutineDatabase
import com.example.util.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val db = RoutineDatabase.getDatabase(context)
          val routines = db.routineDao().getAllRoutines().first().map { it.toClassRoutine() }
          NotificationScheduler.rescheduleAll(context, routines, leadMinutes = 10, notificationsEnabled = true)
        } catch (_: Exception) {
        }
      }
    }
  }
}
