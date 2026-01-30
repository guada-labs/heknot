package com.heknot.app.data.local.database.dao

import androidx.room.*
import com.heknot.app.data.local.database.entity.MealType
import com.heknot.app.data.local.database.entity.Recipe
import com.heknot.app.data.local.database.entity.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    
    // Obtener todas las recetas
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    // Obtener favoritas
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY lastCooked DESC")
    fun getFavorites(): Flow<List<Recipe>>
    
    // Obtener por tipo de comida
    @Query("SELECT * FROM recipes WHERE mealType = :mealType ORDER BY name ASC")
    fun getByMealType(mealType: MealType): Flow<List<Recipe>>
    
    // Buscar por nombre
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<Recipe>>
    
    // Obtener por ID
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: Long): Recipe?
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<Recipe?>
    
    // Obtener más cocinadas
    @Query("SELECT * FROM recipes WHERE timesCooked > 0 ORDER BY timesCooked DESC LIMIT :limit")
    fun getMostCooked(limit: Int = 10): Flow<List<Recipe>>
    
    // Insertar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long
    
    // Actualizar
    @Update
    suspend fun update(recipe: Recipe): Int
    
    // Eliminar
    @Delete
    suspend fun delete(recipe: Recipe): Int
    
    // Marcar como favorito
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean): Int
    
    // Incrementar contador de veces cocinada
    @Query("UPDATE recipes SET timesCooked = timesCooked + 1, lastCooked = :timestamp WHERE id = :id")
    suspend fun incrementTimesCooked(id: Long, timestamp: Long): Int
    
    // --- RecipeIngredient queries ---
    
    // Obtener ingredientes de una receta
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredient>>
    
    // Insertar ingrediente de receta
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient): Long
    
    // Insertar múltiples ingredientes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>): List<Long>
    
    // Eliminar ingrediente de receta
    @Delete
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient): Int
    
    // Eliminar todos los ingredientes de una receta
    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteAllRecipeIngredients(recipeId: Long): Int
    
    // Buscar recetas que contengan un ingrediente específico
    @Query("""
        SELECT DISTINCT r.* FROM recipes r
        INNER JOIN recipe_ingredients ri ON r.id = ri.recipeId
        WHERE ri.foodItemId = :foodItemId
        ORDER BY r.name ASC
    """)
    fun getRecipesWithIngredient(foodItemId: Long): Flow<List<Recipe>>
    
    // Buscar recetas que se puedan hacer con ingredientes disponibles
    @Query("""
        SELECT r.* FROM recipes r
        WHERE NOT EXISTS (
            SELECT 1 FROM recipe_ingredients ri
            LEFT JOIN food_items fi ON ri.foodItemId = fi.id
            WHERE ri.recipeId = r.id 
            AND ri.isOptional = 0
            AND (fi.isInPantry = 0 OR fi.isInPantry IS NULL)
        )
        ORDER BY r.name ASC
    """)
    fun getRecipesWithAvailableIngredients(): Flow<List<Recipe>>
}
