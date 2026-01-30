package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Registro de una comida consumida
 * Puede ser:
 * - Un FoodItem individual (galleta, vaso de yogurt)
 * - Una Recipe completa (avena matutina)
 * - Una comida detectada por IA (foto + descripción)
 */
@Entity(
    tableName = "meal_logs",
    foreignKeys = [
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["foodItemId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("foodItemId"),
        Index("recipeId")
    ]
)
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Fecha y hora
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val type: MealType,
    
    // Referencia a FoodItem o Recipe (solo uno debe estar presente)
    val foodItemId: Long? = null,
    val recipeId: Long? = null,
    
    // Cantidad consumida (multiplicador de la porción base)
    val servings: Float = 1.0f, // Ej: 1.5 porciones
    
    // Descripción manual (para comidas sin FoodItem/Recipe)
    val description: String? = null,
    
    // Valores nutricionales (calculados o manuales)
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    
    // Imagen de la comida (opcional)
    val imageUrl: String? = null,
    
    // Retroalimentación del usuario
    val userFeedback: String? = null, // "Me sentí lleno", "Quedé con hambre"
    val satisfactionLevel: Int? = null, // 1-5 estrellas
    
    // Metadata de IA (si fue detectada por foto)
    val detectedByAI: Boolean = false,
    val aiConfidence: Float? = null, // 0.0 - 1.0
    
    // Planificación
    val isPlanned: Boolean = false, // true si es una comida futura planificada
    val isPast: Boolean = true // false para comidas futuras
)
