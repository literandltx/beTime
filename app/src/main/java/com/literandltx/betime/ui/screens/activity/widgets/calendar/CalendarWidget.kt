package com.literandltx.betime.ui.screens.activity.widgets.calendar

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.screens.activity.ActivityDataIndex
import com.literandltx.betime.ui.screens.activity.buildActivityIndex
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.DEFAULT_PADDING_CHAR
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

enum class GridLineMode {
    ALL,
    MAJOR_ONLY,
    NONE
}

data class CalendarWidgetConfig(
    val startHour: Int = 0,
    val endHour: Int = 24,
    val majorHourStep: Int = 2,
    val firstDayOfWeek: Int = Calendar.MONDAY,
    val dayHeaderFormat: String = "EEE",
    val locale: Locale = Locale.ENGLISH,
    val enableSwipeNavigation: Boolean = false,
    val swipeThresholdPx: Float = 50f,
    val minBlockHeight: Dp = 1.dp,
    val trackCornerRadius: Dp = 8.dp,
    val blockCornerRadius: Dp = 4.dp,
    val columnSpacing: Dp = 4.dp,
    val hourAxisWidth: Dp = 16.dp,
    val hourAxisTopPadding: Dp = 18.dp,
    val hourLabelFontSize: TextUnit = 10.sp,
    val dayLabelFontSize: TextUnit = 11.sp,
    val fallbackBlockColor: Color = Color.Gray,

    val gridLineMode: GridLineMode = GridLineMode.ALL,
    val majorLineThickness: Dp = 1.dp,
    val minorLineThickness: Dp = 1.dp,
    val majorLineAlpha: Float = 0.4f,
    val minorLineAlpha: Float = 0.15f,
    val gridLineHorizontalPadding: Dp = 0.dp,
    val gridLineColor: Color? = null
) {
    val totalHours: Float = (endHour - startHour).coerceAtLeast(1).toFloat()
    val allGridHours: List<Int> = ((startHour + 1) until endHour).toList()
    val majorHourMarks: List<Int> = allGridHours.filter { (it - startHour) % majorHourStep == 0 }
}

data class TimelineBlock(
    val id: String,
    val topFraction: Float,
    val heightFraction: Float,
    val color: Color,
    val labelName: String
)

data class DayColumnData(
    val key: String,
    val label: String,
    val isToday: Boolean,
    val blocks: List<TimelineBlock>
)

