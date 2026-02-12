package com.heknot.app.util

import com.heknot.app.data.local.database.entity.FitnessGoal
import com.heknot.app.data.local.database.entity.Gender
import kotlin.math.pow

/**
 * Logic for estimating optimal target weight and health metrics.
 */
object GoalEstimator {

    /**
     * Data class to hold various ideal weight estimates.
     */
    data class IdealWeightResults(
        val devine: Float,
        val robinson: Float,
        val miller: Float,
        val healthyBmiRange: Pair<Float, Float>
    )

    /**
     * Calculates ideal weight using Devine, Robinson, and Miller formulas.
     */
    fun getIdealWeightEstimates(heightCm: Int, gender: Gender?): IdealWeightResults {
        if (heightCm <= 0) return IdealWeightResults(0f, 0f, 0f, 0f to 0f)
        
        val heightM = heightCm / 100f
        val heightInches = heightCm / 2.54f
        val inchesOver60 = (heightInches - 60f).coerceAtLeast(0f)

        val devine = if (gender == Gender.MALE) {
            50f + 2.3f * inchesOver60
        } else {
            45.5f + 2.3f * inchesOver60
        }

        val robinson = if (gender == Gender.MALE) {
            52f + 1.9f * inchesOver60
        } else {
            49f + 1.7f * inchesOver60
        }

        val miller = if (gender == Gender.MALE) {
            56.2f + 1.41f * inchesOver60
        } else {
            53.1f + 1.36f * inchesOver60
        }

        val healthyBmiMin = 18.5f * heightM.pow(2)
        val healthyBmiMax = 24.9f * heightM.pow(2)

        return IdealWeightResults(
            devine = Math.round(devine * 10f) / 10f,
            robinson = Math.round(robinson * 10f) / 10f,
            miller = Math.round(miller * 10f) / 10f,
            healthyBmiRange = (Math.round(healthyBmiMin * 10f) / 10f) to (Math.round(healthyBmiMax * 10f) / 10f)
        )
    }

    /**
     * Estimates target weight based on height, gender, and fitness goal.
     * Uses BMI standards and Devine Formula for Ideal Body Weight (IBW).
     */
    fun estimateTargetWeight(
        heightCm: Int,
        currentWeightKg: Float,
        gender: Gender?,
        fitnessGoal: FitnessGoal
    ): Float {
        if (heightCm <= 0) return currentWeightKg
        val heightM = heightCm / 100f
        
        // 1. BMI-based target (Healthy middle range: 22.0)
        val bmiMiddleTarget = 22.0f * heightM.pow(2)
        
        // 2. Devine Formula for Ideal Body Weight (IBW)
        // Convert to inches: heightCm / 2.54
        val heightInches = heightCm / 2.54f
        val ibwDevine = if (gender == Gender.MALE) {
            50f + 2.3f * (heightInches - 60f)
        } else {
            // Default to female logic or "Other" as female-ish base
            45.5f + 2.3f * (heightInches - 60f)
        }.coerceAtLeast(40f) // Sanity check

        // Base suggestion is an average of health-standard targets
        val baseTarget = (bmiMiddleTarget + ibwDevine) / 2f
        
        return when (fitnessGoal) {
            FitnessGoal.MAINTAIN_WEIGHT -> currentWeightKg
            FitnessGoal.LOSE_WEIGHT -> {
                // If already below target, don't suggest losing more unless specifically requested
                if (currentWeightKg <= baseTarget) {
                    currentWeightKg * 0.95f // Move slightly towards the "ideal" if above, or 5% reduction
                } else {
                    baseTarget
                }
            }
            FitnessGoal.GAIN_WEIGHT -> {
                if (currentWeightKg >= baseTarget) {
                    currentWeightKg * 1.05f 
                } else {
                    // If underweight, move to a standard healthy target + 5% for muscle building
                    baseTarget * 1.05f 
                }
            }
        }.let { Math.round(it * 10f) / 10f } // Round to 1 decimal place
    }

    /**
     * Calculates Body Mass Index (BMI).
     */
    fun calculateBmi(weightKg: Float, heightCm: Int): Float {
        if (heightCm == 0) return 0f
        val heightM = heightCm / 100f
        return (weightKg / heightM.pow(2)).let { Math.round(it * 10f) / 10f }
    }
    
    /**
     * Suggests a daily water intake goal based on weight (standard recommendation: 35ml per kg).
     */
    fun estimateWaterGoal(weightKg: Float): Int {
        return (weightKg * 35f).toInt().coerceIn(1500, 4000)
    }
}
