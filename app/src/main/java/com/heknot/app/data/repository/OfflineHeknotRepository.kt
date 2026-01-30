package com.heknot.app.data.repository

import com.heknot.app.data.local.database.HeknotDatabase
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineHeknotRepository(private val database: HeknotDatabase) : HeknotRepository {
    
    // --- User Profile ---
    override fun getUserProfile(): Flow<UserProfile?> = 
        database.userProfileDao().getUserProfile()
        
    override suspend fun insertOrUpdateUserProfile(userProfile: UserProfile): Long =
        database.userProfileDao().insertOrUpdate(userProfile)
        
    override suspend fun updateCurrentWeight(weight: Float): Int =
        database.userProfileDao().updateCurrentWeight(weight)

    override suspend fun updateDarkMode(enabled: Boolean?): Int =
        database.userProfileDao().updateDarkMode(enabled)

    // --- Weights ---
    override fun getAllWeights(): Flow<List<WeightEntry>> = 
        database.weightDao().getAllWeights()
        
    override fun getRecentWeights(limit: Int): Flow<List<WeightEntry>> =
        database.weightDao().getRecentWeights(limit)
        
    override fun getLastWeight(): Flow<WeightEntry?> =
        database.weightDao().getLastWeight()
        
    override suspend fun insertWeight(weightEntry: WeightEntry): Long =
        database.weightDao().insert(weightEntry)
        
    override suspend fun deleteWeight(weightEntry: WeightEntry): Int =
        database.weightDao().delete(weightEntry)

    // --- Workouts ---
    override fun getAllWorkouts(): Flow<List<WorkoutLog>> =
        database.workoutDao().getAllWorkouts()
        
    override fun getWorkoutsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutLog>> =
        database.workoutDao().getWorkoutsBetween(startDate, endDate)
        
    override suspend fun getWorkoutCountByDate(date: LocalDate): Int =
        database.workoutDao().getWorkoutCountByDate(date)
        
    override suspend fun insertWorkout(workoutLog: WorkoutLog): Long =
        database.workoutDao().insert(workoutLog)

    override suspend fun updateWorkout(workoutLog: WorkoutLog): Int =
        database.workoutDao().update(workoutLog)
        
    override suspend fun deleteWorkout(workoutLog: WorkoutLog): Int =
        database.workoutDao().delete(workoutLog)

    override fun getTotalCaloriesBurnedByDate(date: LocalDate): Flow<Int?> =
        database.workoutDao().getTotalCaloriesBurnedByDate(date)

    // --- Meals ---
    override fun getAllMeals(): Flow<List<MealLog>> =
        database.mealDao().getAllMeals()

    override fun getMealsByDate(date: LocalDate): Flow<List<MealLog>> =
        database.mealDao().getMealsByDate(date)

    override fun getTotalCaloriesConsumedByDate(date: LocalDate): Flow<Int?> =
        database.mealDao().getTotalCaloriesByDate(date)
        
    override suspend fun insertMeal(mealLog: MealLog): Long =
        database.mealDao().insert(mealLog)
        
    override suspend fun deleteMeal(mealLog: MealLog): Int =
        database.mealDao().delete(mealLog)

    override suspend fun resetData() {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
