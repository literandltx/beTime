package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import com.literandltx.betime.data.model.Label
import sh.calvin.reorderable.ReorderableLazyListState

data class LabelsListState(
    val list: List<Label>,
    val activeLabelId: Int?,
    val isAdding: Boolean,
    val addingParentId: Int?,
    val editingLabelId: Int?,
    val lazyListState: LazyListState,
    val reorderableLazyColumnState: ReorderableLazyListState
)

data class LabelsListActions(
    val onConfirmAdd: (name: String, color: Color, parentId: Int?) -> Unit,
    val onCancelAdd: () -> Unit,
    val onStartAddSubLabel: (parentId: Int) -> Unit,
    val onStartEdit: (Label) -> Unit,
    val onConfirmEdit: (Label) -> Unit,
    val onCancelEdit: () -> Unit,
    val onReorderFinished: (List<Label>) -> Unit,
    val onDeleteClick: (Label) -> Unit,
    val onToggleClick: (Label) -> Unit
)
