package com.literandltx.betime.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "database_metadata")
data class DatabaseMetadata(
    @PrimaryKey
    val id: Int = 1
)
