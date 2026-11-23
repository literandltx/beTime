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
    addingParentId: Int?,
    editingLabelId: Int?,
    onConfirmAdd: (name: String, color: Color, parentId: Int?) -> Unit,
    onCancelAdd: () -> Unit,
    onStartAddSubLabel: (parentId: Int) -> Unit,
    onStartEdit: (Label) -> Unit,
    onConfirmEdit: (Label) -> Unit,
    onCancelEdit: () -> Unit,
    onReorderFinished: (List<Label>) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Label) -> Unit = {},
    onToggleClick: (Label) -> Unit = {}
) {
    val orderedHierarchy = remember(labels) {
        val roots = labels.filter { it.parentId == null }.sortedBy { it.orderIndex }
        val subsByParent = labels
            .filter { it.parentId != null }
            .sortedBy { it.orderIndex }
            .groupBy { it.parentId }

        buildList {
            roots.forEach { root ->
                add(root)
                addAll(subsByParent[root.id].orEmpty())
            }
        }
    }

    val list = remember(orderedHierarchy) { orderedHierarchy.toMutableStateList() }

    LaunchedEffect(orderedHierarchy) {
        if (list != orderedHierarchy) {
            list.clear()
            list.addAll(orderedHierarchy)
        }
    }

    val lazyListState = rememberLazyListState()
    val hasTopAddHeader = if (isAdding && addingParentId == null) 1 else 0

    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = from.index - hasTopAddHeader
        val toIndex = to.index - hasTopAddHeader

        if (fromIndex in list.indices && toIndex in list.indices) {
            val movingItem = list[fromIndex]
            val targetItem = list[toIndex]
            if (movingItem.parentId == targetItem.parentId) {
                list.add(toIndex, list.removeAt(fromIndex))
            }
        }
    }

    LaunchedEffect(isAdding, addingParentId) {
        if (isAdding && addingParentId == null && lazyListState.firstVisibleItemIndex > 0) {
            lazyListState.scrollToItem(index = 0, scrollOffset = 0)
        }
    }

    LaunchedEffect(editingLabelId) {
        if (isAdding || editingLabelId == null) return@LaunchedEffect

        val labelIndex = list.indexOfFirst { it.id == editingLabelId }
        if (labelIndex < 0) return@LaunchedEffect

        lazyListState.animateScrollToItem(index = hasTopAddHeader + labelIndex)
    }

    LabelsList(
        state = LabelsListState(
            list = list,
            activeLabelId = activeLabelId,
            isAdding = isAdding,
            addingParentId = addingParentId,
            editingLabelId = editingLabelId,
            lazyListState = lazyListState,
            reorderableLazyColumnState = reorderableLazyColumnState
        ),
        actions = LabelsListActions(
            onConfirmAdd = onConfirmAdd,
            onCancelAdd = onCancelAdd,
            onStartAddSubLabel = onStartAddSubLabel,
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
