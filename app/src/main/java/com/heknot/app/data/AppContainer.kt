package com.heknot.app.data

import android.content.Context
import com.heknot.app.data.local.backup.BackupManager
import com.heknot.app.data.local.database.HeknotDatabase
import com.heknot.app.data.repository.OfflineHeknotRepository
import com.heknot.app.data.repository.HeknotRepository

interface AppContainer {
    val HeknotRepository: HeknotRepository
    val backupManager: BackupManager
    val exerciseRepository: com.heknot.app.data.repository.ExerciseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    
    override val HeknotRepository: HeknotRepository by lazy {
        OfflineHeknotRepository(HeknotDatabase.getDatabase(context))
    }

    override val backupManager: BackupManager by lazy {
        BackupManager(context, HeknotRepository)
    }

    override val exerciseRepository: com.heknot.app.data.repository.ExerciseRepository by lazy {
        com.heknot.app.data.repository.AssetExerciseRepository(context)
    }
}
