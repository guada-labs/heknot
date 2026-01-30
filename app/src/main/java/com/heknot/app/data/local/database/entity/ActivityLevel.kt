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
        "Sedentary",
        1.2f,
        "Little or no exercise, desk job"
    ),
    LIGHT(
        "Light",
        1.375f,
        "Light exercise 1-3 days/week"
    ),
    MODERATE(
        "Moderate",
        1.55f,
        "Moderate exercise 3-5 days/week"
    ),
    ACTIVE(
        "Active",
        1.725f,
        "Intense exercise 6-7 days/week"
    ),
    VERY_ACTIVE(
        "Very Active",
        1.9f,
        "Very intense exercise, physical job or training 2 times per day"
    )
}
