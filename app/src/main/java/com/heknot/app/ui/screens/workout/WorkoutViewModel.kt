package com.heknot.app.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WorkoutLog
import com.heknot.app.data.local.database.entity.WorkoutType
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

import com.heknot.app.data.local.database.CalorieCalculator

class WorkoutViewModel(private val repository: HeknotRepository) : ViewModel() {

    // Todos los registros de workout
    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.getAllWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Perfil de usuario para obtener el peso y calcular calorías
    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Registra una nueva actividad calculando calorías automáticamente si no se proveen.
     * Usa el CalorieCalculator científico con MET ajustado por esfuerzo.
     */
    fun logActivity(
        type: WorkoutType,
        durationMinutes: Int,
        distanceKm: Float? = null,
        calories: Int? = null,
        effort: Int? = null,
        mood: Int? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            val profile = userProfile.value
            val weight = profile?.currentWeight ?: 75f
            
            // Usar CalorieCalculator científico
            val calculatedCalories = calories ?: CalorieCalculator.calculateActivityCalories(
                type = type,
                durationMinutes = durationMinutes,
                weightKg = weight,
                effortRating = effort
            )

            val newLog = WorkoutLog(
                type = type,
                durationMinutes = durationMinutes,
                distanceKm = distanceKm,
                caloriesBurned = calculatedCalories,
                effortRating = effort,
                moodRating = mood,
                notes = notes,
                dateTime = LocalDateTime.now()
            )
            repository.insertWorkout(newLog)
        }
    }

    fun deleteActivity(workoutLog: WorkoutLog) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutLog)
        }
    }

    fun updateActivity(
        existingLog: WorkoutLog,
        type: WorkoutType,
        durationMinutes: Int,
        distanceKm: Float?,
        calories: Int?,
        effort: Int?,
        mood: Int?,
        notes: String?
    ) {
        viewModelScope.launch {
            val weight = userProfile.value?.currentWeight ?: 75f
            
            // Recalcular calorías si no fueron provistas manualmente
            val calculatedCalories = calories ?: CalorieCalculator.calculateActivityCalories(
                type = type,
                durationMinutes = durationMinutes,
                weightKg = weight,
                effortRating = effort
            )

            val updatedLog = existingLog.copy(
                type = type,
                durationMinutes = durationMinutes,
                distanceKm = distanceKm,
                caloriesBurned = calculatedCalories,
                effortRating = effort,
                moodRating = mood,
                notes = notes
            )
            repository.updateWorkout(updatedLog)
        }
    }

    /**
     * Calcula una estimación de calorías basada en el tipo, duración y el peso actual del usuario.
     * Usa el CalorieCalculator científico.
     */
    fun estimateCalories(type: WorkoutType, durationMinutes: Int, effortRating: Int? = null): Int {
        val weight = userProfile.value?.currentWeight ?: 75f
        return CalorieCalculator.calculateActivityCalories(
            type = type,
            durationMinutes = durationMinutes,
            weightKg = weight,
            effortRating = effortRating
        )
    }

    /**
     * Calcula el TDEE (gasto calórico diario total) del usuario.
     */
    fun calculateTDEE(): Int? {
        val profile = userProfile.value ?: return null
        return CalorieCalculator.calculateTDEE(profile)
    }
}
