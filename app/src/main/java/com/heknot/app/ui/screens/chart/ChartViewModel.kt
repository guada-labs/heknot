package com.heknot.app.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.repository.HeknotRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ChartViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    // Vico 2.0 usa CartesianChartModelProducer
    val modelProducer = CartesianChartModelProducer()

    val uiState: StateFlow<ChartUiState> = combine(
        repository.getAllWeights(),
        repository.getUserProfile(),
        repository.getAllWaterLogs(),
        repository.getAllWorkouts()
    ) { weights, profile, waterLogs, workouts ->
        val sortedWeights = weights.sortedBy { it.dateTime }

        if (sortedWeights.isNotEmpty()) {
            val xValues = sortedWeights.map { it.dateTime.toLocalDate().toEpochDay().toDouble() }
            val yValues = sortedWeights.map { it.weight.toDouble() }

            val currentWeight = sortedWeights.last().weight
            val startWeight = sortedWeights.first().weight
            val totalDelta = currentWeight - startWeight
            val minWeight = sortedWeights.minOf { it.weight }
            val maxWeight = sortedWeights.maxOf { it.weight }
            val targetWeight = profile?.targetWeight ?: 0f
            
            // Promedio Móvil (7 días)
            val movingAvg = mutableListOf<Pair<Double, Double>>()
            for (weight in sortedWeights) {
                val date = weight.dateTime.toLocalDate()
                val window = sortedWeights.filter { 
                    val d = it.dateTime.toLocalDate()
                    !d.isBefore(date.minusDays(7)) && !d.isAfter(date)
                }
                movingAvg.add(date.toEpochDay().toDouble() to window.map { it.weight }.average())
            }

            viewModelScope.launch {
                modelProducer.runTransaction {
                    lineSeries {
                        series(xValues, yValues)
                        if (movingAvg.size > 1) {
                            series(movingAvg.map { it.first }, movingAvg.map { it.second })
                        }
                    }
                }
            }
            
            // Analytics for New Cards
            val today = LocalDate.now()
            val sevenDaysAgo = today.minusDays(7)
            
            // 1. Water Intake (Last 7 days avg)
            val recentWater = waterLogs.filter { !it.dateTime.toLocalDate().isBefore(sevenDaysAgo) }
            val dailyWater = recentWater.groupBy { it.dateTime.toLocalDate() }
                .mapValues { entry -> entry.value.sumOf { it.amountMl } }
            val weeklyWaterAvg = if (dailyWater.isNotEmpty()) dailyWater.values.average().toInt() else 0
            
            // 2. Workout Frequency (Total in last 7 days)
            val recentWorkouts = workouts.filter { !it.dateTime.toLocalDate().isBefore(sevenDaysAgo) }
            val workoutCount = recentWorkouts.size
            
            // 3. Weight Velocity (Change in last 7 days)
            val weight7DaysAgo = sortedWeights.findLast { it.dateTime.toLocalDate().isBefore(sevenDaysAgo.plusDays(1)) }?.weight
            val weightVelocity = if (weight7DaysAgo != null) currentWeight - weight7DaysAgo else 0f

            // Simple projection for estimated completion date
            val estimatedDate = if (weightVelocity < 0 && targetWeight < currentWeight) {
                val weeklyLoss = -weightVelocity
                val remainingWeight = currentWeight - targetWeight
                val weeksToGoal = (remainingWeight / weeklyLoss).toLong()
                today.plusWeeks(weeksToGoal)
            } else {
                profile?.targetDate
            }

            ChartUiState(
                currentWeight = currentWeight,
                totalChange = totalDelta,
                startWeight = startWeight,
                minWeight = minWeight,
                maxWeight = maxWeight,
                targetWeight = targetWeight,
                estimatedDate = estimatedDate,
                weeklyWaterAvg = weeklyWaterAvg,
                waterGoal = profile?.waterGoal ?: 2500,
                workoutFrequency = workoutCount,
                weightVelocity = weightVelocity,
                isEmpty = false
            )
        } else {
            ChartUiState(isEmpty = true)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChartUiState(isLoading = true)
    )
}

data class ChartUiState(
    val currentWeight: Float = 0f,
    val startWeight: Float = 0f,
    val targetWeight: Float = 0f,
    val totalChange: Float = 0f,
    val minWeight: Float = 0f,
    val maxWeight: Float = 0f,
    val estimatedDate: LocalDate? = null,
    val weeklyWaterAvg: Int = 0,
    val waterGoal: Int = 2500,
    val workoutFrequency: Int = 0,
    val weightVelocity: Float = 0f,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
