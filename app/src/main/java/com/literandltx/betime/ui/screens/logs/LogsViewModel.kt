package com.literandltx.betime.ui.screens.logs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.preferences.SettingsPreferences
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.repository.TimerRecordRepository
import com.literandltx.betime.util.CsvExporter
import com.literandltx.betime.util.CsvImporter
import com.literandltx.betime.util.STOP_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LogsUiState(
    val labels: List<com.literandltx.betime.data.model.Label> = emptyList(),
    val records: List<com.literandltx.betime.data.model.TimerRecordDetails> = emptyList(),
    val isFabEnabled: Boolean = false
)

class LogsViewModel(
    private val labelsRepository: LabelsRepository,
    private val timerRecordRepository: TimerRecordRepository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val logsUiState: StateFlow<LogsUiState> = combine(
        labelsRepository.getAllLabelsStream(),
        timerRecordRepository.getAllTimerRecords(),
        settingsPreferences.logsFabEnabledFlow
    ) { labels, records, isFabEnabled ->
        Log.d(
            "TimerDebug",
            "LogsViewModel State Update: Fetched ${labels.size} labels, ${records.size} records, FAB: $isFabEnabled."
        )

        LogsUiState(
            labels = labels,
            records = records,
            isFabEnabled = isFabEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = LogsUiState()
    )

    fun insert(timerRecord: TimerRecord) {
        viewModelScope.launch {
            timerRecordRepository.insert(timerRecord)
        }
    }

    fun update(timerRecord: TimerRecord) {
        viewModelScope.launch {
            timerRecordRepository.update(timerRecord)
        }
    }

    fun delete(timerRecord: TimerRecord) {
        viewModelScope.launch {
            timerRecordRepository.delete(timerRecord)
        }
    }

    fun exportCsvContent(): String {
        return CsvExporter.exportCsvContent(
            logsUiState.value.labels,
            logsUiState.value.records.map { it.record }
        )
    }

    fun importCsvContent(text: String) {
        val importedData = CsvImporter.parseCsvContent(text)

        viewModelScope.launch {
            if (importedData.labels.isNotEmpty()) {
                labelsRepository.insertAll(importedData.labels)
            }
            if (importedData.records.isNotEmpty()) {
                timerRecordRepository.insertAll(importedData.records)
            }
        }
    }
}
