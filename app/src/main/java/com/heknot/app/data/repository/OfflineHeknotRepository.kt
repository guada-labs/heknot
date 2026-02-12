package com.heknot.app.data.repository

import com.heknot.app.data.local.database.HeknotDatabase
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

    override suspend fun updateBiometricEnabled(enabled: Boolean): Int =
        database.userProfileDao().updateBiometricEnabled(enabled)

    override suspend fun updateMeasurements(
        neck: Float?, 
        waist: Float?, 
        hip: Float?,
        chest: Float?,
        arm: Float?,
        thigh: Float?,
        calf: Float?
    ): Int = database.userProfileDao().updateMeasurements(neck, waist, hip, chest, arm, thigh, calf)

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

    override fun getWorkoutsByDate(date: LocalDate): Flow<List<WorkoutLog>> =
        database.workoutDao().getWorkoutsByDate(date)

    // --- Meals ---
    override fun getAllMeals(): Flow<List<MealLog>> =
        database.mealDao().getAllMeals()

    override fun getMealsByDate(date: LocalDate): Flow<List<MealLog>> =
        database.mealDao().getMealsByDate(date)

    override fun getTotalCaloriesConsumedByDate(date: LocalDate): Flow<Int?> =
        database.mealDao().getTotalCaloriesByDate(date)

    override fun getTotalProteinByDate(date: LocalDate): Flow<Float?> =
        database.mealDao().getTotalProteinByDate(date)

    override fun getTotalCarbsByDate(date: LocalDate): Flow<Float?> =
        database.mealDao().getTotalCarbsByDate(date)

    override fun getTotalFatByDate(date: LocalDate): Flow<Float?> =
        database.mealDao().getTotalFatByDate(date)
        
    override suspend fun insertMeal(mealLog: MealLog): Long =
        database.mealDao().insert(mealLog)
        
    override suspend fun deleteMeal(mealLog: MealLog): Int =
        database.mealDao().delete(mealLog)

    // --- Water ---
    override fun getWaterLogsByDate(date: LocalDate): Flow<List<WaterLog>> =
        database.waterDao().getWaterLogsByDate(date)

    override fun getAllWaterLogs(): Flow<List<WaterLog>> =
        database.waterDao().getAllWaterLogs()

    override fun getTotalWaterByDate(date: LocalDate): Flow<Int?> =
        database.waterDao().getTotalWaterByDate(date)

    override suspend fun insertWaterLog(waterLog: WaterLog): Long =
        database.waterDao().insertWaterLog(waterLog)

    override suspend fun deleteWaterLog(waterLog: WaterLog): Int =
        database.waterDao().deleteWaterLog(waterLog)

    // --- Sleep ---
    override fun getSleepLogsByDate(date: LocalDate): Flow<List<SleepLog>> =
        database.sleepDao().getSleepLogsByDate(date)

    override fun getAllSleepLogs(): Flow<List<SleepLog>> =
        database.sleepDao().getAllSleepLogs()

    override suspend fun insertSleepLog(sleepLog: SleepLog): Long =
        database.sleepDao().insertSleepLog(sleepLog)

    override suspend fun deleteSleepLog(sleepLog: SleepLog): Int =
        database.sleepDao().deleteSleepLog(sleepLog)

    // --- Guided Exercises ---
    override fun getAllGuidedExercises(): Flow<List<GuidedExercise>> =
        database.guidedExerciseDao().getAllExercises()

    override fun getGuidedExercisesByCategory(category: WorkoutCategory): Flow<List<GuidedExercise>> =
        database.guidedExerciseDao().getExercisesByCategory(category)

    override suspend fun insertGuidedExercise(exercise: GuidedExercise): Long =
        database.guidedExerciseDao().insertExercise(exercise)

    // --- Food Items ---
    override fun getAllFoodItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>> =
        database.foodItemDao().getAllFoodItems()

    override fun getFavoriteFoodItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>> =
        database.foodItemDao().getFavorites()

    override fun getPantryItems(): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>> =
        database.foodItemDao().getPantryItems()

    override fun searchFoodItems(query: String): Flow<List<com.heknot.app.data.local.database.entity.FoodItem>> =
        database.foodItemDao().searchByName(query)

    override suspend fun getFoodItemById(id: Long): com.heknot.app.data.local.database.entity.FoodItem? =
        database.foodItemDao().getById(id)

    override suspend fun insertFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Long =
        database.foodItemDao().insert(foodItem)

    override suspend fun updateFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Int =
        database.foodItemDao().update(foodItem)

    override suspend fun deleteFoodItem(foodItem: com.heknot.app.data.local.database.entity.FoodItem): Int =
        database.foodItemDao().delete(foodItem)

    override suspend fun setFoodItemFavorite(id: Long, isFavorite: Boolean): Int =
        database.foodItemDao().setFavorite(id, isFavorite)

    override suspend fun setFoodItemInPantry(id: Long, isInPantry: Boolean): Int =
        database.foodItemDao().setInPantry(id, isInPantry)

    // --- Recipes ---
    override fun getAllRecipes(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>> =
        database.recipeDao().getAllRecipes()

    override fun getFavoriteRecipes(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>> =
        database.recipeDao().getFavorites()

    override fun searchRecipes(query: String): Flow<List<com.heknot.app.data.local.database.entity.Recipe>> =
        database.recipeDao().searchByName(query)

    override fun getRecipesWithAvailableIngredients(): Flow<List<com.heknot.app.data.local.database.entity.Recipe>> =
        database.recipeDao().getRecipesWithAvailableIngredients()

    override suspend fun getRecipeById(id: Long): com.heknot.app.data.local.database.entity.Recipe? =
        database.recipeDao().getById(id)

    override fun getRecipeByIdFlow(id: Long): Flow<com.heknot.app.data.local.database.entity.Recipe?> =
        database.recipeDao().getByIdFlow(id)

    override suspend fun insertRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Long =
        database.recipeDao().insert(recipe)

    override suspend fun updateRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Int =
        database.recipeDao().update(recipe)

    override suspend fun deleteRecipe(recipe: com.heknot.app.data.local.database.entity.Recipe): Int =
        database.recipeDao().delete(recipe)

    override suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean): Int =
        database.recipeDao().setFavorite(id, isFavorite)

    override suspend fun incrementRecipeTimesCooked(id: Long): Int =
        database.recipeDao().incrementTimesCooked(id, System.currentTimeMillis())

    // --- Recipe Ingredients ---
    override fun getRecipeIngredients(recipeId: Long): Flow<List<com.heknot.app.data.local.database.entity.RecipeIngredient>> =
        database.recipeDao().getRecipeIngredients(recipeId)

    override suspend fun insertRecipeIngredient(recipeIngredient: com.heknot.app.data.local.database.entity.RecipeIngredient): Long =
        database.recipeDao().insertRecipeIngredient(recipeIngredient)

    override suspend fun insertRecipeIngredients(recipeIngredients: List<com.heknot.app.data.local.database.entity.RecipeIngredient>): List<Long> =
        database.recipeDao().insertRecipeIngredients(recipeIngredients)

    override suspend fun deleteRecipeIngredient(recipeIngredient: com.heknot.app.data.local.database.entity.RecipeIngredient): Int =
        database.recipeDao().deleteRecipeIngredient(recipeIngredient)

    // --- Enhanced Meal Logging ---
    override fun getPlannedMeals(): Flow<List<MealLog>> =
        database.mealDao().getPlannedMeals()

    override fun getPlannedMealsByDate(date: LocalDate): Flow<List<MealLog>> =
        database.mealDao().getPlannedMealsByDate(date)

    override fun getProjectedCaloriesByDate(date: LocalDate): Flow<Int?> =
        database.mealDao().getProjectedCaloriesByDate(date)

    override suspend fun updateMeal(mealLog: MealLog): Int =
        database.mealDao().update(mealLog)

    override suspend fun markMealAsConsumed(id: Long): Int =
        database.mealDao().markAsConsumed(id)

    override suspend fun resetData() {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
