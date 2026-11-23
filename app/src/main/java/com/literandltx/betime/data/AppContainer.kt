package com.literandltx.betime.data

import android.content.Context
import com.literandltx.betime.data.preferences.SettingsPreferences
import com.literandltx.betime.data.preferences.dataStore
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.repository.impl.OfflineLabelsRepository
import com.literandltx.betime.data.repository.impl.OfflineTimerOptionsRepository
import com.literandltx.betime.data.repository.TimerOptionsRepository
import com.literandltx.betime.data.repository.ActivePresetRepository
import com.literandltx.betime.data.repository.impl.OfflineActivePresetRepository
import com.literandltx.betime.data.repository.impl.OfflineTimerRecordRepository
import com.literandltx.betime.data.repository.TimerRecordRepository

interface AppContainer {
    val labelsRepository: LabelsRepository
    val timerOptionRepository: TimerOptionsRepository
    val activePresetRepository: ActivePresetRepository
    val timerRecordRepository: TimerRecordRepository
    val settingsPreferences: SettingsPreferences
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val labelsRepository: LabelsRepository by lazy {
        OfflineLabelsRepository(
            labelDao = TimerDatabase.getDatabase(context).labelDao()
        )
    }

    override val timerOptionRepository: TimerOptionsRepository by lazy {
        OfflineTimerOptionsRepository(
            timerOptionDao = TimerDatabase.getDatabase(context).timerOptionDao()
        )
    }

    override val activePresetRepository: ActivePresetRepository by lazy {
        OfflineActivePresetRepository(
            activePresetDao = TimerDatabase.getDatabase(context).activePresetDao()
        )
    }

    override val timerRecordRepository: TimerRecordRepository by lazy {
        OfflineTimerRecordRepository(
            timerRecordDao = TimerDatabase.getDatabase(context).timerRecordDao()
        )
    }

    override val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(context.dataStore)
    }

}
