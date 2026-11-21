package com.literandltx.betime.data.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import androidx.room3.Relation

@Entity(
    tableName = "active_preset",
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["label_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TimerOption::class,
            parentColumns = ["id"],
            childColumns = ["timer_option_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["label_id"]),
        Index(value = ["timer_option_id"])
    ]
)
data class ActivePreset(
    @PrimaryKey
    val id: Int = 1,

    @ColumnInfo(name = "label_id")
    val labelId: Int? = null,

    @ColumnInfo(name = "timer_option_id")
    val timerOptionId: Int? = null
)

data class ActivePresetWithDetails(
    @Embedded val activePreset: ActivePreset,

    @Relation(
        parentColumns = ["label_id"],
        entityColumns = ["id"]
    )
    val label: Label?,

    @Relation(
        parentColumns = ["timer_option_id"],
        entityColumns = ["id"]
    )
    val timerOption: TimerOption?
)
