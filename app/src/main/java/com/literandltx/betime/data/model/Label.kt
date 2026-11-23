package com.literandltx.betime.data.model

import androidx.compose.ui.graphics.Color
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val colorArgb: Int,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,

    @ColumnInfo(name = "is_uncertain_only")
    val isUncertainOnly: Boolean = false
) {
    val composeColor: Color get() = Color(colorArgb)
}
