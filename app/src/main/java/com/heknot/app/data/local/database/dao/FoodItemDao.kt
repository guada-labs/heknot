package com.heknot.app.data.local.database.dao

import androidx.room.*
import com.heknot.app.data.local.database.entity.FoodCategory
import com.heknot.app.data.local.database.entity.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    
    // Obtener todos los ingredientes
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>
    
    // Obtener favoritos
    @Query("SELECT * FROM food_items WHERE isFavorite = 1 ORDER BY lastUsed DESC")
    fun getFavorites(): Flow<List<FoodItem>>
    
    // Obtener ingredientes en despensa
    @Query("SELECT * FROM food_items WHERE isInPantry = 1 ORDER BY name ASC")
    fun getPantryItems(): Flow<List<FoodItem>>
    
    // Buscar por nombre
    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<FoodItem>>
    
    // Obtener por categoría
    @Query("SELECT * FROM food_items WHERE category = :category ORDER BY name ASC")
    fun getByCategory(category: FoodCategory): Flow<List<FoodItem>>
    
    // Obtener por ID
    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItem?
    
    // Insertar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodItem: FoodItem): Long
    
    // Insertar múltiples
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodItems: List<FoodItem>): List<Long>
    
    // Actualizar
    @Update
    suspend fun update(foodItem: FoodItem): Int
    
    // Eliminar
    @Delete
    suspend fun delete(foodItem: FoodItem): Int
    
    // Marcar como favorito
    @Query("UPDATE food_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean): Int
    
    // Marcar como en despensa
    @Query("UPDATE food_items SET isInPantry = :isInPantry WHERE id = :id")
    suspend fun setInPantry(id: Long, isInPantry: Boolean): Int
    
    // Actualizar última vez usado
    @Query("UPDATE food_items SET lastUsed = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: Long, timestamp: Long): Int
    
    // Obtener más usados
    @Query("SELECT * FROM food_items WHERE lastUsed IS NOT NULL ORDER BY lastUsed DESC LIMIT :limit")
    fun getMostRecentlyUsed(limit: Int = 10): Flow<List<FoodItem>>
}
