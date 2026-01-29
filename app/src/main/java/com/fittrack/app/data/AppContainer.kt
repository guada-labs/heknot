package com.fittrack.app.data

import android.content.Context
import com.fittrack.app.data.local.database.FitTrackDatabase
import com.fittrack.app.data.repository.OfflineFitTrackRepository
import com.fittrack.app.data.repository.FitTrackRepository

interface AppContainer {
    val fitTrackRepository: FitTrackRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    
    override val fitTrackRepository: FitTrackRepository by lazy {
        OfflineFitTrackRepository(FitTrackDatabase.getDatabase(context))
    }
}
