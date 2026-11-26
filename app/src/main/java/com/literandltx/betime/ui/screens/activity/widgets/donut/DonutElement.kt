package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.util.formatTime
import kotlin.math.atan2

@Composable
fun DonutElement(
    stats: Map<Label?, Int>,
    modifier: Modifier = Modifier,
    gapSizeDegrees: Float = 2f,
    strokeWidth: Dp = 12.dp
) {
    val totalTime = stats.values.sum()

    var userRotationAngle by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .padding(
                start = 4.dp,
                end = 20.dp,
                bottom = 12.dp,
                top = 4.dp
            )
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)

                        val prev = change.previousPosition
                        val curr = change.position

                        val prevAngle = atan2(prev.y - center.y, prev.x - center.x)
                        val currAngle = atan2(curr.y - center.y, curr.x - center.x)

                        var delta = Math.toDegrees((currAngle - prevAngle).toDouble()).toFloat()

                        if (delta > 180f) delta -= 360f
                        else if (delta < -180f) delta += 360f

                        userRotationAngle += delta
                        change.consume()
                    }
                }
        ) {
            val strokeWidthPx = strokeWidth.toPx()
            var startAngle = -90f + userRotationAngle

            if (totalTime == 0) {
                drawArc(
                    color = Color.LightGray,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx)
                )
            } else {
                val activeStats = stats
                    .filterValues { it > 0 }
                    .entries
                    .sortedByDescending { it.value }

                val gapAngle = if (activeStats.size > 1) gapSizeDegrees else 0f
                val totalGapDegrees = activeStats.size * gapAngle
                val availableDegrees = 360f - totalGapDegrees

                activeStats.forEach { entry ->
                    val label = entry.key
                    val time = entry.value

                    val sweepAngle = (time.toFloat() / totalTime) * availableDegrees
                    val segmentColor = label?.composeColor ?: Color.Gray

                    drawArc(
                        color = segmentColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidthPx,
                            cap = StrokeCap.Butt
                        )
                    )
                    startAngle += sweepAngle + gapAngle
                }
            }
        }

        Text(
            text = formatTime(totalTime),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
