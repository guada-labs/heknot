package com.heknot.app.data.local.database.entity

/**
 * Tipos de comidas del día
 */
enum class MealType(val displayName: String) {
    BREAKFAST("Desayuno"),
    LUNCH("Almuerzo"),
    DINNER("Cena"),
    SNACK("Merienda/Snack")
}
