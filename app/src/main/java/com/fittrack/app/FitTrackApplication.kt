package com.fittrack.app

import android.app.Application
import com.fittrack.app.data.AppContainer
import com.fittrack.app.data.AppDataContainer

class FitTrackApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
