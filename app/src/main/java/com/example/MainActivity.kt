package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
      val oledPitchBlackEnabled by viewModel.oledPitchBlackEnabled.collectAsStateWithLifecycle()
      val accentOverrideHex by viewModel.accentOverrideHex.collectAsStateWithLifecycle()

      MyApplicationTheme(
        themeMode = currentTheme,
        forceOledBlack = oledPitchBlackEnabled,
        accentOverrideHex = accentOverrideHex
      ) {
        MainScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
      }
    }
  }
}
