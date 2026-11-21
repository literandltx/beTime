package com.literandltx.betime.ui.screens.activity.widgets.histogram

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.screens.activity.shared.LabelSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.ThreeOptionTimeSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.theme.TimerTheme
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import android.graphics.Path as GPath

enum class Granularity {
    MONTH,
    YEAR
}

data class ChartBucket(val label: String, val minutes: Int)

@Composable
fun HistogramWidget(
    records: List<TimerRecordDetails>,
    labels: List<Label>,
    height: Dp,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    hardLimitMinutes: Int? = null,
    showComparison: Boolean = true,
    initialTimeSpan: TimeSpan = TimeSpan.DAY,
    dragModifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTimeSelectorModal by remember { mutableStateOf(false) }
    var showLabelSelectorModal by remember { mutableStateOf(false) }

    var selectedTimeSpan by remember { mutableStateOf(initialTimeSpan) }
    var selectedLabels by remember(labels) { mutableStateOf(labels.toSet()) }
    var timeOffset by remember { mutableIntStateOf(0) }

    val granularity: Granularity = remember(selectedTimeSpan) {
        when (selectedTimeSpan) {
            TimeSpan.MONTH -> Granularity.MONTH
            TimeSpan.YEAR -> Granularity.YEAR
            else -> Granularity.MONTH
        }
    }

    val hiddenLabelIds = remember(labels, selectedLabels) {
        labels.filter { !selectedLabels.contains(it) }.map { it.id }.toSet()
    }

    val referenceDate = remember { Calendar.getInstance() }
    val effectiveReferenceDate = remember(referenceDate, granularity, timeOffset) {
        shiftReference(referenceDate, granularity, timeOffset)
    }

    val currentBuckets = remember(effectiveReferenceDate, granularity, hiddenLabelIds, records, context) {
        buildBuckets(effectiveReferenceDate, granularity, records, hiddenLabelIds, 0, context)
    }
    val previousBuckets = remember(effectiveReferenceDate, granularity, hiddenLabelIds, records, showComparison, context) {
        if (!showComparison) emptyList()
        else buildBuckets(effectiveReferenceDate, granularity, records, hiddenLabelIds, -1, context)
    }

    val avgMinutes = remember(currentBuckets) {
        calculateAverageMinutes(currentBuckets)
    }

    val maxVal = remember(currentBuckets, previousBuckets, hardLimitMinutes) {
        calculateMaxVal(currentBuckets, previousBuckets, hardLimitMinutes)
    }

    LineChartModals(
        showTimeSelectorModal = showTimeSelectorModal,
        onDismissTimeSelector = { showTimeSelectorModal = false },
        selectedTimeSpan = selectedTimeSpan,
        onTimeSpanSelected = { newTimeSpan ->
            selectedTimeSpan = newTimeSpan
            timeOffset = 0
        },
        showLabelSelectorModal = showLabelSelectorModal,
        onDismissLabelSelector = { showLabelSelectorModal = false },
        labels = labels,
        selectedLabels = selectedLabels,
        onLabelsChanged = { selectedLabels = it }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HistogramHeader(
                selectedTimeSpan = selectedTimeSpan,
                avgMinutes = avgMinutes,
                selectedLabels = selectedLabels,
                timeOffset = timeOffset,
                onPreviousClick = { timeOffset -= 1 },
                onNextClick = { timeOffset += 1 },
                onTitleClick = { showTimeSelectorModal = true },
                onLabelClick = { showLabelSelectorModal = true }
            )

            LineChartCanvasContainer(
                currentBuckets = currentBuckets,
                previousBuckets = previousBuckets,
                maxVal = maxVal,
                showComparison = showComparison,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun LineChartModals(
    showTimeSelectorModal: Boolean,
    onDismissTimeSelector: () -> Unit,
    selectedTimeSpan: TimeSpan,
    onTimeSpanSelected: (TimeSpan) -> Unit,
    showLabelSelectorModal: Boolean,
    onDismissLabelSelector: () -> Unit,
    labels: List<Label>,
    selectedLabels: Set<Label>,
    onLabelsChanged: (Set<Label>) -> Unit
) {
    ThreeOptionTimeSelectorModal(
        isOpen = showTimeSelectorModal,
        onDismissRequest = onDismissTimeSelector,
        selectedTimeSpan = selectedTimeSpan,
        onTimeSpanSelected = onTimeSpanSelected,
        options = listOf(TimeSpan.MONTH, TimeSpan.YEAR)
    )

    LabelSelectorModal(
        isOpen = showLabelSelectorModal,
        onDismissRequest = onDismissLabelSelector,
        labels = labels,
        selectedLabels = selectedLabels,
        onLabelsChanged = onLabelsChanged
    )
}

@Composable
private fun LineChartCanvasContainer(
    currentBuckets: List<ChartBucket>,
    previousBuckets: List<ChartBucket>,
    maxVal: Float,
    showComparison: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f))
            .padding(12.dp)
    ) {
        val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        val textColorInt = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
        val primaryColor = MaterialTheme.colorScheme.primary
        val comparisonColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 32f
            val paddingRight = 12f
            val paddingTop = 12f
            val paddingBottom = 24f

            val usableWidth = width - paddingLeft - paddingRight
            val usableHeight = height - paddingTop - paddingBottom

            val denom = (currentBuckets.size - 1).coerceAtLeast(1)

            val lastBucketX = if (currentBuckets.isNotEmpty()) {
                paddingLeft + usableWidth * currentBuckets.lastIndex / denom
            } else {
                paddingLeft
            }

            drawGridLinesAndLabels(
                maxVal = maxVal,
                usableHeight = usableHeight,
                paddingLeft = paddingLeft,
                paddingTop = paddingTop,
                endX = lastBucketX,
                gridColor = gridColor,
                textColorInt = textColorInt
            )

            if (showComparison && previousBuckets.isNotEmpty() && currentBuckets.isNotEmpty()) {
                drawComparisonPath(
                    previousBuckets = previousBuckets,
                    maxVal = maxVal,
                    paddingLeft = paddingLeft,
                    paddingTop = paddingTop,
                    usableWidth = usableWidth,
                    usableHeight = usableHeight,
                    denom = denom,
                    comparisonColor = comparisonColor
                )
            }

            if (currentBuckets.isNotEmpty()) {
                drawCurrentChartData(
                    currentBuckets = currentBuckets,
                    maxVal = maxVal,
                    paddingLeft = paddingLeft,
                    paddingTop = paddingTop,
                    usableWidth = usableWidth,
                    usableHeight = usableHeight,
                    denom = denom,
                    height = height,
                    primaryColor = primaryColor,
                    textColorInt = textColorInt
                )
            }
        }
    }
}

