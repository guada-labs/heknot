package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Siempre será 1 para usuario único
    
    val name: String? = null,
    val age: Int? = null,
    val heightCm: Int? = null,
    val gender: Gender? = null,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    
    // Pesos (en kg)
    val startWeight: Float,
    val currentWeight: Float,
    val targetWeight: Float,
    
    // Metas y Configuración
    val targetDate: LocalDate? = null,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime? = null,
    val isDarkMode: Boolean? = null,
    
    val createdAt: LocalDate = LocalDate.now()
)
