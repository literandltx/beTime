package com.literandltx.betime.data.repository.impl

import com.literandltx.betime.data.dao.ActivePresetDao
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.ActivePresetWithDetails
import com.literandltx.betime.data.repository.ActivePresetRepository
import kotlinx.coroutines.flow.Flow

class OfflineActivePresetRepository(
    private val activePresetDao: ActivePresetDao
) : ActivePresetRepository {

    override suspend fun setActivePreset(preset: ActivePreset) {
        activePresetDao.setActivePreset(preset)
    }

    override suspend fun getActivePresetWithDetails(): ActivePresetWithDetails? {
        return activePresetDao.getActivePresetWithDetails()
    }

    override fun observeActivePresetWithDetails(): Flow<ActivePresetWithDetails?> {
        return activePresetDao.observeActivePresetWithDetails()
    }

}
