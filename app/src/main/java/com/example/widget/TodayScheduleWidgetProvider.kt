package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.db.RoutineDatabase
import com.example.data.model.ClassRoutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TodayScheduleWidgetProvider : AppWidgetProvider() {

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    updateWidgets(context, appWidgetManager, appWidgetIds)
  }

  companion object {
    fun updateAllWidgets(context: Context) {
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val widgetComponent = ComponentName(context, TodayScheduleWidgetProvider::class.java)
      val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
      if (widgetIds != null && widgetIds.isNotEmpty()) {
        updateWidgets(context, appWidgetManager, widgetIds)
      }
    }

    private fun updateWidgets(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray
    ) {
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val db = RoutineDatabase.getDatabase(context)
          val allRoutines = db.routineDao().getAllRoutines().first().map { it.toClassRoutine() }

          val todayName = getTodayDayOfWeek()
          val dateFormatted = SimpleDateFormat("EEE, d MMM", Locale.US).format(Date())

          val todayRoutines = allRoutines
            .filter { it.dayOfWeek.equals(todayName, ignoreCase = true) }
            .sortedBy { it.startMinutes }

          val cal = Calendar.getInstance()
          val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
          val nextClass = todayRoutines.firstOrNull { it.startMinutes > currentMinutes }

          for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_today_schedule)

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
              putExtra("open_tab", "today")
            }
            val pendingIntent = PendingIntent.getActivity(
              context,
              0,
              openAppIntent,
              PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            views.setTextViewText(R.id.widget_title, "Today's Schedule")
            val subtitleText = if (nextClass != null) {
              "$dateFormatted · Next at ${nextClass.startTime}"
            } else {
              "$dateFormatted · $todayName"
            }
            views.setTextViewText(R.id.widget_subtitle, subtitleText)

            val classCount = todayRoutines.size
            views.setTextViewText(
              R.id.widget_badge,
              if (classCount == 1) "1 class" else "$classCount classes"
            )

            if (todayRoutines.isEmpty()) {
              views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE)
              views.setViewVisibility(R.id.widget_items_container, View.GONE)
            } else {
              views.setViewVisibility(R.id.widget_empty_text, View.GONE)
              views.setViewVisibility(R.id.widget_items_container, View.VISIBLE)

              // Item 1
              val item1 = todayRoutines.getOrNull(0)
              if (item1 != null) {
                views.setViewVisibility(R.id.widget_row_1, View.VISIBLE)
                views.setTextViewText(R.id.widget_code_1, item1.courseCode)
                views.setTextViewText(R.id.widget_name_1, item1.courseTitle)
                val roomNote = if (item1.room.isNotBlank()) " · ${item1.room}" else ""
                views.setTextViewText(R.id.widget_time_1, "${item1.startTime}$roomNote")
              } else {
                views.setViewVisibility(R.id.widget_row_1, View.GONE)
              }

              // Item 2
              val item2 = todayRoutines.getOrNull(1)
              if (item2 != null) {
                views.setViewVisibility(R.id.widget_row_2, View.VISIBLE)
                views.setTextViewText(R.id.widget_code_2, item2.courseCode)
                views.setTextViewText(R.id.widget_name_2, item2.courseTitle)
                val roomNote = if (item2.room.isNotBlank()) " · ${item2.room}" else ""
                views.setTextViewText(R.id.widget_time_2, "${item2.startTime}$roomNote")
              } else {
                views.setViewVisibility(R.id.widget_row_2, View.GONE)
              }

              // Item 3
              val item3 = todayRoutines.getOrNull(2)
              if (item3 != null) {
                views.setViewVisibility(R.id.widget_row_3, View.VISIBLE)
                views.setTextViewText(R.id.widget_code_3, item3.courseCode)
                views.setTextViewText(R.id.widget_name_3, item3.courseTitle)
                val roomNote = if (item3.room.isNotBlank()) " · ${item3.room}" else ""
                views.setTextViewText(R.id.widget_time_3, "${item3.startTime}$roomNote")
              } else {
                views.setViewVisibility(R.id.widget_row_3, View.GONE)
              }

              // More count
              if (todayRoutines.size > 3) {
                val remaining = todayRoutines.size - 3
                views.setViewVisibility(R.id.widget_more_count, View.VISIBLE)
                views.setTextViewText(
                  R.id.widget_more_count,
                  if (remaining == 1) "+ 1 more class today" else "+ $remaining more classes today"
                )
              } else {
                views.setViewVisibility(R.id.widget_more_count, View.GONE)
              }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
          }
        } catch (_: Exception) {
        }
      }
    }

    private fun getTodayDayOfWeek(): String {
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
