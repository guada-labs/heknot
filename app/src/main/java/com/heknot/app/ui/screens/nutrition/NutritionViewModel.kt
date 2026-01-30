package com.heknot.app.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.FoodItem
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.MealType
import com.heknot.app.data.local.database.entity.Recipe
import com.heknot.app.data.local.database.entity.RecipeIngredient
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * UI State para pantallas de nutrición
 */
data class NutritionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

/**
 * Estado para formulario de FoodItem
 */
data class FoodItemFormState(
    val foodItem: FoodItem? = null,
    val isEditing: Boolean = false,
    val isValid: Boolean = false
)

/**
 * Estado para formulario de Recipe
 */
data class RecipeFormState(
    val recipe: Recipe? = null,
    val ingredients: List<RecipeIngredient> = emptyList(),
    val isEditing: Boolean = false,
    val isValid: Boolean = false
)

/**
 * Resumen nutricional diario
 */
data class DailyNutritionSummary(
    val date: LocalDate = LocalDate.now(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val mealCount: Int = 0
)

/**
 * ViewModel para el módulo de nutrición
 * Maneja FoodItems, Recipes, y MealLogs mejorados
 */
class NutritionViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    // --- UI State ---
    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    // --- Food Items ---
    val allFoodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteFoodItems: StateFlow<List<FoodItem>> = repository.getFavoriteFoodItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pantryItems: StateFlow<List<FoodItem>> = repository.getPantryItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Recipes ---
    val allRecipes: StateFlow<List<Recipe>> = repository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteRecipes: StateFlow<List<Recipe>> = repository.getFavoriteRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recipesWithAvailableIngredients: StateFlow<List<Recipe>> = 
        repository.getRecipesWithAvailableIngredients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // --- Meal Logs ---
    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsByDate(LocalDate.now())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val plannedMeals: StateFlow<List<MealLog>> = repository.getPlannedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Daily Summary ---
    // --- Daily Summary ---
    val dailyNutritionSummary: StateFlow<DailyNutritionSummary> = combine(
        repository.getTotalCaloriesConsumedByDate(LocalDate.now()),
        repository.getTotalProteinByDate(LocalDate.now()),
        repository.getTotalCarbsByDate(LocalDate.now()),
        repository.getTotalFatByDate(LocalDate.now()),
        todayMeals
    ) { calories, protein, carbs, fat, meals ->
        DailyNutritionSummary(
            date = LocalDate.now(),
            totalCalories = calories ?: 0,
            totalProtein = protein ?: 0f,
            totalCarbs = carbs ?: 0f,
            totalFat = fat ?: 0f,
            mealCount = meals.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyNutritionSummary()
    )

    // --- FoodItem Operations ---

    fun insertFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertFoodItem(foodItem)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        successMessage = "Ingrediente guardado",
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Error al guardar ingrediente"
                    ) 
                }
            }
        }
    }

    fun updateFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateFoodItem(foodItem)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        successMessage = "Ingrediente actualizado",
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Error al actualizar ingrediente"
                    ) 
                }
            }
        }
    }

    fun deleteFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            try {
                repository.deleteFoodItem(foodItem)
                _uiState.update { 
                    it.copy(successMessage = "Ingrediente eliminado") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al eliminar ingrediente") 
                }
            }
        }
    }

    fun toggleFoodItemFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFoodItemFavorite(id, isFavorite)
        }
    }

    fun toggleFoodItemInPantry(id: Long, isInPantry: Boolean) {
        viewModelScope.launch {
            repository.setFoodItemInPantry(id, isInPantry)
        }
    }

    fun searchFoodItems(query: String): StateFlow<List<FoodItem>> {
        return repository.searchFoodItems(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // --- Recipe Operations ---

    fun insertRecipe(recipe: Recipe, ingredients: List<RecipeIngredient>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val recipeId = repository.insertRecipe(recipe)
                
                // Insertar ingredientes con el ID de la receta
                val ingredientsWithRecipeId = ingredients.map { 
                    it.copy(recipeId = recipeId) 
                }
                repository.insertRecipeIngredients(ingredientsWithRecipeId)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        successMessage = "Receta guardada",
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Error al guardar receta"
                    ) 
                }
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateRecipe(recipe)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        successMessage = "Receta actualizada",
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Error al actualizar receta"
                    ) 
                }
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
                _uiState.update { 
                    it.copy(successMessage = "Receta eliminada") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al eliminar receta") 
                }
            }
        }
    }

    fun toggleRecipeFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setRecipeFavorite(id, isFavorite)
        }
    }

    fun incrementRecipeTimesCooked(id: Long) {
        viewModelScope.launch {
            repository.incrementRecipeTimesCooked(id)
        }
    }

    fun searchRecipes(query: String): StateFlow<List<Recipe>> {
        return repository.searchRecipes(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun getRecipeIngredients(recipeId: Long): StateFlow<List<RecipeIngredient>> {
        return repository.getRecipeIngredients(recipeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // --- Meal Logging ---

    fun logMealFromFoodItem(
        foodItem: FoodItem,
        servings: Float,
        mealType: MealType,
        dateTime: LocalDateTime = LocalDateTime.now()
    ) {
        viewModelScope.launch {
            try {
                val mealLog = MealLog(
                    dateTime = dateTime,
                    type = mealType,
                    foodItemId = foodItem.id,
                    servings = servings,
                    calories = (foodItem.calories * servings).toInt(),
                    protein = foodItem.protein * servings,
                    carbs = foodItem.carbs * servings,
                    fat = foodItem.fat * servings,
                    isPast = dateTime <= LocalDateTime.now(),
                    isPlanned = dateTime > LocalDateTime.now()
                )
                repository.insertMeal(mealLog)
                
                // Actualizar lastUsed del FoodItem
                repository.updateFoodItem(
                    foodItem.copy(lastUsed = System.currentTimeMillis())
                )
                
                _uiState.update { 
                    it.copy(successMessage = "Comida registrada") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al registrar comida") 
                }
            }
        }
    }

    fun logMealFromRecipe(
        recipe: Recipe,
        servings: Float,
        mealType: MealType,
        dateTime: LocalDateTime = LocalDateTime.now()
    ) {
        viewModelScope.launch {
            try {
                // Calcular valores nutricionales de la receta
                val ingredients = repository.getRecipeIngredients(recipe.id)
                    .stateIn(viewModelScope).value
                
                var totalCalories = 0
                var totalProtein = 0f
                var totalCarbs = 0f
                var totalFat = 0f
                
                for (ingredient in ingredients) {
                    val foodItem = repository.getFoodItemById(ingredient.foodItemId)
                    if (foodItem != null) {
                        val multiplier = ingredient.amount / foodItem.servingSize
                        totalCalories += (foodItem.calories * multiplier).toInt()
                        totalProtein += foodItem.protein * multiplier
                        totalCarbs += foodItem.carbs * multiplier
                        totalFat += foodItem.fat * multiplier
                    }
                }
                
                // Ajustar por porciones de la receta
                val perServing = recipe.servings.toFloat()
                val mealLog = MealLog(
                    dateTime = dateTime,
                    type = mealType,
                    recipeId = recipe.id,
                    servings = servings,
                    calories = ((totalCalories / perServing) * servings).toInt(),
                    protein = (totalProtein / perServing) * servings,
                    carbs = (totalCarbs / perServing) * servings,
                    fat = (totalFat / perServing) * servings,
                    isPast = dateTime <= LocalDateTime.now(),
                    isPlanned = dateTime > LocalDateTime.now()
                )
                repository.insertMeal(mealLog)
                
                // Incrementar contador de veces cocinada
                repository.incrementRecipeTimesCooked(recipe.id)
                
                _uiState.update { 
                    it.copy(successMessage = "Comida registrada") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al registrar comida") 
                }
            }
        }
    }

    fun updateMeal(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                repository.updateMeal(mealLog)
                _uiState.update { 
                    it.copy(successMessage = "Comida actualizada") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al actualizar comida") 
                }
            }
        }
    }

    fun deleteMeal(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                repository.deleteMeal(mealLog)
                _uiState.update { 
                    it.copy(successMessage = "Comida eliminada") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al eliminar comida") 
                }
            }
        }
    }

    fun markPlannedMealAsConsumed(mealId: Long) {
        viewModelScope.launch {
            try {
                repository.markMealAsConsumed(mealId)
                _uiState.update { 
                    it.copy(successMessage = "Comida marcada como consumida") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Error al marcar comida") 
                }
            }
        }
    }

    // --- Utility ---

    fun clearMessages() {
        _uiState.update { 
            it.copy(successMessage = null, error = null) 
        }
    }
}
