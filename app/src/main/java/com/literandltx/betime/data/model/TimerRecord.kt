package com.literandltx.betime.data.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import androidx.room3.Relation

@Entity(
    tableName = "timer_records",
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["label_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["label_id"])
    ]
)
data class TimerRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "value")
    val value: Int = 0,

    @ColumnInfo(name = "start_timestamp")
    val startTimestamp: Long,

    @ColumnInfo(name = "label_id")
    val labelId: Int? = null,
)

data class TimerRecordDetails(
    @Embedded val record: TimerRecord,

    @Relation(
        parentColumns = ["label_id"],
        entityColumns = ["id"]
    )
    val label: Label?
)
