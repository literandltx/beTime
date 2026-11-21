package com.literandltx.betime

import android.app.Application
import com.literandltx.betime.data.AppContainer
import com.literandltx.betime.data.AppDataContainer

class TimerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