private fun DrawScope.drawGridLinesAndLabels(
    maxVal: Float,
    usableHeight: Float,
    paddingLeft: Float,
    paddingTop: Float,
    endX: Float,
    gridColor: Color,
    textColorInt: Int
) {
    val stepVal = maxVal / 4f
    for (i in 1..4) {
        val v = stepVal * i
        val y = paddingTop + usableHeight - (v / maxVal) * usableHeight

        drawLine(
            color = gridColor,
            start = Offset(paddingLeft, y),
            end = Offset(endX, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
        )

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = textColorInt
                textSize = 20f
                isAntiAlias = true
                textAlign = Paint.Align.RIGHT
            }
            canvas.nativeCanvas.drawText(
                Math.round(v).toString(),
                paddingLeft - 10f,
                y + 7f,
                paint
            )
        }
    }
}

private fun DrawScope.drawComparisonPath(
    previousBuckets: List<ChartBucket>,
    maxVal: Float,
    paddingLeft: Float,
    paddingTop: Float,
    usableWidth: Float,
    usableHeight: Float,
    denom: Int,
    comparisonColor: Color
) {
    val prevPoints = previousBuckets
        .take(denom + 1)
        .mapIndexed { index, b ->
            val x = paddingLeft + (usableWidth * index) / denom
            val y = paddingTop + usableHeight - (b.minutes.toFloat() / maxVal) * usableHeight
            Offset(x, y)
        }
    val prevPath = GPath().apply {
        prevPoints.forEachIndexed { idx, pt ->
            if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
        }
    }
    drawPath(
        path = prevPath.asComposePath(),
        color = comparisonColor,
        style = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
        )
    )
}

