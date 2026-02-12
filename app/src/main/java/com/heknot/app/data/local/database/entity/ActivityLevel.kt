package com.heknot.app.data.local.database.entity

/**
 * Daily physical activity level (excluding specific exercise).
 * Used for TDEE (Total Daily Energy Expenditure) calculation.
 */
enum class ActivityLevel(
    val displayName: String,
    val multiplier: Float,
    val description: String
) {
    SEDENTARY(
        "Sedentario",
        1.2f,
        "Poco o nada de ejercicio, trabajo de escritorio"
    ),
    LIGHT(
        "Ligero",
        1.375f,
        "Ejercicio ligero 1-3 días a la semana"
    ),
    MODERATE(
        "Moderado",
        1.55f,
        "Ejercicio moderado 3-5 días a la semana"
    ),
    ACTIVE(
        "Activo",
        1.725f,
        "Ejercicio intenso 6-7 días a la semana"
    ),
    VERY_ACTIVE(
        "Muy Activo",
        1.9f,
        "Ejercicio muy intenso, trabajo físico o entrenamiento 2 veces al día"
    )
}
