package com.literandltx.betime.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.ActivePresetWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivePresetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActivePreset(preset: ActivePreset)

    @Transaction
    @Query("SELECT * FROM active_preset WHERE id = 1")
    suspend fun getActivePresetWithDetails(): ActivePresetWithDetails?
    
    @Transaction
    @Query("SELECT * FROM active_preset WHERE id = 1")
    fun observeActivePresetWithDetails(): Flow<ActivePresetWithDetails?>

}
