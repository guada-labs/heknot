package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Mapea un ejercicio específico a una rutina con parámetros definidos.
 * Incluye lógica para sustitución dinámica si falta equipamiento.
 */
@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutRoutine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("routineId")]
)
data class RoutineExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineId: Long,
    val exerciseId: String,           // ID del ejercicio base (Asset)
    val alternativeExerciseId: String? = null, // ID para usar si no hay equipo disponible
    
    val sets: Int,
    val reps: Int? = null,            // Nulo si es por tiempo
    val durationSeconds: Int? = null, // Nulo si es por reps
    val restSeconds: Int = 60,
    val orderInRoutine: Int           // Para mantener la secuencia
)
