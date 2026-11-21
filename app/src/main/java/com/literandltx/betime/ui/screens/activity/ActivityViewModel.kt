package com.literandltx.betime.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.SettingsPreferences
import com.literandltx.betime.data.repository.TimerRecordRepository
import com.literandltx.betime.ui.screens.activity.widgets.WidgetType
import com.literandltx.betime.util.STOP_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActivityUiState(
    val labels: List<Label> = emptyList(),
    val records: List<TimerRecordDetails> = emptyList(),
    val widgetOrder: List<WidgetType> = SettingsPreferences.DEFAULT_ACTIVITY_WIDGET_ORDER,
    val isBottomNavEnabled: Boolean = SettingsPreferences.DEFAULT_BOTTOM_NAV_ENABLED
)

class ActivityViewModel(
    labelsRepository: LabelsRepository,
    timerRecordRepository: TimerRecordRepository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val activityUiState: StateFlow<ActivityUiState> = combine(
        labelsRepository.getAllLabelsStream(),
        timerRecordRepository.getAllTimerRecords(),
        settingsPreferences.activityWidgetOrderFlow,
        settingsPreferences.bottomNavEnabledFlow
    ) { labels, records, widgetOrder, isBottomNavEnabled ->
        ActivityUiState(
            labels = labels,
            records = records,
            widgetOrder = widgetOrder,
            isBottomNavEnabled = isBottomNavEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ActivityUiState()
    )

    fun updateWidgetOrder(newOrder: List<WidgetType>) {
        viewModelScope.launch {
            settingsPreferences.setActivityWidgetOrder(newOrder)
        }
    }
}
