package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R

@Composable
fun TimersList(
    state: TimersListState,
    actions: TimersListActions,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (state.isAdding) {
            AddTimerOptionItem(
                onConfirm = actions.onConfirmAdd,
                onCancel = actions.onCancelAdd
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_standard)))
        }

        if (state.timerOptions.isNotEmpty()) {
            ScrollableFlowRow(
                maxRows = state.maxRows,
                modifier = Modifier.fillMaxWidth()
            ) {
                state.timerOptions.forEach { option ->
                    TimerCard(
                        text = stringResource(R.string.timerOptionFormat, option.value),
                        isEditing = state.isEditing,
                        isActive = state.activeTimerId == option.id,
                        onSelectClick = { actions.onSelectClick(option) },
                        onDeleteClick = { actions.onDeleteClick(option) }
                    )
                }
            }
        } else {
            EmptyTimersItem()
        }
    }
}

@Composable
fun ScrollableFlowRow(
    modifier: Modifier = Modifier,
    maxRows: Int = 2,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val maxAllowedWidth = constraints.maxWidth

        val horizontalSpacingDp = dimensionResource(R.dimen.timer_item_horizontal_spacing)
        val verticalSpacingDp = dimensionResource(R.dimen.timer_item_vertical_spacing)

        Layout(
            content = content,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = dimensionResource(R.dimen.padding_small))
        ) { measurables, layoutConstraints ->
            val horizontalSpacingPx = horizontalSpacingDp.toPx().toInt()
            val verticalSpacingPx = verticalSpacingDp.toPx().toInt()

            val placeable = measurables.map {
                it.measure(layoutConstraints.copy(maxWidth = Constraints.Infinity))
            }

            val rowWidths = IntArray(maxRows)
            val rowHeights = IntArray(maxRows)
            val placeableCoordinates = mutableListOf<Pair<Int, Int>>()

            var currentRow = 0

            placeable.forEach { placeable ->
                val spacing = if (rowWidths[currentRow] > 0) horizontalSpacingPx else 0
                if (rowWidths[currentRow] + spacing + placeable.width > maxAllowedWidth) {
                    if (currentRow < maxRows - 1) {
                        currentRow++
                    } else {
                        var minWidth = rowWidths[0]
                        var minRow = 0
                        for (i in 1 until maxRows) {
                            if (rowWidths[i] < minWidth) {
                                minWidth = rowWidths[i]
                                minRow = i
                            }
                        }
                        currentRow = minRow
                    }
                }

                val currentSpacing = if (rowWidths[currentRow] > 0) horizontalSpacingPx else 0
                placeableCoordinates.add(Pair(rowWidths[currentRow] + currentSpacing, currentRow))
                rowWidths[currentRow] += currentSpacing + placeable.width
                rowHeights[currentRow] = maxOf(rowHeights[currentRow], placeable.height)
            }

            val rowYPositions = IntArray(maxRows)
            for (i in 1 until maxRows) {
                rowYPositions[i] = rowYPositions[i - 1] + rowHeights[i - 1] + verticalSpacingPx
            }

            val totalWidth = rowWidths.maxOrNull() ?: 0
            val totalHeight = rowHeights.sum() + (verticalSpacingPx * (maxRows - 1))

            val rowXOffsets = IntArray(maxRows) { i ->
                maxOf(0, (totalWidth - rowWidths[i]) / 2)
            }

            layout(totalWidth, totalHeight) {
                placeable.forEachIndexed { index, placeable ->
                    val (x, rowIndex) = placeableCoordinates[index]
                    val centeredX = x + rowXOffsets[rowIndex]
                    val y = rowYPositions[rowIndex]
                    placeable.placeRelative(centeredX, y)
                }
            }
        }
    }
}

@Composable
private fun AddTimerOptionItem(
    onConfirm: (value: Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    EditorTimerCard(
        initialValue = 25,
        placeholder = stringResource(R.string.newTimerPlaceholder),
        onConfirm = onConfirm,
        onCancel = onCancel,
        modifier = modifier
    )
}

@Composable
private fun EmptyTimersItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_large)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.noItemsMessage),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
