package com.literandltx.betime.data.model

import androidx.compose.ui.graphics.Color
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "labels",
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent_id"])
    ]
)
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "parent_id")
    val parentId: Int? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val colorArgb: Int,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,

    @ColumnInfo(name = "is_uncertain_only")
    val isUncertainOnly: Boolean = true
) {
    val composeColor: Color get() = Color(colorArgb)
}
