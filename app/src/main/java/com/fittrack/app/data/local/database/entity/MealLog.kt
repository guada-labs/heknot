package com.fittrack.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val type: MealType,
    val description: String,
    
    val calories: Int? = null,
    val protein: Int? = null
)
