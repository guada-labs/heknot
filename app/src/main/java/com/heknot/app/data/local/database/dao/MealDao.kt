package com.heknot.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealDao {
    
    // Obtener todas las comidas
    @Query("SELECT * FROM meal_logs WHERE isPast = 1 ORDER BY dateTime DESC")
    fun getAllMeals(): Flow<List<MealLog>>
    
    // Obtener comidas de un día específico
    @Query("SELECT * FROM meal_logs WHERE date(dateTime) = :date AND isPast = 1 ORDER BY dateTime ASC")
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    
    // Obtener comidas planificadas (futuras)
    @Query("SELECT * FROM meal_logs WHERE isPlanned = 1 AND isPast = 0 ORDER BY dateTime ASC")
    fun getPlannedMeals(): Flow<List<MealLog>>
    
    // Obtener comidas planificadas para un día
    @Query("SELECT * FROM meal_logs WHERE date(dateTime) = :date AND isPlanned = 1 ORDER BY dateTime ASC")
    fun getPlannedMealsByDate(date: LocalDate): Flow<List<MealLog>>
    
    // Obtener por tipo de comida
    @Query("SELECT * FROM meal_logs WHERE type = :mealType AND isPast = 1 ORDER BY dateTime DESC LIMIT :limit")
    fun getMealsByType(mealType: MealType, limit: Int = 20): Flow<List<MealLog>>
    
    // Totales nutricionales por día
    @Query("SELECT SUM(calories) FROM meal_logs WHERE date(dateTime) = :date AND isPast = 1")
    fun getTotalCaloriesByDate(date: LocalDate): Flow<Int?>
    
    @Query("SELECT SUM(protein) FROM meal_logs WHERE date(dateTime) = :date AND isPast = 1")
    fun getTotalProteinByDate(date: LocalDate): Flow<Float?>
    
    @Query("SELECT SUM(carbs) FROM meal_logs WHERE date(dateTime) = :date AND isPast = 1")
    fun getTotalCarbsByDate(date: LocalDate): Flow<Float?>
    
    @Query("SELECT SUM(fat) FROM meal_logs WHERE date(dateTime) = :date AND isPast = 1")
    fun getTotalFatByDate(date: LocalDate): Flow<Float?>
    
    // Totales nutricionales planificados (para simulación "¿Qué pasaría si...?")
    @Query("""
        SELECT SUM(calories) FROM meal_logs 
        WHERE date(dateTime) = :date 
        AND (isPast = 1 OR isPlanned = 1)
    """)
    fun getProjectedCaloriesByDate(date: LocalDate): Flow<Int?>
    
    // Obtener comidas detectadas por IA
    @Query("SELECT * FROM meal_logs WHERE detectedByAI = 1 ORDER BY dateTime DESC LIMIT :limit")
    fun getAIDetectedMeals(limit: Int = 20): Flow<List<MealLog>>
    
    // Obtener comidas con retroalimentación
    @Query("SELECT * FROM meal_logs WHERE userFeedback IS NOT NULL ORDER BY dateTime DESC LIMIT :limit")
    fun getMealsWithFeedback(limit: Int = 20): Flow<List<MealLog>>
    
    // Insertar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealLog: MealLog): Long
    
    // Actualizar
    @Update
    suspend fun update(mealLog: MealLog): Int
    
    // Eliminar
    @Delete
    suspend fun delete(mealLog: MealLog): Int
    
    // Marcar comida planificada como consumida
    @Query("UPDATE meal_logs SET isPast = 1, isPlanned = 0 WHERE id = :id")
    suspend fun markAsConsumed(id: Long): Int
    
    // Obtener estadísticas de satisfacción promedio
    @Query("SELECT AVG(satisfactionLevel) FROM meal_logs WHERE satisfactionLevel IS NOT NULL")
    fun getAverageSatisfaction(): Flow<Float?>
}
