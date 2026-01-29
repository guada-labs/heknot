package com.fittrack.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class WorkoutType {
    WALK,       // Caminata
    BIKE,       // Bicicleta
    HOME,       // Ejercicio en casa (rutina)
    MIXED,      // Combinado
    OTHER       // Otro
}

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val type: WorkoutType,
    val completed: Boolean = true,
    
    val durationMinutes: Int? = null,
    val caloriesBurned: Int? = null
)
