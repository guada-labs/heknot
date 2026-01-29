package com.fittrack.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class MealType {
    BREAKFAST,  // Desayuno
    LUNCH,      // Almuerzo
    DINNER,     // Cena
    SNACK,      // Merienda/Snack
    PRE_WORKOUT // Pre-entreno
}

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val date: LocalDate,
    val type: MealType,
    val description: String, // Texto libre: "Arroz con pollo"
    
    val calories: Int? = null, // Para Fase 3 (IA)
    val protein: Int? = null,  // Para futuro
    
    val timestamp: LocalDateTime = LocalDateTime.now()
)
