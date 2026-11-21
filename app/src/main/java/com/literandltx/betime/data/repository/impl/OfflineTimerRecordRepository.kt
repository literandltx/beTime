package com.literandltx.betime.data.repository.impl

import com.literandltx.betime.data.dao.TimerRecordDao
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.repository.TimerRecordRepository
import kotlinx.coroutines.flow.Flow

class OfflineTimerRecordRepository(
    private val timerRecordDao: TimerRecordDao
) : TimerRecordRepository {

    override suspend fun insert(record: TimerRecord) {
        timerRecordDao.insert(record)
    }

    override suspend fun insertAll(records: List<TimerRecord>) {
        timerRecordDao.insertAll(records)
    }

    override suspend fun update(record: TimerRecord) {
        timerRecordDao.update(record)
    }

    override suspend fun delete(record: TimerRecord) {
        timerRecordDao.delete(record)
    }

    override fun getAllTimerRecords(): Flow<List<TimerRecordDetails>> {
        return timerRecordDao.getAllTimerRecordDetails()
    }

}
