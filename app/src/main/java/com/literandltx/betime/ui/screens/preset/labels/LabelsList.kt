package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.literandltx.betime.R
import sh.calvin.reorderable.ReorderableItem

@Composable
fun LabelsList(
    state: LabelsListState,
    actions: LabelsListActions,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = state.lazyListState,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (state.isAdding && state.addingParentId == null) {
            item(key = "add_label_form") {
                AddLabelItem(
                    parentId = null,
                    onConfirm = actions.onConfirmAdd,
                    onCancel = actions.onCancelAdd
                )
            }
        }

        if (state.list.isEmpty() && !state.isAdding) {
            item(key = "empty_state") {
                EmptyLabelsItem()
            }
        }

        items(
            items = state.list,
            key = { it.id }
        ) { label ->
            ReorderableItem(state.reorderableLazyColumnState, key = label.id) { _ ->
                if (state.editingLabelId == label.id) {
                    EditorLabelCard(
                        initialName = label.name,
                        initialColor = label.composeColor,
                        parentId = label.parentId,
                        onConfirm = { updatedName, updatedColor, updatedParentId ->
                            actions.onConfirmEdit(
                                label.copy(
                                    name = updatedName,
                                    colorArgb = updatedColor.toArgb(),
                                    parentId = updatedParentId
                                )
                            )
                        },
                        onCancel = actions.onCancelEdit
                    )
                } else {
                    if (!label.isUncertainOnly) {
                        LabelCard(
                            label = label,
                            isActive = label.id == state.activeLabelId,
                            hasSubLabels = label.parentId == null && label.id in state.parentIdsWithSubLabels,
                            isSubLabelsCollapsed = label.id in state.collapsedParentIds,
                            dragModifier = Modifier.draggableHandle(
                                onDragStopped = { actions.onReorderFinished(state.list.toList()) }
                            ),
                            onToggleCollapseSubLabels = { actions.onToggleCollapseSubLabels(label.id) },
                            onAddSubLabelClick = { actions.onStartAddSubLabel(label.id) },
                            onEditClick = { actions.onStartEdit(label) },
                            onDeleteClick = { actions.onDeleteClick(label) },
                            onToggleClick = { actions.onToggleClick(label) }
                        )
                    }
                }
            }

            if (state.isAdding && state.addingParentId == label.id) {
                AddLabelItem(
                    parentId = label.id,
                    onConfirm = actions.onConfirmAdd,
                    onCancel = actions.onCancelAdd,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AddLabelItem(
    parentId: Int?,
    onConfirm: (name: String, color: Color, parentId: Int?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    EditorLabelCard(
        initialName = "",
        initialColor = PRESET_LABEL_COLORS.first(),
        parentId = parentId,
        placeholder = stringResource(R.string.newLabelPlaceholder),
        onConfirm = onConfirm,
        onCancel = onCancel,
        modifier = modifier
    )
}

@Composable
private fun EmptyLabelsItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.noItemsMessage),
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}
