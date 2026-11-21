package com.literandltx.betime.data.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.literandltx.betime.data.model.TimerOption
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerOptionDao {

    @Query("SELECT * FROM timer_options ORDER BY value ASC")
    fun getAllTimerOption(): Flow<List<TimerOption>>

    @Query("SELECT * FROM timer_options WHERE id = :id")
    fun getTimerOption(id: Int): Flow<TimerOption?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(timerOption: TimerOption)

    @Update
    suspend fun update(timerOption: TimerOption)

    @Delete
    suspend fun delete(timerOption: TimerOption)

}
