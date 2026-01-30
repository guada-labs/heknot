package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una receta completa con múltiples ingredientes
 * Ejemplo: "Avena matutina", "Agua de panela"
 */
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Información básica
    val name: String,
    val description: String? = null,
    
    // Tiempo de preparación
    val prepTimeMinutes: Int = 0,
    val cookTimeMinutes: Int = 0,
    
    // Porciones
    val servings: Int = 1, // Número de porciones que produce
    
    // Instrucciones (JSON string - se parsea en la app)
    val instructions: String = "[]", // JSON array de RecipeStep
    
    // Imagen
    val imageUrl: String? = null,
    val pixelArtGenerated: Boolean = false,
    
    // Categoría y etiquetas
    val mealType: MealType = MealType.SNACK,
    val difficulty: RecipeDifficulty = RecipeDifficulty.EASY,
    
    // Favoritos y uso
    val isFavorite: Boolean = false,
    val timesCooked: Int = 0,
    val lastCooked: Long? = null,
    
    // Metadata
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Paso individual de una receta con timer opcional
 */
data class RecipeStep(
    val stepNumber: Int,
    val instruction: String,
    val timerSeconds: Int? = null, // Timer opcional (ej: "Hervir 5 min" = 300)
    val imageUrl: String? = null
)

enum class RecipeDifficulty {
    EASY,       // Fácil
    MEDIUM,     // Medio
    HARD        // Difícil
}
