package com.heknot.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.MealLog
import com.heknot.app.data.local.database.entity.WaterLog
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.local.database.entity.WorkoutLog
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

// Clase sellada para representar los items en la lista mezclada
sealed class HistoryItem {
    abstract val date: LocalDate
    abstract val dateTime: LocalDateTime
    
    data class Weight(val entry: WeightEntry) : HistoryItem() {
        override val date: LocalDate = entry.dateTime.toLocalDate()
        override val dateTime: LocalDateTime = entry.dateTime
    }
    data class Workout(val log: WorkoutLog) : HistoryItem() {
        override val date: LocalDate = log.dateTime.toLocalDate()
        override val dateTime: LocalDateTime = log.dateTime
    }
    data class Meal(val log: MealLog) : HistoryItem() {
        override val date: LocalDate = log.dateTime.toLocalDate()
        override val dateTime: LocalDateTime = log.dateTime
    }
    data class Water(val log: WaterLog) : HistoryItem() {
        override val date: LocalDate = log.date
        override val dateTime: LocalDateTime = log.dateTime
    }
}

class HistoryViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    val historyItems: StateFlow<List<HistoryItem>> = combine(
        repository.getAllWeights(),
        repository.getAllWorkouts(),
        repository.getAllMeals(),
        repository.getAllWaterLogs()
    ) { weights, workouts, meals, waterLogs ->
        val items = mutableListOf<HistoryItem>()
        
        items.addAll(weights.map { HistoryItem.Weight(it) })
        items.addAll(workouts.map { HistoryItem.Workout(it) })
        items.addAll(meals.map { HistoryItem.Meal(it) })
        items.addAll(waterLogs.map { HistoryItem.Water(it) })
        
        // Ordenar por LocalDateTime
        items.sortedByDescending { it.dateTime }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteItem(item: HistoryItem) {
        viewModelScope.launch {
            when (item) {
                is HistoryItem.Weight -> repository.deleteWeight(item.entry)
                is HistoryItem.Workout -> repository.deleteWorkout(item.log)
                is HistoryItem.Meal -> repository.deleteMeal(item.log)
                is HistoryItem.Water -> repository.deleteWaterLog(item.log)
            }
        }
    }

    fun addWeight(weight: Float, dateTime: LocalDateTime) {
        viewModelScope.launch {
            repository.insertWeight(WeightEntry(weight = weight, dateTime = dateTime))
        }
    }
}
