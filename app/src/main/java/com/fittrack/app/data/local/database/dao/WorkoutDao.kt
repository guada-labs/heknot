package com.fittrack.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.app.data.local.database.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkoutDao {
    
    @Query("SELECT * FROM workout_logs ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>
    
    // Obtener workouts de un rango de fechas (para gráficas/rachas)
    @Query("SELECT * FROM workout_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getWorkoutsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutLog>>
    
    // Verificar si hizo ejercicio hoy
    @Query("SELECT COUNT(*) FROM workout_logs WHERE date = :date")
    suspend fun getWorkoutCountByDate(date: LocalDate): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutLog: WorkoutLog): Long
    
    @Update
    suspend fun update(workoutLog: WorkoutLog): Int
    
    @Delete
    suspend fun delete(workoutLog: WorkoutLog): Int
}
