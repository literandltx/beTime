package com.literandltx.betime.ui.screens.activity.widgets.histogram

import android.graphics.Paint
import android.graphics.Path as GPath
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
import androidx.compose.ui.res.stringResource
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
import com.literandltx.betime.ui.screens.activity.ActivityDataIndex
import com.literandltx.betime.ui.screens.activity.buildActivityIndex
import com.literandltx.betime.ui.screens.activity.shared.LabelSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.ThreeOptionTimeSelectorModal
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.screens.activity.shared.WidgetContainer
import com.literandltx.betime.ui.theme.TimerTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

enum class Granularity {
    MONTH,
    YEAR
}

data class ChartBucket(val label: String, val minutes: Int)

@Composable
fun HistogramWidget(
    labels: List<Label>,
    height: Dp,
    modifier: Modifier = Modifier,
    index: ActivityDataIndex = ActivityDataIndex(),
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

    val hiddenLabelIds: Set<Int> = remember(labels, selectedLabels) {
        if (selectedLabels.size == labels.size) {
            emptySet()
        } else {
            labels.asSequence()
                .filter { !selectedLabels.contains(it) }
                .map { it.id }
                .toSet()
        }
    }

    val monthNames: List<String> = listOf(
        stringResource(R.string.monthJan),
        stringResource(R.string.monthFeb),
        stringResource(R.string.monthMar),
        stringResource(R.string.monthApr),
        stringResource(R.string.monthMay),
        stringResource(R.string.monthJun),
        stringResource(R.string.monthJul),
        stringResource(R.string.monthAug),
        stringResource(R.string.monthSep),
        stringResource(R.string.monthOct),
        stringResource(R.string.monthNov),
        stringResource(R.string.monthDec)
    )

    val currentBuckets = remember(index, granularity, timeOffset, hiddenLabelIds, monthNames) {
        buildBucketsFromIndex(
            index = index,
            granularity = granularity,
            periodOffset = timeOffset,
            hiddenLabels = hiddenLabelIds,
            monthNames = monthNames
        )
    }

    val previousBuckets =
        remember(index, granularity, timeOffset, hiddenLabelIds, showComparison, monthNames) {
            if (!showComparison) {
                emptyList()
            } else {
                buildBucketsFromIndex(
                    index = index,
                    granularity = granularity,
                    periodOffset = timeOffset - 1,
                    hiddenLabels = hiddenLabelIds,
                    monthNames = monthNames
                )
            }
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

        val yAxisPaint = remember {
            Paint().apply {
                textSize = 20f
                isAntiAlias = true
                textAlign = Paint.Align.RIGHT
            }
        }
        val xAxisPaint = remember {
            Paint().apply {
                textSize = 18f
                isAntiAlias = true
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            yAxisPaint.color = textColorInt
            xAxisPaint.color = textColorInt

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
                yAxisPaint = yAxisPaint
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
                    xAxisPaint = xAxisPaint
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
    yAxisPaint: Paint
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
            canvas.nativeCanvas.drawText(
                Math.round(v).toString(),
                paddingLeft - 10f,
                y + 7f,
                yAxisPaint
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
    xAxisPaint: Paint
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
            colors = listOf(
                primaryColor.copy(alpha = 0.3f),
                primaryColor.copy(alpha = 0.02f),
                Color.Transparent
            ),
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
                cubicTo(
                    controlPoint1.x,
                    controlPoint1.y,
                    controlPoint2.x,
                    controlPoint2.y,
                    pt.x,
                    pt.y
                )
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
                xAxisPaint.textAlign = when {
                    isFirst -> Paint.Align.LEFT
                    isLast -> Paint.Align.RIGHT
                    else -> Paint.Align.CENTER
                }

                canvas.nativeCanvas.drawText(
                    currentBuckets[idx].label,
                    pt.x,
                    height - 2f,
                    xAxisPaint
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

private fun buildBucketsFromIndex(
    index: ActivityDataIndex,
    granularity: Granularity,
    periodOffset: Int,
    hiddenLabels: Set<Int>,
    monthNames: List<String>
): List<ChartBucket> {
    val today = LocalDate.now()

    return when (granularity) {
        Granularity.MONTH -> {
            val ym = YearMonth.from(today).plusMonths(periodOffset.toLong())
            val daysInMonth = ym.lengthOfMonth()
            val totalsInSeconds = IntArray(daysInMonth)

            for (day in 1..daysInMonth) {
                val epochDay = ym.atDay(day).toEpochDay()
                val bucket = index.byDay[epochDay] ?: continue
                if (hiddenLabels.isEmpty()) {
                    totalsInSeconds[day - 1] = bucket.totalSeconds
                } else {
                    var sum = 0
                    for (rec in bucket.records) {
                        val lid = rec.labelId
                        if (lid == null || !hiddenLabels.contains(lid)) {
                            sum += rec.durationSeconds
                        }
                    }
                    totalsInSeconds[day - 1] = sum
                }
            }

            List(daysInMonth) { i ->
                ChartBucket(label = (i + 1).toString(), minutes = totalsInSeconds[i] / 60)
            }
        }

        Granularity.YEAR -> {
            val year = today.year + periodOffset
            val totalsInSeconds = IntArray(12)

            for (month in 1..12) {
                val ym = YearMonth.of(year, month)
                val daysInMonth = ym.lengthOfMonth()
                var monthSec = 0

                for (day in 1..daysInMonth) {
                    val epochDay = ym.atDay(day).toEpochDay()
                    val bucket = index.byDay[epochDay] ?: continue
                    if (hiddenLabels.isEmpty()) {
                        monthSec += bucket.totalSeconds
                    } else {
                        for (rec in bucket.records) {
                            val lid = rec.labelId
                            if (lid == null || !hiddenLabels.contains(lid)) {
                                monthSec += rec.durationSeconds
                            }
                        }
                    }
                }
                totalsInSeconds[month - 1] = monthSec
            }

            List(12) { i ->
                ChartBucket(label = monthNames[i], minutes = totalsInSeconds[i] / 60)
            }
        }
    }
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

    val nowMillis = System.currentTimeMillis()
    val sampleRecords = listOf(
        TimerRecordDetails(
            record = TimerRecord(id = 1, labelId = 1, startTimestamp = nowMillis, value = 5400),
            label = sampleLabels[0]
        ),
        TimerRecordDetails(
            record = TimerRecord(
                id = 2,
                labelId = 2,
                startTimestamp = nowMillis - 86_400_000L,
                value = 3600
            ),
            label = sampleLabels[1]
        )
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                HistogramWidget(
                    index = buildActivityIndex(
                        records = sampleRecords,
                        labels = sampleLabels,
                        zoneId = ZoneId.systemDefault()
                    ),
                    labels = sampleLabels,
                    height = 320.dp,
                    showComparison = true
                )
            }
        }
    }
}
