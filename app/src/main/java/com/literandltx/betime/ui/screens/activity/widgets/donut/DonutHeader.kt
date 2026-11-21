package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PreviousIconButton(
            onClick = onPrevClick,
            isEnabled = isPrevEnabled,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        RangeSelectorButton(
            selectedTimeSpan = selectedTimeSpan,
            timeOffset = timeOffset,
            customStartDate = customStartDate,
            customEndDate = customEndDate,
            onClick = onRangeClick,
            modifier = Modifier.align(Alignment.Center)
        )

        LabelSelector(
            labels = labels,
            selectedLabels = selectedLabels,
            onLabelsChanged = onLabelsChanged,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
        )

        NextIconButton(
            onClick = onNextClick,
            isEnabled = isNextEnabled,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
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
            onClick = { isModalOpen = true }
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
                    labels = listOf(),
                    selectedLabels = setOf(),
                    onPrevClick = { },
                    onNextClick = { },
                    onRangeClick = { },
                    onLabelsChanged = { }
                )
            }
        }
    }
}
