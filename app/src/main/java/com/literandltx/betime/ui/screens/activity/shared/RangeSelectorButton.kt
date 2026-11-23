package com.literandltx.betime.ui.screens.activity.shared

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.literandltx.betime.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RangeSelectorButton(
    selectedTimeSpan: TimeSpan,
    timeOffset: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customStartDate: Long = 0L,
    customEndDate: Long = 0L
) {
    val context = LocalContext.current

    val dateTitleText = remember(
        selectedTimeSpan,
        timeOffset,
        customStartDate,
        customEndDate,
        context
    ) {
        getRangeTitle(
            selectedTimeSpan,
            timeOffset,
            customStartDate,
            customEndDate,
            context = context
        )
    }

    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = dateTitleText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RangeSelectorWithTextButton(
    selectedTimeSpan: TimeSpan,
    timeOffset: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customStartDate: Long = 0L,
    customEndDate: Long = 0L
) {
    val context = LocalContext.current

    val dateTitleText = remember(
        selectedTimeSpan,
        timeOffset,
        customStartDate,
        customEndDate,
        context
    ) {
        getRangeTitle(
            selectedTimeSpan,
            timeOffset,
            customStartDate,
            customEndDate,
            context = context
        )
    }

    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dateTitleText)

            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun HistogramRangeSelectorWithTextButton(
    selectedTimeSpan: TimeSpan,
    timeOffset: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTitleText = remember(selectedTimeSpan, timeOffset) {
        getHistogramRangeTitle(selectedTimeSpan, timeOffset)
    }

    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dateTitleText)

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
    }
}

private fun getRangeTitle(
    selectedTimeSpan: TimeSpan,
    timeOffset: Int,
    customStartDate: Long,
    customEndDate: Long,
    context: Context
): String {
    return when (selectedTimeSpan) {
        TimeSpan.DAY -> when (timeOffset) {
            0 -> context.getString(R.string.today)
            -1 -> context.getString(R.string.yesterday)
            else -> getDayTitle(timeOffset)
        }

        TimeSpan.WEEK -> getFormattedWeekTitle(timeOffset)
        TimeSpan.MONTH -> getFormattedMonthTitle(timeOffset)
        TimeSpan.YEAR -> getFormattedYearTitle(timeOffset)
        TimeSpan.ALL_TIME -> selectedTimeSpan.getName(context)
        TimeSpan.CUSTOM -> getFormattedCustomRangeTitle(
            customStartDate,
            customEndDate,
            context
        )
    }
}

private fun getHistogramRangeTitle(selectedTimeSpan: TimeSpan, timeOffset: Int): String {
    return when (selectedTimeSpan) {
        TimeSpan.MONTH -> {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, timeOffset)
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        }

        TimeSpan.YEAR -> {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, timeOffset)
            SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        }

        else -> {
            val calendar = Calendar.getInstance()
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        }
    }
}

private fun getDayTitle(timeOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, timeOffset)
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

private fun getFormattedWeekTitle(timeOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, timeOffset)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    val startSdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    val start = startSdf.format(calendar.time)

    calendar.add(Calendar.DAY_OF_WEEK, 6)
    val endSdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    val end = endSdf.format(calendar.time)

    return "$start - $end"
}

private fun getFormattedMonthTitle(timeOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, timeOffset)
    val sdf = SimpleDateFormat("MMMM", Locale.getDefault())
    return sdf.format(calendar.time)
}

private fun getFormattedYearTitle(timeOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, timeOffset)
    val sdf = SimpleDateFormat("yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

private fun getFormattedCustomRangeTitle(startDate: Long, endDate: Long, context: Context): String {
    if (startDate == 0L) return context.getString(R.string.customMessage)
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    val startStr = sdf.format(startDate)

    if (endDate == 0L || endDate == startDate) {
        return startStr
    }

    val endStr = sdf.format(endDate)
    return "$startStr - $endStr"
}
