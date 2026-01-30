package com.heknot.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heknot.app.data.local.database.dao.MealDao
import com.heknot.app.data.local.database.dao.UserProfileDao
import com.heknot.app.data.local.database.dao.WeightDao
import com.heknot.app.data.local.database.dao.WorkoutDao
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog

@Database(
    entities = [
        UserProfile::class,
        WeightEntry::class,
        WorkoutLog::class,
        MealLog::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HeknotDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightDao(): WeightDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var Instance: HeknotDatabase? = null

        fun getDatabase(context: Context): HeknotDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HeknotDatabase::class.java,
                    "heknot_database"
                )
                .addMigrations(Migrations.MIGRATION_7_8)
                .fallbackToDestructiveMigration() // Fallback if other migrations are missing
                .build()
                .also { Instance = it }
            }
        }
    }
}
