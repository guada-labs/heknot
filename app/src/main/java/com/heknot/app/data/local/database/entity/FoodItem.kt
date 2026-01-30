package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un ingrediente o producto alimenticio individual
 * Ejemplo: Leche descremada, Avena Quaker, Galleta Tosh
 */
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Información básica
    val name: String,
    val brand: String? = null, // Ej: "Quaker", "Alpina"
    val category: FoodCategory = FoodCategory.OTHER,
    
    // Información nutricional (por cada servingSize)
    val servingSize: Float, // Cantidad en gramos o ml
    val servingUnit: ServingUnit = ServingUnit.GRAMS,
    
    val calories: Int,
    val protein: Float, // gramos
    val carbs: Float,   // gramos
    val fat: Float,     // gramos
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f, // mg
    
    // Imagen (opcional)
    val imageUrl: String? = null, // URL local o remota
    val pixelArtGenerated: Boolean = false,
    
    // Disponibilidad
    val isInPantry: Boolean = false, // ¿Lo tengo en casa?
    val isFavorite: Boolean = false,
    
    // Metadata
    val isCustom: Boolean = true, // true si lo creó el usuario
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long? = null
)

enum class FoodCategory {
    DAIRY,          // Lácteos
    GRAINS,         // Granos/Cereales
    FRUITS,         // Frutas
    VEGETABLES,     // Verduras
    PROTEINS,       // Proteínas (carne, huevo, etc)
    SNACKS,         // Snacks/Galletas
    BEVERAGES,      // Bebidas
    CONDIMENTS,     // Condimentos/Especias
    OTHER
}

enum class ServingUnit {
    GRAMS,          // g
    MILLILITERS,    // ml
    PIECES,         // unidades (ej: 1 banano)
    TABLESPOONS,    // cucharadas
    TEASPOONS,      // cucharaditas
    CUPS            // tazas
}
