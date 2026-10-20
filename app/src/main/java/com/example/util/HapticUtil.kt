package com.example.util

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Provides clean, modern tactile haptic feedback for user interactions across the app.
 */
class AppHapticFeedback(private val view: View) {

  init {
    view.isHapticFeedbackEnabled = true
  }

  /**
   * Subtle tick for tab changes, day capsule toggles, date changes, and switches.
   */
  fun tick() {
    view.performHapticFeedback(
      HapticFeedbackConstants.CLOCK_TICK,
      HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
    )
  }

  /**
   * Standard key/button press tap for standard clicks.
   */
  fun click() {
    view.performHapticFeedback(
      HapticFeedbackConstants.KEYBOARD_TAP,
      HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
    )
  }

  /**
   * Pleasant success confirmation haptic (e.g. routine scan complete, class saved).
   */
  fun confirm() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      view.performHapticFeedback(
        HapticFeedbackConstants.CONFIRM,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
      )
    } else {
      view.performHapticFeedback(
        HapticFeedbackConstants.VIRTUAL_KEY,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
      )
    }
  }

  /**
   * Alert / reject haptic (e.g. deletion, routine error, schedule clash).
   */
  fun reject() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      view.performHapticFeedback(
        HapticFeedbackConstants.REJECT,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
      )
    } else {
      view.performHapticFeedback(
        HapticFeedbackConstants.LONG_PRESS,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
      )
    }
  }

  /**
   * Heavy feedback for primary actions or long-press events.
   */
  fun heavy() {
    view.performHapticFeedback(
      HapticFeedbackConstants.LONG_PRESS,
      HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
    )
  }
}

@Composable
fun rememberAppHaptic(): AppHapticFeedback {
  val view = LocalView.current
  return remember(view) { AppHapticFeedback(view) }
}
