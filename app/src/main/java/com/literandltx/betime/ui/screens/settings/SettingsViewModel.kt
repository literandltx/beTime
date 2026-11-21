package com.literandltx.betime.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.SettingsPreferences
import com.literandltx.betime.util.STOP_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = settingsPreferences.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = AppThemeMode.DARK
        )

    val isBottomNavEnabled: StateFlow<Boolean> = settingsPreferences.bottomNavEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true
        )

    val isBottomTimerButtonEnabled: StateFlow<Boolean> = settingsPreferences.bottomTimerButtonEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true
        )

    val isBottomLabelButtonEnabled: StateFlow<Boolean> = settingsPreferences.bottomLabelButtonEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true
        )

    val isLogsFabEnabled: StateFlow<Boolean> = settingsPreferences.logsFabEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = false
        )

    val isTimerDotEnabled: StateFlow<Boolean> = settingsPreferences.timerDotEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true
        )

    val isTimerHintsEnabled: StateFlow<Boolean> = settingsPreferences.timerHintsEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true
        )

    val isTimerStartIconEnabled: StateFlow<Boolean> = settingsPreferences.timerStartIconEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = SettingsPreferences.DEFAULT_TIMER_START_ICON_ENABLED
        )

    val isTimerDoubleClickEnabled: StateFlow<Boolean> = settingsPreferences.timerDoubleClickEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = SettingsPreferences.DEFAULT_TIMER_DOUBLE_CLICK_ENABLED
        )

    val isTimerSwipeBottomEnabled: StateFlow<Boolean> = settingsPreferences.timerSwipeBottomEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = SettingsPreferences.DEFAULT_TIMER_SWIPE_BOTTOM_ENABLED
        )

    fun setTimerStartIconEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setTimerStartIconEnabled(enabled)
        }
    }

    fun setTimerDoubleClickEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setTimerDoubleClickEnabled(enabled)
        }
    }

    fun setTimerSwipeBottomEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setTimerSwipeBottomEnabled(enabled)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsPreferences.setThemeMode(mode)
        }
    }

    fun setBottomNavEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setBottomNavEnabled(enabled)
        }
    }

    fun setBottomTimerButtonEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setBottomTimerButtonEnabled(enabled)
        }
    }

    fun setBottomLabelButtonEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setBottomLabelButtonEnabled(enabled)
        }
    }

    fun setLogsFabEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setLogsFabEnabled(enabled)
        }
    }

    fun setTimerDotEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setTimerDotEnabled(enabled)
        }
    }

    fun setTimerHintsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setTimerHintsEnabled(enabled)
        }
    }

}
