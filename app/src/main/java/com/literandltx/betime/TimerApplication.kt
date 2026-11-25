package com.literandltx.betime

import android.app.Application
import com.literandltx.betime.data.AppContainer
import com.literandltx.betime.data.AppDataContainer
import com.literandltx.betime.data.TimerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TimerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            TimerDatabase.initialize(this@TimerApplication)
        }
    }
}
