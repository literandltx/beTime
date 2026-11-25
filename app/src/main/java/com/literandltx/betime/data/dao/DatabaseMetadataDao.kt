package com.literandltx.betime.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.literandltx.betime.data.model.DatabaseMetadata

@Dao
interface DatabaseMetadataDao {

    @Query("SELECT EXISTS(SELECT 1 FROM database_metadata WHERE id = 1)")
    suspend fun isInitialized(): Boolean

    @Insert
    suspend fun insert(metadata: DatabaseMetadata)
}
