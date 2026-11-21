package com.literandltx.betime.data.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: TimerRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<TimerRecord>)

    @Update
    suspend fun update(record: TimerRecord)

    @Delete
    suspend fun delete(record: TimerRecord)

    @Transaction
    @Query("SELECT * FROM timer_records ORDER BY start_timestamp DESC")
    fun getAllTimerRecordDetails(): Flow<List<TimerRecordDetails>>

}
