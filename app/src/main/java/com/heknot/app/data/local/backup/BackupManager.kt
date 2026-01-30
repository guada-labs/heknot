package com.heknot.app.data.local.backup

import android.content.Context
import android.net.Uri
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.heknot.app.data.local.database.entity.*

class BackupManager(
    private val context: Context,
    private val repository: HeknotRepository
) {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun exportBackup(uri: Uri): Result<Unit> {
        return try {
            val userProfile = repository.getUserProfile().first()
            val weights = repository.getAllWeights().first()
            val workouts = repository.getAllWorkouts().first()
            val meals = repository.getAllMeals().first()

            val backup = heknotBackup(
                userProfile = userProfile?.toBackup(),
                weights = weights.map { it.toBackup() },
                workouts = workouts.map { it.toBackup() },
                meals = meals.map { it.toBackup() }
            )

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json.encodeToString(backup))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(uri: Uri): Result<Unit> {
        return try {
            val backupText = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                InputStreamReader(inputStream).readText()
            } ?: throw Exception("No se pudo leer el archivo")

            val backup = json.decodeFromString<heknotBackup>(backupText)

            // Limpiar datos actuales (opcional o preguntar al usuario)
            // Por ahora vamos a insertar lo nuevo.
            
            backup.userProfile?.let { b ->
                repository.insertOrUpdateUserProfile(
                    UserProfile(
                        name = b.name,
                        age = b.age,
                        heightCm = b.heightCm,
                        startWeight = b.startWeight,
                        currentWeight = b.currentWeight,
                        targetWeight = b.targetWeight,
                        targetDate = b.targetDate?.let { LocalDate.parse(it) },
                        reminderEnabled = b.reminderEnabled,
                        reminderTime = b.reminderTime?.let { LocalTime.parse(it) },
                        isDarkMode = b.isDarkMode,
                        createdAt = LocalDate.parse(b.createdAt)
                    )
                )
            }

            backup.weights.forEach { b ->
                repository.insertWeight(
                    WeightEntry(
                        weight = b.weight,
                        dateTime = LocalDateTime.parse(b.dateTime)
                    )
                )
            }

            backup.workouts.forEach { b ->
                repository.insertWorkout(
                    WorkoutLog(
                        type = WorkoutType.valueOf(b.type),
                        dateTime = LocalDateTime.parse(b.dateTime),
                        durationMinutes = b.durationMinutes,
                        completed = b.completed
                    )
                )
            }

            backup.meals.forEach { b ->
                repository.insertMeal(
                    MealLog(
                        type = MealType.valueOf(b.type),
                        dateTime = LocalDateTime.parse(b.dateTime),
                        description = b.description,
                        calories = b.calories
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
