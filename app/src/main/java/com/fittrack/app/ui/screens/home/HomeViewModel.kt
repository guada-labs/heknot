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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val lastWeight: StateFlow<WeightEntry?> = repository.getLastWeight()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsByDate(LocalDate.now())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val streak: StateFlow<Int> = combine(
        repository.getAllWeights(),
        repository.getAllWorkouts(),
        repository.getAllMeals()
    ) { weights, workouts, meals ->
        val allDates = (weights.map { it.dateTime.toLocalDate() } +
                        workouts.map { it.dateTime.toLocalDate() } +
                        meals.map { it.dateTime.toLocalDate() }).toSet().sortedDescending()
        
        calculateStreak(allDates)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        if (dates.first() != today && dates.first() != yesterday) return 0
        
        var currentStreak = 0
        var currentCheckDate = dates.first()
        
        for (i in dates.indices) {
            val date = dates[i]
            if (i == 0) {
                currentStreak = 1
                currentCheckDate = date
            } else {
                if (date == currentCheckDate.minusDays(1)) {
                    currentStreak++
                    currentCheckDate = date
                } else if (date == currentCheckDate) {
                    continue
                } else {
                    break
                }
            }
        }
        return currentStreak
    }

    fun logWorkout(type: WorkoutType) {
        viewModelScope.launch {
            repository.insertWorkout(
                WorkoutLog(
                    dateTime = LocalDateTime.now(),
                    type = type,
                    durationMinutes = 30, // Default
                    completed = true
                )
            )
        }
    }

    fun addWeight(weight: Float, dateTime: LocalDateTime) {
        viewModelScope.launch {
            repository.insertWeight(WeightEntry(weight = weight, dateTime = dateTime))
            repository.updateCurrentWeight(weight)
        }
    }
    
    fun logMeal(type: MealType, description: String, calories: Int? = null) {
        viewModelScope.launch {
            if (description.isBlank()) return@launch
            repository.insertMeal(
                MealLog(
                    dateTime = LocalDateTime.now(),
                    type = type,
                    description = description,
                    calories = calories
                )
            )
        }
    }
}
