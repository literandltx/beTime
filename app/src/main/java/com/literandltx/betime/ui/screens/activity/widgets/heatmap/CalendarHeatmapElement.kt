package com.literandltx.betime.ui.screens.activity.widgets.heatmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.theme.TimerTheme
import java.util.Calendar

data class DayCell(
    val day: Int,
    val count: Int,
    val level: Int,
    val isToday: Boolean
)

@Composable
fun CalendarHeatmapElement(
    year: Int,
    month: Int,
    daysInMonth: Int,
    dailyCounts: List<Int>,
    levelThresholds: List<Int>,
    modifier: Modifier = Modifier
) {
    val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")

    val cells = remember(year, month, daysInMonth, dailyCounts) {
        val cal = Calendar.getInstance().apply {
            set(year, month, 1)
        }
        val firstWeekday = cal.get(Calendar.DAY_OF_WEEK) - 1

        val today = Calendar.getInstance()
        val isCurrentMonth = today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month
        val todayDate = today.get(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<DayCell?>()
        repeat(firstWeekday) {
            list.add(null)
        }

        for (day in 1..daysInMonth) {
            val count = dailyCounts.getOrElse(day - 1) { 0 }
            list.add(
                DayCell(
                    day = day,
                    count = count,
                    level = toLevel(count, levelThresholds),
                    isToday = isCurrentMonth && todayDate == day
                )
            )
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Weekday header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { wd ->
                Text(
                    text = wd,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        // Calendar grid (7 columns)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val rows = cells.chunked(7)
            rows.forEach { weekRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weekRow.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell != null) {
                                val backgroundColor = getLevelColor(cell.level)
                                val textColor = getLevelTextColor(cell.level)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(backgroundColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cell.day.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                    repeat(7 - weekRow.size) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }

        // Legend row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { level ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(getLevelColor(level))
                    )
                }
            }
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

private fun toLevel(count: Int, thresholds: List<Int>): Int {
    val t1 = thresholds.getOrElse(0) { 1 }
    val t2 = thresholds.getOrElse(1) { 2 }
    val t3 = thresholds.getOrElse(2) { 3 }
    val t4 = thresholds.getOrElse(3) { 4 }

    if (count < t1) return 0
    if (count < t2) return 1
    if (count < t3) return 2
    if (count < t4) return 3
    return 4
}

@Composable
private fun getLevelColor(level: Int): Color {
    return when (level) {
        0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        1 -> Color(0xFF14532D)
        2 -> Color(0xFF15803D)
        3 -> Color(0xFF16A34A)
        4 -> Color(0xFF4ADE80)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
private fun getLevelTextColor(level: Int): Color {
    return if (level >= 4) Color(0xFF052E12) else MaterialTheme.colorScheme.onSurface
}

@Preview(showBackground = true)
@Composable
fun CalendarHeatmapElementPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val sampleCounts = listOf(1, 3, 0, 2, 5, 4, 1, 0, 2, 3)

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                CalendarHeatmapElement(
                    year = 2026,
                    month = 5,
                    daysInMonth = 30,
                    dailyCounts = sampleCounts,
                    levelThresholds = listOf(1, 2, 3, 4),
                )
            }
        }
    }
}
