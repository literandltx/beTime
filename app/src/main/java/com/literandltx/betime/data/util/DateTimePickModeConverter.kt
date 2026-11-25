package com.literandltx.betime.data.util

import androidx.room3.ColumnTypeConverter
import com.literandltx.betime.data.model.DateTimePickMode

class DateTimePickModeConverter {
    @ColumnTypeConverter
    fun fromMode(mode: DateTimePickMode): String = mode.name

    @ColumnTypeConverter
    fun toMode(value: String): DateTimePickMode =
        runCatching { DateTimePickMode.valueOf(value) }
            .getOrDefault(DateTimePickMode.DATE_AND_TIME)
}
