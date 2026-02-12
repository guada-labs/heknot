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
import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import com.heknot.app.data.local.database.entity.RoutineExercise
import com.heknot.app.data.local.database.dao.TrainingDao
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
        RecipeIngredient::class,
        UserEquipment::class,
        WorkoutPlan::class,
        WorkoutRoutine::class,
        RoutineExercise::class
    ],
    version = 16,
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
    abstract fun trainingDao(): com.heknot.app.data.local.database.dao.TrainingDao

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
                    Migrations.MIGRATION_12_13,
                    Migrations.MIGRATION_13_14,
                    Migrations.MIGRATION_14_15,
                    Migrations.MIGRATION_15_16
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration() // Fallback if other migrations are missing
                .build()
                .also { Instance = it }
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            // Usamos un hilo separado o WorkManager para la pre-población real si es pesada.
            // Para este caso sencillo, lo haremos via CoroutineScope en el Repositorio o una utilidad.
            // Room recomienda no hacer operaciones pesadas en onCreate directamente bloqueando.
        }
    }
}
