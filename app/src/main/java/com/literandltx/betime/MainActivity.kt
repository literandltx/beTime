package com.literandltx.betime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.screens.settings.SettingsViewModel
import com.literandltx.betime.ui.theme.TimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle(initialValue = AppThemeMode.SYSTEM)

            TimerTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TimerApp()
                }
            }
        }
    }
}