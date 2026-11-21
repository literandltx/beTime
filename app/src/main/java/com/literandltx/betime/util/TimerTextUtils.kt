package com.literandltx.betime.util

import android.content.Context
import com.literandltx.betime.R

const val SECONDS_PER_MINUTE = 60
const val MINUTES_PER_HOUR = 60
const val HOURS_PER_DAY = 24

const val DEFAULT_INITIAL_TIME_SECONDS = 25.times(SECONDS_PER_MINUTE)

const val SECONDS_PADDING_LENGTH = 2
const val MINUTES_PADDING_LENGTH = 2
const val DEFAULT_PADDING_CHAR = '0'

fun formatTime(totalSeconds: Int): String {
    val minutes = (totalSeconds / SECONDS_PER_MINUTE)
        .toString()
        .padStart(length = MINUTES_PADDING_LENGTH, padChar = DEFAULT_PADDING_CHAR)

    val seconds = (totalSeconds % SECONDS_PER_MINUTE)
        .toString()
        .padStart(length = SECONDS_PADDING_LENGTH, padChar = DEFAULT_PADDING_CHAR)

    return "$minutes:$seconds"
}

fun formatDetailedTime(totalSeconds: Int, context: Context): String {
    if (totalSeconds <= 0) return context.getString(R.string.time_format_zero)
    if (totalSeconds < SECONDS_PER_MINUTE) return context.getString(R.string.time_format_less_than_minute)

    val totalMinutes = totalSeconds / SECONDS_PER_MINUTE
    val hours = totalMinutes / MINUTES_PER_HOUR
    val minutes = totalMinutes % MINUTES_PER_HOUR
    val days = hours / HOURS_PER_DAY
    val remainingHours = hours % HOURS_PER_DAY

    return buildList {
        if (days > 0) {
            add(context.getString(R.string.time_format_days, days))
        }

        if (hours > 0) {
            add(context.getString(R.string.time_format_hours, if (days > 0) remainingHours else hours))
        }

        if (minutes > 0 || isEmpty()) {
            add(context.getString(R.string.time_format_minutes, minutes))
        }
    }.joinToString(" ")
}
