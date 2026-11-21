package com.literandltx.betime.ui.screens.activity.widgets.calendar

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.DEFAULT_PADDING_CHAR
import java.text.SimpleDateFormat
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
    records: List<TimerRecordDetails>,
    height: Dp,
    modifier: Modifier = Modifier,
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

    val referenceDate = remember(timeOffset, config.firstDayOfWeek) {
        startOfWeek(Calendar.getInstance(), config.firstDayOfWeek).apply {
            add(Calendar.WEEK_OF_YEAR, timeOffset)
        }
    }

    val daysData = remember(referenceDate, records, config) {
        buildWeekColumns(
            weekStart = referenceDate,
            records = records,
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(config.trackCornerRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalHeight = maxHeight
                val baseLineColor = config.gridLineColor ?: MaterialTheme.colorScheme.outlineVariant

                val visibleGridHours = when (config.gridLineMode) {
                    GridLineMode.ALL -> config.allGridHours
                    GridLineMode.MAJOR_ONLY -> config.majorHourMarks
                    GridLineMode.NONE -> emptyList()
                }

                visibleGridHours.forEach { hour ->
                    val fraction = (hour - config.startHour) / config.totalHours
                    val isMajor = hour in config.majorHourMarks

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = config.gridLineHorizontalPadding)
                            .height(if (isMajor) config.majorLineThickness else config.minorLineThickness)
                            .offset(y = totalHeight * fraction)
                            .background(
                                baseLineColor.copy(
                                    alpha = if (isMajor) config.majorLineAlpha else config.minorLineAlpha
                                )
                            )
                    )
                }

                dayColumn.blocks.forEach { block ->
                    val blockOffsetY = totalHeight * block.topFraction
                    val blockHeight = (totalHeight * block.heightFraction)
                        .coerceAtLeast(config.minBlockHeight)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .height(blockHeight)
                            .offset(y = blockOffsetY)
                            .background(block.color, RoundedCornerShape(config.blockCornerRadius))
                    )
                }
            }
        }
    }
}

private fun buildWeekColumns(
    weekStart: Calendar,
    records: List<TimerRecordDetails>,
    config: CalendarWidgetConfig,
    context: Context
): List<DayColumnData> {
    val todayCal = Calendar.getInstance()
    val dayFormatter = SimpleDateFormat(config.dayHeaderFormat, config.locale)

    return (0 until 7).map { dayOffset ->
        val dayCal = (weekStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }

        val blocks = records.mapNotNull { recordDetails ->
            val recordCal = Calendar.getInstance().apply {
                timeInMillis = recordDetails.record.startTimestamp
            }
            if (!isSameDay(recordCal, dayCal)) return@mapNotNull null

            val startHourFloat = recordCal.get(Calendar.HOUR_OF_DAY) +
                    (recordCal.get(Calendar.MINUTE) / 60f)
            val durationHours = recordDetails.record.value / 3600f
            val endHourFloat = startHourFloat + durationHours

            val visibleStart = startHourFloat.coerceIn(
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
                id = recordDetails.record.id.toString(),
                topFraction = (visibleStart - config.startHour) / config.totalHours,
                heightFraction = visibleDuration / config.totalHours,
                color = recordDetails.label?.composeColor ?: config.fallbackBlockColor,
                labelName = recordDetails.label?.name ?: context.getString(R.string.noLabelMessage)
            )
        }

        DayColumnData(
            key = dayCal.timeInMillis.toString(),
            label = dayFormatter.format(dayCal.time),
            isToday = isSameDay(dayCal, todayCal),
            blocks = blocks
        )
    }
}

private fun startOfWeek(date: Calendar, firstDayOfWeek: Int): Calendar {
    return (date.clone() as Calendar).apply {
        this.firstDayOfWeek = firstDayOfWeek
        val currentDay = get(Calendar.DAY_OF_WEEK)
        val diff = ((firstDayOfWeek - currentDay - 7) % 7)
        add(Calendar.DAY_OF_YEAR, diff)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean {
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

@Preview(showBackground = true)
@Composable
fun CalendarWidgetPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val workLabel = Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt(), orderIndex = 0)
    val studyLabel = Label(id = 2, name = "Study", colorArgb = 0xFF34A853.toInt(), orderIndex = 1)
    val exerciseLabel = Label(id = 3, name = "Exercise", colorArgb = 0xFFEA4335.toInt(), orderIndex = 2)

    val config = CalendarWidgetConfig()
    val weekStart = startOfWeek(Calendar.getInstance(), config.firstDayOfWeek)

    fun timestampAt(dayOffset: Int, hour: Int, minute: Int = 0): Long {
        return (weekStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.timeInMillis
    }

    val sampleRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(id = 1, value = 7200, startTimestamp = timestampAt(0, 9, 0), labelId = 1),
            label = workLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 2, value = 5400, startTimestamp = timestampAt(0, 14, 30), labelId = 2),
            label = studyLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 3, value = 9000, startTimestamp = timestampAt(2, 10, 0), labelId = 1),
            label = workLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 4, value = 3600, startTimestamp = timestampAt(2, 18, 0), labelId = 3),
            label = exerciseLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 5, value = 10800, startTimestamp = timestampAt(4, 11, 0), labelId = 1),
            label = workLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 6, value = 4500, startTimestamp = timestampAt(5, 15, 0), labelId = 2),
            label = studyLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 7, value = 600, startTimestamp = timestampAt(6, 15, 0), labelId = 2),
            label = studyLabel
        ),
        TimerRecordDetails(
            record = TimerRecord(id = 8, value = 300, startTimestamp = timestampAt(6, 18, 0), labelId = 2),
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
                    records = sampleRecords,
                    height = 320.dp,
                    config = config
                )
            }
        }
    }
}
