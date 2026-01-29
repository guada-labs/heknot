package com.fittrack.app.data.repository

import com.fittrack.app.data.local.database.entity.MealLog
import com.fittrack.app.data.local.database.entity.UserProfile
import com.fittrack.app.data.local.database.entity.WeightEntry
import com.fittrack.app.data.local.database.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface FitTrackRepository {
    
    // User Profile
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun insertOrUpdateUserProfile(userProfile: UserProfile): Long
    suspend fun updateCurrentWeight(weight: Float): Int
    
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
    suspend fun deleteWorkout(workoutLog: WorkoutLog): Int
    
    // --- Meals ---
    fun getAllMeals(): Flow<List<MealLog>>
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    suspend fun insertMeal(mealLog: MealLog): Long
    suspend fun deleteMeal(mealLog: MealLog): Int

    // Reset
    suspend fun resetData()
}
