package com.literandltx.betime.ui.screens.activity.widgets.heatmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.screens.activity.ActivityDataIndex
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.theme.TimerTheme
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

@Composable
fun HeatmapContainer(
    records: List<TimerRecordDetails>,
    height: Dp,
    modifier: Modifier = Modifier,
    index: ActivityDataIndex = ActivityDataIndex(),
    unitLabel: String = stringResource(R.string.recordsMessage),
    levelThresholds: List<Int> = listOf(1, 2, 3, 4),
    isEditing: Boolean = false,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    dragModifier: Modifier = Modifier
) {
    var timeOffset by remember { mutableIntStateOf(0) }

    val yearMonth = remember(timeOffset) {
        YearMonth.from(LocalDate.now()).plusMonths(timeOffset.toLong())
    }
    val year = yearMonth.year
    val month = yearMonth.monthValue - 1
    val daysInMonth = remember(yearMonth) { yearMonth.lengthOfMonth() }

    val dailyCounts = remember(index, yearMonth, daysInMonth) {
        List(daysInMonth) { dayIdx ->
            val epochDay = yearMonth.atDay(dayIdx + 1).toEpochDay()
            index.byDay[epochDay]?.recordCount ?: 0
        }
    }

    val totalCount = remember(dailyCounts) { dailyCounts.sum() }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeatmapHeader(
                timeOffset = timeOffset,
                totalCount = totalCount,
                unitLabel = unitLabel,
                onPrevClick = { timeOffset -= 1 },
                onNextClick = { timeOffset += 1 },
                onRangeClick = { timeOffset = 0 }
            )

            CalendarHeatmapElement(
                year = year,
                month = month,
                daysInMonth = daysInMonth,
                dailyCounts = dailyCounts,
                levelThresholds = levelThresholds,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeatmapContainerPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val now = Calendar.getInstance()
    val sampleRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(
                id = 1,
                labelId = 1,
                startTimestamp = now.timeInMillis,
                value = 1800
            ),
            label = null
        )
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                HeatmapContainer(
                    records = sampleRecords,
                    height = 320.dp,
                    unitLabel = stringResource(R.string.recordsMessage)
                )
            }
        }
    }
}