private fun DrawScope.drawCurrentChartData(
    currentBuckets: List<ChartBucket>,
    maxVal: Float,
    paddingLeft: Float,
    paddingTop: Float,
    usableWidth: Float,
    usableHeight: Float,
    denom: Int,
    height: Float,
    primaryColor: Color,
    textColorInt: Int
) {
    val points = currentBuckets.mapIndexed { index, b ->
        val x = paddingLeft + (usableWidth * index) / denom
        val y = paddingTop + usableHeight - (b.minutes.toFloat() / maxVal) * usableHeight
        Offset(x, y)
    }

    val baselineY = paddingTop + usableHeight
    val fillPath = GPath().apply {
        moveTo(points.first().x, baselineY)
        points.forEach { lineTo(it.x, it.y) }
        lineTo(points.last().x, baselineY)
        close()
    }
    drawPath(
        path = fillPath.asComposePath(),
        brush = Brush.verticalGradient(
            colors = listOf(primaryColor.copy(alpha = 0.3f), primaryColor.copy(alpha = 0.02f), Color.Transparent),
            startY = paddingTop,
            endY = baselineY
        )
    )

    val path = GPath().apply {
        points.forEachIndexed { idx, pt ->
            if (idx == 0) {
                moveTo(pt.x, pt.y)
            } else {
                val prev = points[idx - 1]
                val controlPoint1 = Offset(prev.x + (pt.x - prev.x) / 2f, prev.y)
                val controlPoint2 = Offset(prev.x + (pt.x - prev.x) / 2f, pt.y)
                cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, pt.x, pt.y)
            }
        }
    }

    drawPath(
        path = path.asComposePath(),
        color = primaryColor,
        style = Stroke(
            width = 3.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    val stepX = if (currentBuckets.size > 15) 5 else 1

    points.forEachIndexed { idx, pt ->
        drawCircle(color = primaryColor, radius = 3f, center = pt)

        val isFirst = idx == 0
        val isLast = idx == currentBuckets.size - 1

        if (isFirst || isLast || idx % stepX == 0) {
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = textColorInt
                    textSize = 18f
                    isAntiAlias = true
                    textAlign = when {
                        isFirst -> Paint.Align.LEFT
                        isLast -> Paint.Align.RIGHT
                        else -> Paint.Align.CENTER
                    }
                }

                val drawX = when {
                    isFirst -> pt.x
                    isLast -> pt.x
                    else -> pt.x
                }

                canvas.nativeCanvas.drawText(
                    currentBuckets[idx].label,
                    drawX,
                    height - 2f,
                    paint
                )
            }
        }
    }
}

private fun calculateAverageMinutes(buckets: List<ChartBucket>): Int {
    val active = buckets.filter { it.minutes > 0 }
    return if (active.isEmpty()) 0 else active.sumOf { it.minutes } / active.size
}

private fun calculateMaxVal(
    currentBuckets: List<ChartBucket>,
    previousBuckets: List<ChartBucket>,
    hardLimitMinutes: Int?
): Float {
    val dataMax = maxOf(
        30,
        currentBuckets.maxOfOrNull { it.minutes } ?: 0,
        previousBuckets.maxOfOrNull { it.minutes } ?: 0,
        hardLimitMinutes ?: 0
    )
    return niceMax(dataMax.toFloat())
}

