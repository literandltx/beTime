package com.literandltx.betime.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.literandltx.betime.TimerApplication
import com.literandltx.betime.ui.screens.activity.ActivityViewModel
import com.literandltx.betime.ui.screens.home.HomeViewModel
import com.literandltx.betime.ui.screens.logs.LogsViewModel
import com.literandltx.betime.ui.screens.preset.PresetViewModel
import com.literandltx.betime.ui.screens.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PresetViewModel(
                labelsRepository = timerApplication().container.labelsRepository,
                timerOptionsRepository = timerApplication().container.timerOptionRepository,
                activePresetRepository = timerApplication().container.activePresetRepository
            )
        }

        initializer {
            HomeViewModel(
                labelsRepository = timerApplication().container.labelsRepository,
                timerOptionsRepository = timerApplication().container.timerOptionRepository,
                activePresetRepository = timerApplication().container.activePresetRepository,
                timerRecordRepository = timerApplication().container.timerRecordRepository
            )
        }

        initializer {
            LogsViewModel(
                labelsRepository = timerApplication().container.labelsRepository,
                timerRecordRepository = timerApplication().container.timerRecordRepository,
                settingsPreferences = timerApplication().container.settingsPreferences
            )
        }

        initializer {
            ActivityViewModel(
                labelsRepository = timerApplication().container.labelsRepository,
                timerRecordRepository = timerApplication().container.timerRecordRepository,
                settingsPreferences = timerApplication().container.settingsPreferences
            )
        }

        initializer {
            SettingsViewModel(
                settingsPreferences = timerApplication().container.settingsPreferences
            )
        }
    }
}

fun CreationExtras.timerApplication(): TimerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TimerApplication)
