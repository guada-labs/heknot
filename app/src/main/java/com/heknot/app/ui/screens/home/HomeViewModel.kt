package com.heknot.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.MealType
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog
import com.heknot.app.data.local.database.entity.WorkoutType
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import com.heknot.app.data.local.database.CalorieCalculator

data class CalorieBalance(
    val tdee: Int = 2000,
    val consumed: Int = 0,
    val burned: Int = 0,
    val netBalance: Int = 0
)

data class StreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val activeDays: Set<LocalDate> = emptySet()
)

class HomeViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    // --- States ---

    val calorieBalance: StateFlow<CalorieBalance> = combine(
        repository.getUserProfile(),
        repository.getTotalCaloriesConsumedByDate(LocalDate.now()),
        repository.getTotalCaloriesBurnedByDate(LocalDate.now())
    ) { profile, consumedSrc, burnedSrc ->
        val consumed = consumedSrc ?: 0
        val burned = burnedSrc ?: 0
        
        var tdee = 2000
        if (profile != null) {
            // Usar CalorieCalculator si es posible, o usar default
            if (profile.gender != null) {
                tdee = CalorieCalculator.calculateTDEE(profile)
            }
        }
        
        CalorieBalance(
            tdee = tdee,
            consumed = consumed,
            burned = burned,
            // Balance = Consumido - (Gasto Base + Ejercicio)
            // Si es negativo (-), estás en déficit (perdiendo peso).
            // Si es positivo (+), estás en superávit (ganando peso).
            netBalance = consumed - (tdee + burned)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalorieBalance()
    )

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

    val streakInfo: StateFlow<StreakInfo> = combine(
        repository.getAllWeights(),
        repository.getAllWorkouts(),
        repository.getAllMeals()
    ) { weights, workouts, meals ->
        val allDates = (weights.map { it.dateTime.toLocalDate() } +
                        workouts.map { it.dateTime.toLocalDate() } +
                        meals.map { it.dateTime.toLocalDate() }).toSet().sortedDescending()
        
        val current = calculateStreak(allDates)
        val best = calculateBestStreak(allDates)
        
        StreakInfo(
            currentStreak = current,
            bestStreak = best,
            activeDays = allDates.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakInfo()
    )

    private fun calculateBestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        var best = 0
        var current = 0
        var lastDate: LocalDate? = null
        
        // Asumiendo que vienen ordenadas de más reciente a más antigua
        // Pero para calcular mejor racha histórica es más fácil de antigua a reciente
        val sortedDates = dates.sorted() 
        
        for (date in sortedDates) {
            if (lastDate == null) {
                current = 1
            } else {
                if (date == lastDate.plusDays(1)) {
                    current++
                } else if (date == lastDate) {
                    continue
                } else {
                    if (current > best) best = current
                    current = 1
                }
            }
            lastDate = date
        }
        return if (current > best) current else best
    }

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        // Vienen ordenadas DESC (más reciente primero)
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
