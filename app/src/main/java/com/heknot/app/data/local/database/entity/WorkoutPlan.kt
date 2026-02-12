package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un plan de entrenamiento completo (ej. "Full Body 4 Semanas").
 */
@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val difficulty: String,     // Ej: "PRINCIPIANTE", "INTERMEDIO", "AVANZADO"
    val goal: String,           // Ej: "PERDIDA_PESO", "GANAR_MUSCULO"
    val durationWeeks: Int,
    val suggestedEquipment: String // Lista simple separada por comas de equipmentIds requeridos
)
