package com.literandltx.betime.data.repository.impl

import com.literandltx.betime.data.dao.TimerOptionDao
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.repository.TimerOptionsRepository
import kotlinx.coroutines.flow.Flow

class OfflineTimerOptionsRepository(
    private val timerOptionDao: TimerOptionDao
) : TimerOptionsRepository {

    override fun getAllTimerOption(): Flow<List<TimerOption>> = timerOptionDao.getAllTimerOption()

    override fun getTimerOption(id: Int): Flow<TimerOption?> = timerOptionDao.getTimerOption(id)

    override suspend fun insert(timerOption: TimerOption) = timerOptionDao.insert(timerOption)

    override suspend fun update(timerOption: TimerOption) = timerOptionDao.update(timerOption)

    override suspend fun delete(timerOption: TimerOption) = timerOptionDao.delete(timerOption)

}
