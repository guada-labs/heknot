package com.fittrack.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fittrack.app.data.local.database.dao.MealDao
import com.fittrack.app.data.local.database.dao.UserProfileDao
import com.fittrack.app.data.local.database.dao.WeightDao
import com.fittrack.app.data.local.database.dao.WorkoutDao
import com.fittrack.app.data.local.database.entity.MealLog
import com.fittrack.app.data.local.database.entity.UserProfile
import com.fittrack.app.data.local.database.entity.WeightEntry
import com.fittrack.app.data.local.database.entity.WorkoutLog

@Database(
    entities = [
        UserProfile::class,
        WeightEntry::class,
        WorkoutLog::class,
        MealLog::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightDao(): WeightDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var Instance: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                .fallbackToDestructiveMigration() // Solo para MVP, cuidado en prod
                .build()
                .also { Instance = it }
            }
        }
    }
}
