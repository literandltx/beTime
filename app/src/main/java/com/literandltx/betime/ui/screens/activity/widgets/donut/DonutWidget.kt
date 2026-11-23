package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.screens.activity.shared.CustomDateRangePickerDialog
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewProvider
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewState
import com.literandltx.betime.ui.screens.activity.shared.TimeSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.screens.activity.shared.getFilteredRecords
import com.literandltx.betime.ui.screens.activity.shared.getLabelStatisticMap
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun DonutContainer(
    records: List<TimerRecordDetails>,
    labels: List<Label>,
    height: Dp,
    modifier: Modifier = Modifier,
    initialShowPercentage: Boolean = false,
    initialTimeSpan: TimeSpan = TimeSpan.DAY,
    initialGroupSubLabels: Boolean = false,
    isEditing: Boolean = false,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    dragModifier: Modifier = Modifier
) {
    var showTimeSelectorModal by remember { mutableStateOf(false) }
    var showCustomRangePicker by remember { mutableStateOf(false) }

    var selectedTimeSpan by remember { mutableStateOf(initialTimeSpan) }
    var selectedLabels by remember(labels) { mutableStateOf(labels.toSet()) }
    var groupSubLabels by remember { mutableStateOf(initialGroupSubLabels) }

    var timeOffset by remember { mutableIntStateOf(0) }
    var customStartDate by remember { mutableLongStateOf(0L) }
    var customEndDate by remember { mutableLongStateOf(0L) }

    val timeFilteredRecords = remember(records, selectedTimeSpan, timeOffset, customStartDate, customEndDate) {
        getFilteredRecords(records, selectedTimeSpan, timeOffset, customStartDate, customEndDate)
    }

    val statsMap = remember(timeFilteredRecords) {
        getLabelStatisticMap(timeFilteredRecords)
    }

    val fullyFilteredStats = remember(statsMap, selectedLabels, labels, groupSubLabels) {
        val labelFiltered = statsMap.filterKeys { it == null || selectedLabels.contains(it) }
        if (groupSubLabels) {
            aggregateSubLabels(labelFiltered, labels)
        } else {
            labelFiltered
        }
    }

    TimeSelectorModal(
        isOpen = showTimeSelectorModal,
        onDismissRequest = { showTimeSelectorModal = false },
        selectedTimeSpan = selectedTimeSpan,
        onTimeSpanSelected = { newTimeSpan ->
            selectedTimeSpan = newTimeSpan
            timeOffset = 0
            showTimeSelectorModal = false
        },
        onCustomRangeClick = {
            showTimeSelectorModal = false
            showCustomRangePicker = true
        }
    )

    CustomDateRangePickerDialog(
        isOpen = showCustomRangePicker,
        onDismissRequest = { showCustomRangePicker = false },
        onDateRangeSelected = { start, end ->
            if (start != null) {
                customStartDate = start
                customEndDate = end ?: start
                selectedTimeSpan = TimeSpan.CUSTOM
            }
        }
    )

    WidgetContainer(
        height = height,
        modifier = modifier.fillMaxWidth(),
        isEditing = isEditing,
        canMoveUp = canMoveUp,
        canMoveDown = canMoveDown,
        onMoveUp = onMoveUp,
        onMoveDown = onMoveDown,
        dragModifier = dragModifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isPrevEnabled = selectedTimeSpan != TimeSpan.ALL_TIME && selectedTimeSpan != TimeSpan.CUSTOM
            val isNextEnabled = isPrevEnabled && timeOffset < 0

            DonutHeader(
                selectedTimeSpan = selectedTimeSpan,
                timeOffset = timeOffset,
                labels = labels,
                selectedLabels = selectedLabels,
                groupSubLabels = groupSubLabels,
                onToggleGroupSubLabels = { groupSubLabels = !groupSubLabels },
                onPrevClick = { timeOffset -= 1 },
                onNextClick = { timeOffset += 1 },
                onRangeClick = { showTimeSelectorModal = true },
                onLabelsChanged = { selectedLabels = it },
                customStartDate = customStartDate,
                customEndDate = customEndDate,
                isPrevEnabled = isPrevEnabled,
                isNextEnabled = isNextEnabled
            )

            DonutContent(
                stats = fullyFilteredStats,
                initialShowPercentage = initialShowPercentage,
                selectedTimeSpan = selectedTimeSpan,
                timeOffset = timeOffset,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun aggregateSubLabels(
    stats: Map<Label?, Int>,
    allLabels: List<Label>
): Map<Label?, Int> {
    val labelsById = allLabels.associateBy { it.id } +
            stats.keys.filterNotNull().associateBy { it.id }

    val aggregated = mutableMapOf<Label?, Int>()
    for ((label, duration) in stats) {
        val parentId = label?.parentId
        val targetLabel = if (parentId != null) {
            labelsById[parentId] ?: label
        } else {
            label
        }
        aggregated[targetLabel] = (aggregated[targetLabel] ?: 0) + duration
    }
    return aggregated
}

@Composable
private fun DonutContent(
    stats: Map<Label?, Int>,
    initialShowPercentage: Boolean,
    selectedTimeSpan: TimeSpan,
    timeOffset: Int,
    modifier: Modifier = Modifier
) {
    val hasData = stats.isNotEmpty() && stats.values.any { it > 0 }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (hasData) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val layoutStats: Map<Label?, Int> = if (hasData) stats else mapOf(null to 1)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                DonutElement(layoutStats)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                StatsList(
                    stats = layoutStats,
                    initialShowPercentage = initialShowPercentage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (!hasData) {
            val noRecordsText = when (selectedTimeSpan) {
                TimeSpan.DAY -> when (timeOffset) {
                    0 -> stringResource(R.string.noRecordsToday)
                    1 -> stringResource(R.string.noRecordsTomorrow)
                    -1 -> stringResource(R.string.noRecordsYesterday)
                    else -> if (timeOffset > 0) {
                        stringResource(R.string.noRecordsInDays, timeOffset)
                    } else {
                        stringResource(R.string.noRecordsDaysAgo, -timeOffset)
                    }
                }
                TimeSpan.WEEK -> when (timeOffset) {
                    0 -> stringResource(R.string.noRecordsThisWeek)
                    1 -> stringResource(R.string.noRecordsNextWeek)
                    -1 -> stringResource(R.string.noRecordsLastWeek)
                    else -> if (timeOffset > 0) {
                        stringResource(R.string.noRecordsInWeeks, timeOffset)
                    } else {
                        stringResource(R.string.noRecordsWeeksAgo, -timeOffset)
                    }
                }
                TimeSpan.MONTH -> when (timeOffset) {
                    0 -> stringResource(R.string.noRecordsThisMonth)
                    1 -> stringResource(R.string.noRecordsNextMonth)
                    -1 -> stringResource(R.string.noRecordsLastMonth)
                    else -> if (timeOffset > 0) {
                        stringResource(R.string.noRecordsInMonths, timeOffset)
                    } else {
                        stringResource(R.string.noRecordsMonthsAgo, -timeOffset)
                    }
                }
                TimeSpan.YEAR -> stringResource(R.string.noRecordsMessage)
                TimeSpan.ALL_TIME -> stringResource(R.string.noRecordsMessage)
                TimeSpan.CUSTOM -> stringResource(R.string.noRecordsPeriod)
            }

            Text(
                text = noRecordsText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Donut Container Theme Preview")
@Composable
fun DonutContainerPreview(
    @PreviewParameter(HeaderPreviewProvider::class) state: HeaderPreviewState
) {
    val focusLabel = Label(id = 1, name = "Focus", colorArgb = 0xFFFF5722.toInt(), orderIndex = 0)
    val breakLabel = Label(id = 2, name = "Break", colorArgb = 0xFF4CAF50.toInt(), orderIndex = 1)
    val chillLabel = Label(id = 3, name = "Chill", colorArgb = 0xFFFFFF00.toInt(), orderIndex = 2)

    val mockLabels = listOf(focusLabel, breakLabel, chillLabel)
    val now = System.currentTimeMillis()

    val mockRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(id = 1, value = 5400, startTimestamp = now),
            label = focusLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 2, value = 3600, startTimestamp = now),
            label = breakLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 3, value = 1800, startTimestamp = now),
            label = chillLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 4, value = 900, startTimestamp = now),
            label = null
        )
    )

    TimerTheme(themeMode = state.themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                DonutContainer(
                    labels = mockLabels,
                    records = mockRecords,
                    initialTimeSpan = state.timeSpan,
                    height = 360.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Donut Container Date Format Preview")
@Composable
fun DonutContainerDatePreview() {
    MaterialTheme {
        Surface {
            val labelToday = Label(id = 1, name = "Today", colorArgb = 0xFFFF5722.toInt(), orderIndex = 0)
            val labelDaysAgo = Label(id = 2, name = "2 Days Ago", colorArgb = Color.Green.toArgb(), orderIndex = 1)
            val labelLastWeek = Label(id = 3, name = "Last Week", colorArgb = Color.Blue.toArgb(), orderIndex = 2)
            val labelLastMonth = Label(id = 4, name = "Last Month", colorArgb = Color.Magenta.toArgb(), orderIndex = 3)

            val mockLabels = listOf(labelToday, labelDaysAgo, labelLastWeek, labelLastMonth)
            val now = System.currentTimeMillis()
            val dayInMillis = 24L * 60L * 60L * 1000L

            val mockRecords = listOf(
                TimerRecordDetails(
                    record = TimerRecord(id = 1, value = 3600, startTimestamp = now),
                    label = labelToday
                ),
                TimerRecordDetails(
                    record = TimerRecord(id = 2, value = 5400, startTimestamp = now - (2 * dayInMillis)),
                    label = labelDaysAgo
                ),
                TimerRecordDetails(
                    record = TimerRecord(id = 3, value = 7200, startTimestamp = now - (8 * dayInMillis)),
                    label = labelLastWeek
                ),
                TimerRecordDetails(
                    record = TimerRecord(id = 4, value = 9600, startTimestamp = now - (35 * dayInMillis)),
                    label = labelLastMonth
                )
            )

            Box(modifier = Modifier.padding(16.dp)) {
                DonutContainer(
                    labels = mockLabels,
                    records = mockRecords,
                    initialTimeSpan = TimeSpan.WEEK,
                    height = 360.dp
                )
            }
        }
    }
}
