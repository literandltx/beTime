package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.ui.screens.activity.ActivityDataIndex
import com.literandltx.betime.ui.screens.activity.IndexedRecord
import com.literandltx.betime.ui.screens.activity.buildActivityIndex
import com.literandltx.betime.ui.screens.activity.shared.CustomDateRangePickerDialog
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewProvider
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewState
import com.literandltx.betime.ui.screens.activity.shared.TimeSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.theme.TimerTheme
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@Composable
fun DonutWidget(
    labels: List<Label>,
    height: Dp,
    modifier: Modifier = Modifier,
    index: ActivityDataIndex = ActivityDataIndex(),
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

    val fullyFilteredStats: Map<Label?, Int> = remember(
        index,
        labels,
        selectedTimeSpan,
        timeOffset,
        customStartDate,
        customEndDate,
        selectedLabels,
        groupSubLabels
    ) {
        val rawMap = computeStatsFromIndex(
            index = index,
            timeSpan = selectedTimeSpan,
            timeOffset = timeOffset,
            customStartMillis = customStartDate,
            customEndMillis = customEndDate
        )

        val includeAllLabels = selectedLabels.size == labels.size
        val map = LinkedHashMap<Label?, Int>()
        val labelsById = if (groupSubLabels) {
            labels.associateBy { it.id } + rawMap.keys.filterNotNull().associateBy { it.id }
        } else {
            emptyMap()
        }

        for ((label, duration) in rawMap) {
            if (label != null && !includeAllLabels && !selectedLabels.contains(label)) {
                continue
            }
            val targetLabel = if (groupSubLabels && label?.parentId != null) {
                labelsById[label.parentId] ?: label
            } else {
                label
            }
            map[targetLabel] = (map[targetLabel] ?: 0) + duration
        }
        map
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

private fun computeStatsFromIndex(
    index: ActivityDataIndex,
    timeSpan: TimeSpan,
    timeOffset: Int,
    customStartMillis: Long,
    customEndMillis: Long
): Map<Label?, Int> {
    val result = HashMap<Label?, Int>()
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()

    fun addRecord(rec: IndexedRecord) {
        val label = rec.label ?: rec.labelId?.let {
            index.labelsById[it]
        }

        result[label] = (result[label] ?: 0) + rec.durationSeconds
    }

    when (timeSpan) {
        TimeSpan.DAY -> {
            val targetDay = today.plusDays(timeOffset.toLong()).toEpochDay()
            index.byDay[targetDay]?.records?.forEach(::addRecord)
        }

        TimeSpan.WEEK -> {
            val weekStart = today
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(timeOffset.toLong())
            for (i in 0 until 7) {
                val epochDay = weekStart.plusDays(i.toLong()).toEpochDay()
                index.byDay[epochDay]?.records?.forEach(::addRecord)
            }
        }

        TimeSpan.MONTH -> {
            val yearMonth = YearMonth.from(today).plusMonths(timeOffset.toLong())

            for (day in 1..yearMonth.lengthOfMonth()) {
                val epochDay = yearMonth.atDay(day).toEpochDay()
                index.byDay[epochDay]?.records?.forEach(::addRecord)
            }
        }

        TimeSpan.YEAR -> {
            val targetYear = today.year + timeOffset
            for ((epochDay, bucket) in index.byDay) {
                if (LocalDate.ofEpochDay(epochDay).year == targetYear) {
                    bucket.records.forEach(::addRecord)
                }
            }
        }

        TimeSpan.ALL_TIME -> {
            index.allRecords.forEach(::addRecord)
        }

        TimeSpan.CUSTOM -> {
            if (customStartMillis > 0L) {
                val startDay = Instant.ofEpochMilli(customStartMillis)
                    .atZone(zone)
                    .toLocalDate()
                    .toEpochDay()

                val endDay = Instant.ofEpochMilli(customEndMillis)
                    .atZone(zone)
                    .toLocalDate()
                    .toEpochDay()

                for (day in startDay..endDay) {
                    index.byDay[day]?.records?.forEach(::addRecord)
                }
            }
        }
    }

    return result
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        val donutSize = min(maxWidth, maxHeight) * 0.72f
        val statsHeight = maxHeight * 0.25f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (hasData) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DonutElement(
                    stats = if (hasData) stats else mapOf(null to 1),
                    modifier = Modifier.size(donutSize)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statsHeight)
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                StatsList(
                    stats = if (hasData) stats else mapOf(null to 1),
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
                TimeSpan.YEAR,
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
fun DonutWidgetPreview(
    @PreviewParameter(HeaderPreviewProvider::class) state: HeaderPreviewState
) {
    val focusLabel = Label(
        id = 1,
        name = "Focus",
        colorArgb = 0xFFFF5722.toInt(),
        orderIndex = 0
    )

    val breakLabel = Label(
        id = 2,
        name = "Break",
        colorArgb = 0xFF4CAF50.toInt(),
        orderIndex = 1
    )

    val mockLabels = listOf(focusLabel, breakLabel)
    val now = System.currentTimeMillis()

    val mockRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(
                id = 1,
                value = 5400,
                startTimestamp = now
            ),
            label = focusLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(
                id = 2,
                value = 3600,
                startTimestamp = now
            ),
            label = breakLabel
        )
    )

    TimerTheme(themeMode = state.themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                DonutWidget(
                    labels = mockLabels,
                    index = buildActivityIndex(
                        records = mockRecords,
                        labels = mockLabels,
                        zoneId = ZoneId.systemDefault()
                    ),
                    initialTimeSpan = state.timeSpan,
                    height = 360.dp
                )
            }
        }
    }
}