private fun buildBuckets(
    ref: Calendar,
    granularity: Granularity,
    records: List<TimerRecordDetails>,
    hiddenLabels: Set<Int>,
    periodOffset: Int,
    context: Context
): List<ChartBucket> {
    val cal = ref.clone() as Calendar

    when (granularity) {
        Granularity.MONTH -> {
            cal.add(Calendar.MONTH, periodOffset)
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalsInSeconds = IntArray(daysInMonth)

            records.forEach { recordDetails ->
                val labelId = recordDetails.record.labelId
                if (labelId == null || !hiddenLabels.contains(labelId)) {
                    val d = Calendar.getInstance().apply { timeInMillis = recordDetails.record.startTimestamp }
                    if (d.get(Calendar.YEAR) == year && d.get(Calendar.MONTH) == month) {
                        val dayOfMonth = d.get(Calendar.DAY_OF_MONTH) - 1
                        if (dayOfMonth in totalsInSeconds.indices) {
                            totalsInSeconds[dayOfMonth] += recordDetails.record.value
                        }
                    }
                }
            }
            return totalsInSeconds.mapIndexed { i, secs ->
                ChartBucket(label = (i + 1).toString(), minutes = (secs / 60.0).toInt())
            }
        }
        Granularity.YEAR -> {
            cal.add(Calendar.YEAR, periodOffset)
            val year = cal.get(Calendar.YEAR)
            val totalsInSeconds = IntArray(12)
            val monthNames = listOf(
                context.getString(R.string.monthJan),
                context.getString(R.string.monthFeb),
                context.getString(R.string.monthMar),
                context.getString(R.string.monthApr),
                context.getString(R.string.monthMay),
                context.getString(R.string.monthJun),
                context.getString(R.string.monthJul),
                context.getString(R.string.monthAug),
                context.getString(R.string.monthSep),
                context.getString(R.string.monthOct),
                context.getString(R.string.monthNov),
                context.getString(R.string.monthDec)
            )

            records.forEach { recordDetails ->
                val labelId = recordDetails.record.labelId
                if (labelId == null || !hiddenLabels.contains(labelId)) {
                    val d = Calendar.getInstance().apply { timeInMillis = recordDetails.record.startTimestamp }
                    if (d.get(Calendar.YEAR) == year) {
                        val m = d.get(Calendar.MONTH)
                        totalsInSeconds[m] += recordDetails.record.value
                    }
                }
            }
            return totalsInSeconds.mapIndexed { i, secs ->
                ChartBucket(label = monthNames[i], minutes = (secs / 60.0).toInt())
            }
        }
    }
}

private fun shiftReference(ref: Calendar, granularity: Granularity, dir: Int): Calendar {
    val d = ref.clone() as Calendar
    when (granularity) {
        Granularity.MONTH -> d.add(Calendar.MONTH, dir)
        Granularity.YEAR -> d.add(Calendar.YEAR, dir)
    }
    return d
}

private fun niceMax(value: Float): Float {
    if (value <= 30f) return 30f
    val rawStep = value / 4f
    val magnitude = 10.0.pow(floor(log10(rawStep.toDouble()))).toFloat()
    val normalized = rawStep / magnitude
    val niceStep = when {
        normalized <= 1f -> 1f
        normalized <= 2f -> 2f
        normalized <= 5f -> 5f
        else -> 10f
    }
    return niceStep * magnitude * 4f
}

@Preview(showBackground = true)
@Composable
fun HistogramWidgetPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val sampleLabels = listOf(
        Label(id = 1, name = "Work", colorArgb = 0xFFFF5252.toInt(), orderIndex = 0),
        Label(id = 2, name = "Study", colorArgb = 0xFF4CAF50.toInt(), orderIndex = 1),
        Label(id = 3, name = "Exercise", colorArgb = 0xFF2196F3.toInt(), orderIndex = 2)
    )

    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val dailyMinutes = listOf(
        85, 120, 45, 180, 150, 210, 90,
        240, 165, 110, 195, 270, 130, 180,
        220, 95, 160, 250, 200, 140, 300,
        175, 230, 115, 190, 260, 145, 210,
        180, 280, 125
    )

    val sampleRecords = buildList {
        dailyMinutes.take(currentDay).forEachIndexed { index, minutes ->
            val dayCalendar = calendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, index + 1)
            dayCalendar.set(Calendar.HOUR_OF_DAY, 12)
            dayCalendar.set(Calendar.MINUTE, 0)
            dayCalendar.set(Calendar.SECOND, 0)
            dayCalendar.set(Calendar.MILLISECOND, 0)

            val label = sampleLabels[index % sampleLabels.size]

            add(
                TimerRecordDetails(
                    record = TimerRecord(
                        id = index + 1,
                        labelId = label.id,
                        startTimestamp = dayCalendar.timeInMillis,
                        value = minutes * 60
                    ),
                    label = label
                )
            )
        }
    }

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                HistogramWidget(
                    records = sampleRecords,
                    labels = sampleLabels,
                    height = 320.dp,
                    showComparison = true
                )
            }
        }
    }
}
