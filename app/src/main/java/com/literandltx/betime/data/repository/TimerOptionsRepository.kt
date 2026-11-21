package com.literandltx.betime.data.repository

import com.literandltx.betime.data.model.TimerOption
import kotlinx.coroutines.flow.Flow

interface TimerOptionsRepository {

    fun getAllTimerOption(): Flow<List<TimerOption>>

    fun getTimerOption(id: Int): Flow<TimerOption?>

    suspend fun insert(timerOption: TimerOption)

    suspend fun update(timerOption: TimerOption)

    suspend fun delete(timerOption: TimerOption)

}
