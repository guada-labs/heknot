package com.heknot.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heknot.app.data.local.database.dao.FoodItemDao
import com.heknot.app.data.local.database.dao.GuidedExerciseDao
import com.heknot.app.data.local.database.dao.MealDao
import com.heknot.app.data.local.database.dao.RecipeDao
import com.heknot.app.data.local.database.dao.SleepDao
import com.heknot.app.data.local.database.dao.UserProfileDao
import com.heknot.app.data.local.database.dao.WaterDao
import com.heknot.app.data.local.database.dao.WeightDao
import com.heknot.app.data.local.database.dao.WorkoutDao
import com.heknot.app.data.local.database.entity.FoodItem
import com.heknot.app.data.local.database.entity.GuidedExercise
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.Recipe
import com.heknot.app.data.local.database.entity.RecipeIngredient
import com.heknot.app.data.local.database.entity.SleepLog
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WaterLog
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog

@Database(
    entities = [
        UserProfile::class,
        WeightEntry::class,
        WorkoutLog::class,
        MealLog::class,
        WaterLog::class,
        SleepLog::class,
        GuidedExercise::class,
        FoodItem::class,
        Recipe::class,
        RecipeIngredient::class
    ],
    version = 13,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HeknotDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightDao(): WeightDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao
    abstract fun waterDao(): WaterDao
    abstract fun sleepDao(): SleepDao
    abstract fun guidedExerciseDao(): GuidedExerciseDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun recipeDao(): RecipeDao

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
                .addMigrations(
                    Migrations.MIGRATION_7_8, 
                    Migrations.MIGRATION_8_9,
                    Migrations.MIGRATION_9_10,
                    Migrations.MIGRATION_10_11,
                    Migrations.MIGRATION_11_12,
                    Migrations.MIGRATION_12_13
                )
                .fallbackToDestructiveMigration() // Fallback if other migrations are missing
                .build()
                .also { Instance = it }
            }
        }
    }
}
