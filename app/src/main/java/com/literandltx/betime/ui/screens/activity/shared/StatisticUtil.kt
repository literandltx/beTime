package com.literandltx.betime.ui.screens.activity.shared

import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecordDetails
import java.util.Calendar

fun getLabelStatisticMap(records: List<TimerRecordDetails>): Map<Label?, Int> {
    return records
        .groupingBy { it.label }
        .fold(0) { accumulator, element ->
            accumulator + element.record.value
        }
}

fun getFilteredRecords(
    records: List<TimerRecordDetails>,
    timeSpan: TimeSpan,
    offset: Int = 0,
    customStartDate: Long? = null,
    customEndDate: Long? = null
): List<TimerRecordDetails> {

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    when (timeSpan) {
        TimeSpan.DAY -> calendar.add(Calendar.DAY_OF_YEAR, offset)
        TimeSpan.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, offset)
        TimeSpan.MONTH -> calendar.add(Calendar.MONTH, offset)
        TimeSpan.YEAR -> calendar.add(Calendar.YEAR, offset)
        else -> {}
    }

    val periodStart = calendar.timeInMillis

    return records.filter { details ->
        val recordTime = details.record.startTimestamp

        when (timeSpan) {
            TimeSpan.DAY -> {
                val endOfDay = periodStart + 86_400_000L
                recordTime in periodStart until endOfDay
            }

            TimeSpan.WEEK -> {
                val weekCal = Calendar.getInstance().apply { timeInMillis = periodStart }
                weekCal.set(Calendar.DAY_OF_WEEK, weekCal.firstDayOfWeek)
                val startOfWeek = weekCal.timeInMillis
                val endOfWeek = startOfWeek + (7 * 86_400_000L)
                recordTime in startOfWeek until endOfWeek
            }

            TimeSpan.MONTH -> {
                val monthCal = Calendar.getInstance().apply { timeInMillis = periodStart }
                monthCal.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = monthCal.timeInMillis

                monthCal.add(Calendar.MONTH, 1)
                val endOfMonth = monthCal.timeInMillis
                recordTime in startOfMonth until endOfMonth
            }

            TimeSpan.YEAR -> {
                val yearCal = Calendar.getInstance().apply { timeInMillis = periodStart }
                yearCal.set(Calendar.DAY_OF_YEAR, 1)
                val startOfYear = yearCal.timeInMillis

                yearCal.add(Calendar.YEAR, 1)
                val endOfYear = yearCal.timeInMillis
                recordTime in startOfYear until endOfYear
            }

            TimeSpan.ALL_TIME -> true
            TimeSpan.CUSTOM -> {
                if (customStartDate == null) return@filter true

                val end = if (customEndDate != null) {
                    customEndDate + 86_400_000L - 1L
                } else {
                    customStartDate + 86_400_000L - 1L
                }

                recordTime in customStartDate..end
            }
        }
    }
}
