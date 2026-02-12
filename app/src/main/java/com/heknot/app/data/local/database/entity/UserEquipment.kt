package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una pieza de equipamiento que el usuario puede tener disponible.
 * Esto permite filtrar planes y sugerir alternativas dinámicamente.
 */
@Entity(tableName = "user_equipment")
data class UserEquipment(
    @PrimaryKey
    val equipmentId: String, // Ej: "dumbbells", "stationary_bike", "resistance_bands"
    val name: String,        // Nombre para mostrar en español
    val isAvailable: Boolean = false
)
