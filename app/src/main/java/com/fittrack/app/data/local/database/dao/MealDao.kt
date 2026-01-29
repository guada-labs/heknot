package com.fittrack.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.app.data.local.database.entity.MealLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealDao {
    
    @Query("SELECT * FROM meal_logs ORDER BY date DESC, type ASC")
    fun getAllMeals(): Flow<List<MealLog>>
    
    // Obtener comidas de hoy
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY type ASC")
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealLog: MealLog): Long
    
    @Update
    suspend fun update(mealLog: MealLog): Int
    
    @Delete
    suspend fun delete(mealLog: MealLog): Int
}
