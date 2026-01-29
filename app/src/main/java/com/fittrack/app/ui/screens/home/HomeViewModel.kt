package com.fittrack.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.local.database.entity.MealLog
import com.fittrack.app.data.local.database.entity.MealType
import com.fittrack.app.data.local.database.entity.UserProfile
import com.fittrack.app.data.local.database.entity.WeightEntry
import com.fittrack.app.data.local.database.entity.WorkoutLog
import com.fittrack.app.data.local.database.entity.WorkoutType
import com.fittrack.app.data.repository.FitTrackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    // Perfil del usuario (Nombre, meta, etc.)
    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Último peso registrado (para el card principal)
    val lastWeight: StateFlow<WeightEntry?> = repository.getLastWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Comidas registradas HOY
    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsByDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Registrar un entrenamiento rápido
    fun logWorkout(type: WorkoutType, durationMinutes: Int = 30) {
        viewModelScope.launch {
            val log = WorkoutLog(
                date = LocalDate.now(),
                type = type,
                durationMinutes = durationMinutes,
                completed = true
            )
            repository.insertWorkout(log)
        }
    }

    // Registrar una comida rápida
    fun logMeal(type: MealType, description: String) {
        viewModelScope.launch {
            if (description.isBlank()) return@launch
            
            val log = MealLog(
                date = LocalDate.now(),
                type = type,
                description = description
            )
            repository.insertMeal(log)
        }
    }
}
