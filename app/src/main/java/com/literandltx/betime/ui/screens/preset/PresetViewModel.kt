package com.literandltx.betime.ui.screens.preset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.repository.TimerOptionsRepository
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.repository.ActivePresetRepository
import com.literandltx.betime.data.model.ActivePresetWithDetails
import com.literandltx.betime.util.STOP_TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PresetUiState(
    val labelList: List<Label> = emptyList(),
    val timerOptions: List<TimerOption> = emptyList(),
    val activePreset: ActivePresetWithDetails? = null
)

class PresetViewModel(
    private val labelsRepository: LabelsRepository,
    private val timerOptionsRepository: TimerOptionsRepository,
    private val activePresetRepository: ActivePresetRepository
) : ViewModel() {

    val presetUiState: StateFlow<PresetUiState> = combine(
        labelsRepository.getAllLabelsStream(),
        timerOptionsRepository.getAllTimerOption(),
        activePresetRepository.observeActivePresetWithDetails()
    ) { labels, options, activePreset ->
        PresetUiState(
            labelList = labels,
            timerOptions = options,
            activePreset = activePreset
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = PresetUiState()
    )

    fun insert(label: Label) {
        viewModelScope.launch {
            val currentCount = presetUiState.value.labelList.size
            labelsRepository.insertLabel(label.copy(orderIndex = currentCount))
        }
    }

    fun insert(timerOption: TimerOption) {
        viewModelScope.launch {
            timerOptionsRepository.insert(timerOption)
        }
    }

    fun update(label: Label) {
        viewModelScope.launch {
            labelsRepository.updateLabel(label)
        }
    }

    fun update(timerOption: TimerOption) {
        viewModelScope.launch {
            timerOptionsRepository.update(timerOption)
        }
    }

    fun delete(label: Label) {
        viewModelScope.launch {
            labelsRepository.deleteLabel(label)
        }
    }

    fun delete(timerOption: TimerOption) {
        viewModelScope.launch {
            timerOptionsRepository.delete(timerOption)
        }
    }

    fun setAsActive(label: Label) {
        viewModelScope.launch {
            val currentPreset = activePresetRepository.getActivePresetWithDetails()?.activePreset ?: ActivePreset()
            activePresetRepository.setActivePreset(currentPreset.copy(labelId = label.id))
        }
    }

    fun setAsActive(timerOption: TimerOption) {
        viewModelScope.launch {
            val currentPreset = activePresetRepository.getActivePresetWithDetails()?.activePreset ?: ActivePreset()
            activePresetRepository.setActivePreset(currentPreset.copy(timerOptionId = timerOption.id))
        }
    }

    fun reorderLabels(reorderedList: List<Label>) {
        viewModelScope.launch {
            val updated = reorderedList.mapIndexed { index, label ->
                label.copy(orderIndex = index)
            }
            labelsRepository.updateLabels(updated)
        }
    }
}
