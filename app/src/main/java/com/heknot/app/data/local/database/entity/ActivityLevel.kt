package com.heknot.app.data.local.database.entity

/**
 * Nivel de actividad física diaria (sin contar ejercicio específico).
 * Se usa para calcular el TDEE (Total Daily Energy Expenditure).
 */
enum class ActivityLevel(
    val displayName: String,
    val multiplier: Float,
    val description: String
) {
    SEDENTARY(
        "Sedentario",
        1.2f,
        "Poco o ningún ejercicio, trabajo de oficina"
    ),
    LIGHT(
        "Ligero",
        1.375f,
        "Ejercicio ligero 1-3 días/semana"
    ),
    MODERATE(
        "Moderado",
        1.55f,
        "Ejercicio moderado 3-5 días/semana"
    ),
    ACTIVE(
        "Activo",
        1.725f,
        "Ejercicio intenso 6-7 días/semana"
    ),
    VERY_ACTIVE(
        "Muy Activo",
        1.9f,
        "Ejercicio muy intenso, trabajo físico o entrenamiento 2 veces al día"
    )
}
