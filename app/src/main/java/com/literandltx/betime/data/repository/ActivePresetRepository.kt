package com.literandltx.betime.data.repository

import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.ActivePresetWithDetails
import kotlinx.coroutines.flow.Flow

interface ActivePresetRepository {

    suspend fun setActivePreset(preset: ActivePreset)

    suspend fun getActivePresetWithDetails(): ActivePresetWithDetails?
    
    fun observeActivePresetWithDetails(): Flow<ActivePresetWithDetails?>

}
