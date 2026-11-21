package com.literandltx.betime.data.repository

import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import kotlinx.coroutines.flow.Flow

interface TimerRecordRepository {

    suspend fun insert(record: TimerRecord)

    suspend fun insertAll(records: List<TimerRecord>)

    suspend fun update(record: TimerRecord)

    suspend fun delete(record: TimerRecord)

    fun getAllTimerRecords(): Flow<List<TimerRecordDetails>>

}
