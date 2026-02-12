package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa una sesión o rutina individual dentro de un plan de entrenamiento.
 */
@Entity(
    tableName = "workout_routines",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId")]
)
data class WorkoutRoutine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: Long,
    val dayNumber: Int,         // Día dentro del plan (ej: Día 1, Día 3)
    val title: String,          // Ej: "Pecho y Espalda"
    val description: String? = null,
    val isCardioBlock: Boolean = false // Si es un bloque puramente metabólico/cardio
)
