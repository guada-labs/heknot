package com.heknot.app.data.local.database

import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WorkoutType
import kotlin.math.roundToInt

/**
 * Calculadora científica de calorías basada en:
 * - TMB (Tasa Metabólica Basal) usando Mifflin-St Jeor
 * - TDEE (Total Daily Energy Expenditure)
 * - MET (Metabolic Equivalent of Task) ajustado por intensidad
 */
object CalorieCalculator {

    /**
     * Calcula la TMB (Tasa Metabólica Basal) usando la fórmula de Mifflin-St Jeor.
     * Es más precisa que Harris-Benedict para poblaciones modernas.
     * 
     * Hombres: TMB = (10 × peso en kg) + (6.25 × altura en cm) - (5 × edad en años) + 5
     * Mujeres: TMB = (10 × peso en kg) + (6.25 × altura en cm) - (5 × edad en años) - 161
     */
    fun calculateBMR(
        weightKg: Float,
        heightCm: Int?,
        age: Int?,
        gender: Gender?
    ): Int {
        if (heightCm == null || age == null || gender == null) {
            // Fallback: usar estimación simple basada solo en peso
            return (weightKg * 24).roundToInt()
        }

        val bmr = when (gender) {
            Gender.MALE -> (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
            Gender.FEMALE -> (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
            Gender.OTHER -> (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 78 // Promedio
        }

        return bmr.roundToInt()
    }

    /**
     * Calcula el TDEE (Total Daily Energy Expenditure).
     * Es la TMB multiplicada por el nivel de actividad diaria.
     */
    fun calculateTDEE(profile: UserProfile): Int {
        val bmr = calculateBMR(
            weightKg = profile.currentWeight,
            heightCm = profile.heightCm,
            age = profile.age,
            gender = profile.gender
        )
        return (bmr * profile.activityLevel.multiplier).roundToInt()
    }

    /**
     * Calcula las calorías quemadas durante una actividad específica.
     * 
     * Fórmula: Calorías = MET × Peso(kg) × Duración(horas)
     * 
     * El MET se ajusta según el rating de esfuerzo reportado (1-5).
     */
    fun calculateActivityCalories(
        type: WorkoutType,
        durationMinutes: Int,
        weightKg: Float,
        effortRating: Int? = null
    ): Int {
        val effectiveMet = type.getEffectiveMet(effortRating)
        val durationHours = durationMinutes / 60f
        val calories = effectiveMet * weightKg * durationHours
        return calories.roundToInt()
    }

    /**
     * Calcula el déficit/superávit calórico diario.
     * Positivo = superávit (ganancia de peso)
     * Negativo = déficit (pérdida de peso)
     */
    fun calculateDailyBalance(
        tdee: Int,
        caloriesConsumed: Int,
        caloriesBurned: Int
    ): Int {
        return caloriesConsumed - (tdee + caloriesBurned)
    }

    /**
     * Estima el tiempo necesario para alcanzar un objetivo de peso.
     * 
     * Asume:
     * - 1 kg de grasa = ~7700 kcal
     * - Déficit/superávit diario constante
     * 
     * @return Días estimados para alcanzar el objetivo
     */
    fun estimateDaysToGoal(
        currentWeight: Float,
        targetWeight: Float,
        dailyCalorieBalance: Int
    ): Int? {
        if (dailyCalorieBalance == 0) return null
        
        val weightDifference = targetWeight - currentWeight
        val totalCaloriesNeeded = weightDifference * 7700 // kcal
        val days = (totalCaloriesNeeded / dailyCalorieBalance).roundToInt()
        
        return if (days > 0) days else null
    }
}
