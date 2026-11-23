package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewProvider
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewState
import com.literandltx.betime.ui.screens.activity.shared.LabelSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.RangeSelectorButton
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.shared.LabelIconButton
import com.literandltx.betime.ui.shared.NextIconButton
import com.literandltx.betime.ui.shared.PreviousIconButton
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun DonutHeader(
    timeOffset: Int,
    labels: List<Label>,
    selectedTimeSpan: TimeSpan,
    selectedLabels: Set<Label>,
    groupSubLabels: Boolean,
    onToggleGroupSubLabels: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onRangeClick: () -> Unit,
    onLabelsChanged: (Set<Label>) -> Unit,
    modifier: Modifier = Modifier,
    customStartDate: Long = 0L,
    customEndDate: Long = 0L,
    isPrevEnabled: Boolean = true,
    isNextEnabled: Boolean = true
) {
    val hasSubLabels = remember(labels) { labels.any { it.parentId != null } }

    Row(
        modifier = modifier
            .padding(0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PreviousIconButton(
            onClick = onPrevClick,
            isEnabled = isPrevEnabled
        )

        DonutLabelToggleButton(hasSubLabels, onToggleGroupSubLabels, groupSubLabels)

        RangeSelectorButton(
            selectedTimeSpan = selectedTimeSpan,
            timeOffset = timeOffset,
            customStartDate = customStartDate,
            customEndDate = customEndDate,
            onClick = onRangeClick,
            modifier = Modifier.weight(1f)
        )

        LabelSelector(
            labels = labels,
            selectedLabels = selectedLabels,
            onLabelsChanged = onLabelsChanged
        )

        NextIconButton(
            onClick = onNextClick,
            isEnabled = isNextEnabled
        )
    }
}

@Composable
private fun DonutLabelToggleButton(
    hasSubLabels: Boolean,
    onToggleGroupSubLabels: () -> Unit,
    groupSubLabels: Boolean
) {
    if (hasSubLabels) {
        IconButton(
            onClick = onToggleGroupSubLabels,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (groupSubLabels) {
                    Icons.Outlined.AccountTree
                } else {
                    Icons.AutoMirrored.Outlined.FormatListBulleted
                },
                contentDescription = stringResource(
                    if (groupSubLabels) {
                        R.string.detailedSubLabelsMode
                    } else {
                        R.string.groupedSubLabelsMode
                    }
                ),
                tint = if (groupSubLabels) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LabelSelector(
    labels: List<Label>,
    selectedLabels: Set<Label>,
    onLabelsChanged: (Set<Label>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isModalOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LabelIconButton(
            label = if (selectedLabels.size == 1) selectedLabels.first() else null,
            onClick = { isModalOpen = true },
            modifier = Modifier.size(36.dp)
        )

        LabelSelectorModal(
            isOpen = isModalOpen,
            onDismissRequest = { isModalOpen = false },
            labels = labels,
            selectedLabels = selectedLabels,
            onLabelsChanged = onLabelsChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview(
    @PreviewParameter(HeaderPreviewProvider::class) state: HeaderPreviewState
) {
    TimerTheme(themeMode = state.themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .border(width = 1.dp, color = Color.Black)
            ) {
                DonutHeader(
                    selectedTimeSpan = state.timeSpan,
                    timeOffset = 0,
                    labels = listOf(
                        Label(
                            id = 1,
                            name = "Work",
                            colorArgb = 0xFFFF5722.toInt(),
                            orderIndex = 0
                        ),
                        Label(
                            id = 2,
                            parentId = 1,
                            name = "Deep Work",
                            colorArgb = 0xFFE64A19.toInt(),
                            orderIndex = 0
                        )
                    ),
                    selectedLabels = setOf(),
                    groupSubLabels = false,
                    onToggleGroupSubLabels = { },
                    onPrevClick = { },
                    onNextClick = { },
                    onRangeClick = { },
                    onLabelsChanged = { }
                )
            }
        }
    }
}
