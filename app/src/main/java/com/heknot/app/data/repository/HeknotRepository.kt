package com.heknot.app.data.repository

import com.heknot.app.data.local.database.entity.GuidedExercise
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.SleepLog
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WaterLog
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutCategory
import com.heknot.app.data.local.database.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HeknotRepository {
    
    // User Profile
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun insertOrUpdateUserProfile(userProfile: UserProfile): Long
    suspend fun updateCurrentWeight(weight: Float): Int
    suspend fun updateDarkMode(enabled: Boolean?): Int
    suspend fun updateBiometricEnabled(enabled: Boolean): Int
    
    // Weights
    fun getAllWeights(): Flow<List<WeightEntry>>
    fun getRecentWeights(limit: Int): Flow<List<WeightEntry>>
    fun getLastWeight(): Flow<WeightEntry?>
    suspend fun insertWeight(weightEntry: WeightEntry): Long
    suspend fun deleteWeight(weightEntry: WeightEntry): Int
    
    // Workouts
    fun getAllWorkouts(): Flow<List<WorkoutLog>>
    fun getWorkoutsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutLog>>
    suspend fun getWorkoutCountByDate(date: LocalDate): Int
    suspend fun insertWorkout(workoutLog: WorkoutLog): Long
    suspend fun updateWorkout(workoutLog: WorkoutLog): Int
    suspend fun deleteWorkout(workoutLog: WorkoutLog): Int
    fun getTotalCaloriesBurnedByDate(date: LocalDate): Flow<Int?>
    
    // --- Meals ---
    fun getAllMeals(): Flow<List<MealLog>>
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    fun getTotalCaloriesConsumedByDate(date: LocalDate): Flow<Int?>
    suspend fun insertMeal(mealLog: MealLog): Long
    suspend fun deleteMeal(mealLog: MealLog): Int

    // --- Water ---
    fun getWaterLogsByDate(date: LocalDate): Flow<List<WaterLog>>
    fun getTotalWaterByDate(date: LocalDate): Flow<Int?>
    suspend fun insertWaterLog(waterLog: WaterLog): Long
    suspend fun deleteWaterLog(waterLog: WaterLog): Int

    // --- Sleep ---
    fun getSleepLogsByDate(date: LocalDate): Flow<List<SleepLog>>
    fun getAllSleepLogs(): Flow<List<SleepLog>>
    suspend fun insertSleepLog(sleepLog: SleepLog): Long
    suspend fun deleteSleepLog(sleepLog: SleepLog): Int

    // --- Guided Exercises ---
    fun getAllGuidedExercises(): Flow<List<GuidedExercise>>
    fun getGuidedExercisesByCategory(category: WorkoutCategory): Flow<List<GuidedExercise>>
    suspend fun insertGuidedExercise(exercise: GuidedExercise): Long

    // Reset
    suspend fun resetData()
}
