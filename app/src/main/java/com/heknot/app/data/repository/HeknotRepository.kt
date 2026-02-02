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
    fun getWorkoutsByDate(date: LocalDate): Flow<List<WorkoutLog>>
    
    // --- Meals ---
    fun getAllMeals(): Flow<List<MealLog>>
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    fun getTotalCaloriesConsumedByDate(date: LocalDate): Flow<Int?>
    fun getTotalProteinByDate(date: LocalDate): Flow<Float?>
    fun getTotalCarbsByDate(date: LocalDate): Flow<Float?>
    fun getTotalFatByDate(date: LocalDate): Flow<Float?>
    suspend fun insertMeal(mealLog: MealLog): Long
    suspend fun deleteMeal(mealLog: MealLog): Int

    // --- Water ---
    fun getWaterLogsByDate(date: LocalDate): Flow<List<WaterLog>>
    fun getAllWaterLogs(): Flow<List<WaterLog>>
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

    // --- Food Items ---
    fun getAllFoodItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>>
    fun getFavoriteFoodItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>>
    fun getPantryItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>>
    fun searchFoodItems(query: String): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>>
    suspend fun getFoodItemById(id: Long): com.heknot.app.data.local.database.entity.FoodItem?
    suspend fun insertFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Long
    suspend fun updateFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Int
    suspend fun deleteFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Int
    suspend fun setFoodItemFavorite(id: Long, isFavorite: Boolean): Int
    suspend fun setFoodItemInPantry(id: Long, isInPantry: Boolean): Int

    // --- Recipes ---
    fun getAllRecipes(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>>
    fun getFavoriteRecipes(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>>
    fun searchRecipes(query: String): Flow<List<com.heknot.app.data.local.database.entity.Recipe>>
    fun getRecipesWithAvailableIngredients(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>>
    suspend fun getRecipeById(id: Long): com.heknot.app.data.local.database.entity.Recipe?
    fun getRecipeByIdFlow(id: Long): Flow<com.heknot.app.data.local.database.entity.Recipe?>
    suspend fun insertRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Long
    suspend fun updateRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Int
    suspend fun deleteRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Int
    suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean): Int
    suspend fun incrementRecipeTimesCooked(id: Long): Int

    // --- Recipe Ingredients ---
    fun getRecipeIngredients(recipeId: Long): Flow<List<com.heknot.app.data.local.database.entity.RecipeIngredient>>
    suspend fun insertRecipeIngredient(recipeIngredient: com.heknot.app.data.local.database.entity.RecipeIngredient): Long
    suspend fun insertRecipeIngredients(recipeIngredients: List<com.heknot.app.data.local.database.entity.RecipeIngredient>): List<Long>
    suspend fun deleteRecipeIngredient(recipeIngredient: com.heknot.app.data.local.database.entity.RecipeIngredient): Int

    // --- Enhanced Meal Logging ---
    fun getPlannedMeals(): Flow<List<MealLog>>
    fun getPlannedMealsByDate(date: LocalDate): Flow<List<MealLog>>
    fun getProjectedCaloriesByDate(date: LocalDate): Flow<Int?>
    suspend fun updateMeal(mealLog: MealLog): Int
    suspend fun markMealAsConsumed(id: Long): Int

    // Reset
    suspend fun resetData()
}