@Composable
fun CalendarWidget(
    height: Dp,
    modifier: Modifier = Modifier,
    index: ActivityDataIndex = ActivityDataIndex(),
    config: CalendarWidgetConfig = CalendarWidgetConfig(),
    isEditing: Boolean = false,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    dragModifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var timeOffset by remember { mutableIntStateOf(0) }

    val weekStartDate = remember(timeOffset, config.firstDayOfWeek) {
        startOfWeekDate(LocalDate.now(), config.firstDayOfWeek).plusWeeks(timeOffset.toLong())
    }

    val daysData = remember(weekStartDate, index, config, context) {
        buildWeekColumnsFromIndex(
            weekStart = weekStartDate,
            index = index,
            config = config,
            context = context
        )
    }

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
        ) {
            CalendarHeader(
                timeOffset = timeOffset,
                onPrevClick = { timeOffset -= 1 },
                onNextClick = { timeOffset += 1 },
                onRangeClick = { timeOffset = 0 }
            )

            WeekTimelineGrid(
                daysData = daysData,
                config = config,
                onSwipePrevious = { timeOffset -= 1 },
                onSwipeNext = { timeOffset += 1 },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun WeekTimelineGrid(
    daysData: List<DayColumnData>,
    config: CalendarWidgetConfig,
    onSwipePrevious: () -> Unit,
    onSwipeNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragTotalOffset by remember { mutableFloatStateOf(0f) }

    val swipeModifier = if (config.enableSwipeNavigation) {
        Modifier.pointerInput(config.swipeThresholdPx) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    when {
                        dragTotalOffset > config.swipeThresholdPx -> onSwipePrevious()
                        dragTotalOffset < -config.swipeThresholdPx -> onSwipeNext()
                    }
                    dragTotalOffset = 0f
                },
                onHorizontalDrag = { _, dragAmount ->
                    dragTotalOffset += dragAmount
                }
            )
        }
    } else {
        Modifier
    }

    Row(
        modifier = modifier.then(swipeModifier),
        horizontalArrangement = Arrangement.spacedBy(config.columnSpacing),
        verticalAlignment = Alignment.Top
    ) {
        HourAxisColumn(
            hourMarks = config.majorHourMarks,
            config = config,
            modifier = Modifier
                .width(config.hourAxisWidth)
                .fillMaxHeight()
                .padding(top = config.hourAxisTopPadding)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(config.columnSpacing)
        ) {
            daysData.forEach { dayColumn ->
                DayTrackColumn(
                    dayColumn = dayColumn,
                    config = config,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun HourAxisColumn(
    hourMarks: List<Int>,
    config: CalendarWidgetConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        hourMarks.forEach { hour ->
            Text(
                text = hour.toString().padStart(2, DEFAULT_PADDING_CHAR),
                fontSize = config.hourLabelFontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayTrackColumn(
    dayColumn: DayColumnData,
    config: CalendarWidgetConfig,
    modifier: Modifier = Modifier
) {
    val trackBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val baseLineColor = config.gridLineColor ?: MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = dayColumn.label,
            fontSize = config.dayLabelFontSize,
            fontWeight = FontWeight.Bold,
            color = if (dayColumn.isToday) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(config.trackCornerRadius))
        ) {
            val totalHeight = size.height
            val totalWidth = size.width

            drawRect(color = trackBgColor, size = size)

            val visibleGridHours = when (config.gridLineMode) {
                GridLineMode.ALL -> config.allGridHours
                GridLineMode.MAJOR_ONLY -> config.majorHourMarks
                GridLineMode.NONE -> emptyList()
            }

            val linePadPx = config.gridLineHorizontalPadding.toPx()
            val majorThickPx = config.majorLineThickness.toPx()
            val minorThickPx = config.minorLineThickness.toPx()

            for (hour in visibleGridHours) {
                val fraction = (hour - config.startHour) / config.totalHours
                val isMajor = hour in config.majorHourMarks
                val y = totalHeight * fraction
                val thickness = if (isMajor) majorThickPx else minorThickPx
                val alpha = if (isMajor) config.majorLineAlpha else config.minorLineAlpha

                drawRect(
                    color = baseLineColor.copy(alpha = alpha),
                    topLeft = Offset(linePadPx, y),
                    size = Size(
                        width = (totalWidth - linePadPx * 2f).coerceAtLeast(0f),
                        height = thickness
                    )
                )
            }

            val blockPadPx = 2.dp.toPx()
            val minBlockHeightPx = config.minBlockHeight.toPx()
            val cornerRadiusPx = config.blockCornerRadius.toPx()
            val blockWidth = (totalWidth - blockPadPx * 2f).coerceAtLeast(0f)

            for (block in dayColumn.blocks) {
                val blockTopPx = totalHeight * block.topFraction
                val blockHeightPx = (totalHeight * block.heightFraction).coerceAtLeast(minBlockHeightPx)

                drawRoundRect(
                    color = block.color,
                    topLeft = Offset(blockPadPx, blockTopPx),
                    size = Size(width = blockWidth, height = blockHeightPx),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
        }
    }
}

private fun buildWeekColumnsFromIndex(
    weekStart: LocalDate,
    index: ActivityDataIndex,
    config: CalendarWidgetConfig,
    context: Context
): List<DayColumnData> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(config.dayHeaderFormat, config.locale)
    val noLabelText = context.getString(R.string.noLabelMessage)

    return (0 until 7).map { dayOffset ->
        val dayDate = weekStart.plusDays(dayOffset.toLong())
        val bucket = index.byDay[dayDate.toEpochDay()]
        val dayRecords = bucket?.records.orEmpty()

        val blocks = dayRecords.mapNotNull { rec ->
            val durationHours = rec.durationSeconds / 3600f
            val endHourFloat = rec.startHourFloat + durationHours

            val visibleStart = rec.startHourFloat.coerceIn(
                config.startHour.toFloat(),
                config.endHour.toFloat()
            )
            val visibleEnd = endHourFloat.coerceIn(
                config.startHour.toFloat(),
                config.endHour.toFloat()
            )
            val visibleDuration = visibleEnd - visibleStart

            if (visibleDuration <= 0f) return@mapNotNull null

            TimelineBlock(
                id = rec.id.toString(),
                topFraction = (visibleStart - config.startHour) / config.totalHours,
                heightFraction = visibleDuration / config.totalHours,
                color = rec.label?.composeColor ?: config.fallbackBlockColor,
                labelName = rec.label?.name ?: noLabelText
            )
        }

        DayColumnData(
            key = dayDate.toEpochDay().toString(),
            label = dayDate.format(formatter),
            isToday = dayDate == today,
            blocks = blocks
        )
    }
}

private fun startOfWeekDate(date: LocalDate, firstDayOfWeek: Int): LocalDate {
    val targetDow = when (firstDayOfWeek) {
        Calendar.SUNDAY -> DayOfWeek.SUNDAY
        Calendar.MONDAY -> DayOfWeek.MONDAY
        Calendar.TUESDAY -> DayOfWeek.TUESDAY
        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
        Calendar.THURSDAY -> DayOfWeek.THURSDAY
        Calendar.FRIDAY -> DayOfWeek.FRIDAY
        Calendar.SATURDAY -> DayOfWeek.SATURDAY
        else -> DayOfWeek.MONDAY
    }
    return date.with(TemporalAdjusters.previousOrSame(targetDow))
}

@Preview(showBackground = true)
@Composable
fun CalendarWidgetPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val workLabel = Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt(), orderIndex = 0)
    val studyLabel = Label(id = 2, name = "Study", colorArgb = 0xFF34A853.toInt(), orderIndex = 1)
    val config = CalendarWidgetConfig()
    val nowMillis = System.currentTimeMillis()

    val sampleRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(id = 1, value = 7200, startTimestamp = nowMillis, labelId = 1),
            label = workLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 2, value = 5400, startTimestamp = nowMillis - 3_600_000L, labelId = 2),
            label = studyLabel
        )
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                CalendarWidget(
                    index = buildActivityIndex(
                        records = sampleRecords,
                        labels = listOf(workLabel, studyLabel),
                        zoneId = ZoneId.systemDefault()
                    ),
                    height = 320.dp,
                    config = config
                )
            }
        }
    }
}
