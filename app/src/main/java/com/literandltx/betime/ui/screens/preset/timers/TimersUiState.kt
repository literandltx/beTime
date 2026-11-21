package com.literandltx.betime.ui.screens.preset.timers

import com.literandltx.betime.data.model.TimerOption

data class TimersListState(
    val timerOptions: List<TimerOption>,
    val activeTimerId: Int?,
    val isAdding: Boolean,
    val isEditing: Boolean,
    val maxRows: Int = 2
)

data class TimersListActions(
    val onConfirmAdd: (value: Int) -> Unit,
    val onCancelAdd: () -> Unit,
    val onSelectClick: (TimerOption) -> Unit,
    val onDeleteClick: (TimerOption) -> Unit
)
