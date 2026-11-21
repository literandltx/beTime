package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.literandltx.betime.data.model.Label
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun LabelsContent(
    labels: List<Label>,
    activeLabelId: Int?,
    isAdding: Boolean,
    editingLabelId: Int?,
    onConfirmAdd: (name: String, color: Color) -> Unit,
    onCancelAdd: () -> Unit,
    onStartEdit: (Label) -> Unit,
    onConfirmEdit: (Label) -> Unit,
    onCancelEdit: () -> Unit,
    onReorderFinished: (List<Label>) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Label) -> Unit = {},
    onToggleClick: (Label) -> Unit = {}
) {
    val list = remember(labels) { labels.toMutableStateList() }

    LaunchedEffect(labels) {
        if (list != labels) {
            list.clear()
            list.addAll(labels)
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val offset = if (isAdding) 1 else 0
        val fromIndex = from.index - offset
        val toIndex = to.index - offset

        if (fromIndex in list.indices && toIndex in list.indices) {
            list.add(toIndex, list.removeAt(fromIndex))
        }
    }

    LabelsList(
        state = LabelsListState(
            list = list,
            activeLabelId = activeLabelId,
            isAdding = isAdding,
            editingLabelId = editingLabelId,
            lazyListState = lazyListState,
            reorderableLazyColumnState = reorderableLazyColumnState
        ),
        actions = LabelsListActions(
            onConfirmAdd = onConfirmAdd,
            onCancelAdd = onCancelAdd,
            onStartEdit = onStartEdit,
            onConfirmEdit = onConfirmEdit,
            onCancelEdit = onCancelEdit,
            onReorderFinished = onReorderFinished,
            onDeleteClick = onDeleteClick,
            onToggleClick = onToggleClick
        ),
        modifier = modifier
    )
}
