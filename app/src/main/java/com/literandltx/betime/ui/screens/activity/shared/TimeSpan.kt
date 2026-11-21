package com.literandltx.betime.ui.screens.activity.shared

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.literandltx.betime.R

enum class TimeSpan {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    ALL_TIME,
    CUSTOM
}

fun TimeSpan.getName(context: Context): String {
    return when (this) {
        TimeSpan.DAY -> context.getString(R.string.today)
        TimeSpan.WEEK -> context.getString(R.string.timeSpanWeek)
        TimeSpan.MONTH -> context.getString(R.string.timeSpanMonth)
        TimeSpan.YEAR -> context.getString(R.string.timeSpanYear)
        TimeSpan.ALL_TIME -> context.getString(R.string.timeSpanAll)
        TimeSpan.CUSTOM -> context.getString(R.string.customRangeAction)
    }
}

@Composable
fun TimeSpan.getName(): String {
    return getName(LocalContext.current)
}
