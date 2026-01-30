package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Relación many-to-many entre recetas e ingredientes
 * Ejemplo: "Avena matutina" contiene "200ml de leche", "40g de avena", etc.
 */
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["foodItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("recipeId"),
        Index("foodItemId")
    ]
)
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val recipeId: Long,
    val foodItemId: Long,
    
    // Cantidad del ingrediente en esta receta
    val amount: Float, // Ej: 200 (ml), 40 (g), 1 (unidad)
    val unit: ServingUnit = ServingUnit.GRAMS,
    
    // Opcional: notas sobre este ingrediente
    val notes: String? = null, // Ej: "Opcional", "Al gusto"
    val isOptional: Boolean = false
)
