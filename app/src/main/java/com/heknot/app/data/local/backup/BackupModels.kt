package com.heknot.app.data.local.backup

import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.MealType
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog
import com.heknot.app.data.local.database.entity.WorkoutType
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class heknotBackup(
    val version: Int = 1,
    val userProfile: UserProfileBackup? = null,
    val weights: List<WeightEntryBackup> = emptyList(),
    val workouts: List<WorkoutLogBackup> = emptyList(),
    val meals: List<MealLogBackup> = emptyList()
)

@Serializable
data class UserProfileBackup(
    val name: String?,
    val age: Int?,
    val heightCm: Int?,
    val startWeight: Float,
    val currentWeight: Float,
    val targetWeight: Float,
    val targetDate: String?,
    val reminderEnabled: Boolean,
    val reminderTime: String?,
    val isDarkMode: Boolean?,
    val createdAt: String,
    
    // Advanced fields (Optional)
    val neckCm: Float? = null,
    val waistCm: Float? = null,
    val hipCm: Float? = null,
    val chestCm: Float? = null,
    val armCm: Float? = null,
    val thighCm: Float? = null,
    val calfCm: Float? = null,
    val bodyFatPercentage: Float? = null
)

@Serializable
data class WeightEntryBackup(
    val weight: Float,
    val dateTime: String,
    // Body measurements
    val neckCm: Float? = null,
    val waistCm: Float? = null,
    val hipCm: Float? = null,
    val chestCm: Float? = null,
    val armCm: Float? = null,
    val thighCm: Float? = null,
    val calfCm: Float? = null,
    val note: String? = null
)

@Serializable
data class WorkoutLogBackup(
    val type: String,
    val dateTime: String,
    val durationMinutes: Int?,
    val completed: Boolean
)

@Serializable
data class MealLogBackup(
    val type: String,
    val dateTime: String,
    val description: String?,
    val calories: Int?
)

// Extensiones para convertir entre entidades y backups
fun UserProfile.toBackup() = UserProfileBackup(
    name = name,
    age = age,
    heightCm = heightCm,
    startWeight = startWeight,
    currentWeight = currentWeight,
    targetWeight = targetWeight,
    targetDate = targetDate?.toString(),
    reminderEnabled = reminderEnabled,
    reminderTime = reminderTime?.toString(),
    isDarkMode = isDarkMode,
    createdAt = createdAt.toString(),
    neckCm = neckCm,
    waistCm = waistCm,
    hipCm = hipCm,
    chestCm = chestCm,
    armCm = armCm,
    thighCm = thighCm,
    calfCm = calfCm,
    bodyFatPercentage = bodyFatPercentage
)

fun WeightEntry.toBackup() = WeightEntryBackup(
    weight = weight,
    dateTime = dateTime.toString(),
    neckCm = neckCm,
    waistCm = waistCm,
    hipCm = hipCm,
    chestCm = chestCm,
    armCm = armCm,
    thighCm = thighCm,
    calfCm = calfCm,
    note = note
)

fun WorkoutLog.toBackup() = WorkoutLogBackup(
    type = type.name,
    dateTime = dateTime.toString(),
    durationMinutes = durationMinutes,
    completed = completed
)

fun MealLog.toBackup() = MealLogBackup(
    type = type.name,
    dateTime = dateTime.toString(),
    description = description,
    calories = calories
)
