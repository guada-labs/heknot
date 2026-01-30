package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Always 1 for single user setup
    
    val name: String? = null,
    val age: Int? = null,
    val heightCm: Int? = null,
    val gender: Gender? = null,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessGoal: FitnessGoal = FitnessGoal.MAINTAIN_WEIGHT,
    
    // Optional anthropometry (for body fat estimation)
    val neckCm: Float? = null,
    val waistCm: Float? = null,
    val hipCm: Float? = null,
    
    // Preferences
    val preferredUnit: String = "kg", // "kg" or "lb"
    
    // Weights (in kg)
    val startWeight: Float,
    val currentWeight: Float,
    val targetWeight: Float,
    
    // Goals and Configuration
    val targetDate: LocalDate? = null,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime? = null,
    val isDarkMode: Boolean? = null,
    val biometricEnabled: Boolean = false,
    
    val createdAt: LocalDate = LocalDate.now()
)
