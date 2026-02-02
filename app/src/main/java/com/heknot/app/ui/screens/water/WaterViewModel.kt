package com.heknot.app.ui.screens.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.WaterLog
import com.heknot.app.data.repository.HeknotRepository
import com.heknot.app.ui.AppViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import com.heknot.app.data.local.database.entity.BeverageType

data class WaterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val dailyGoal: Int = 2500
)

class WaterViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()

    // Daily Water Goal Calculation (Weight * 35ml)
    // Daily Water Goal Calculation
    // Base: User's custom goal OR (Weight * 35ml)
    // Bonus: +12ml per minute of exercise today
    val dailyGoal: StateFlow<Int> = combine(
        repository.getUserProfile(),
        repository.getWorkoutsByDate(LocalDate.now())
    ) { profile, workouts ->
        val baseGoal = profile?.waterGoal ?: profile?.currentWeight?.let { (it * 35).toInt() } ?: 2500
        
        val exerciseBonus = workouts.sumOf { workout ->
            val factor = when(workout.type.category) {
                com.heknot.app.data.local.database.entity.WorkoutCategory.CARDIO -> 15
                com.heknot.app.data.local.database.entity.WorkoutCategory.STRENGTH -> 12
                com.heknot.app.data.local.database.entity.WorkoutCategory.FLEXIBILITY -> 8
                com.heknot.app.data.local.database.entity.WorkoutCategory.DAILY_ACTIVITY -> 5
                com.heknot.app.data.local.database.entity.WorkoutCategory.REST -> 0
                else -> 5 // Fallback
            }
            (workout.durationMinutes ?: 0) * factor
        }
        
        baseGoal + exerciseBonus
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 2500
    )

    val todaysLogs: StateFlow<List<WaterLog>> = repository.getWaterLogsByDate(LocalDate.now())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentTotal: StateFlow<Int> = repository.getTotalWaterByDate(LocalDate.now())
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
        
    val progress: StateFlow<Float> = combine(currentTotal, dailyGoal) { current, goal ->
        if (goal > 0) (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    fun addWater(amountMl: Int, type: BeverageType = BeverageType.WATER) {
        viewModelScope.launch {
            try {
                repository.insertWaterLog(
                    WaterLog(
                        amountMl = amountMl,
                        dateTime = LocalDateTime.now(),
                        type = type
                    )
                )
                _uiState.update { it.copy(successMessage = "+$amountMl ml") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al registrar") }
            }
        }
    }

    fun deleteWaterLog(log: WaterLog) {
        viewModelScope.launch {
            try {
                repository.deleteWaterLog(log)
                _uiState.update { it.copy(successMessage = "Registro eliminado") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar") }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
