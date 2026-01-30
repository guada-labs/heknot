package com.heknot.app

import android.app.Application
import com.heknot.app.data.AppContainer
import com.heknot.app.data.AppDataContainer

class HeknotApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
