package com.fittrack.app.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.repository.FitTrackRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ChartViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    // Vico 2.0 usa CartesianChartModelProducer
    val modelProducer = CartesianChartModelProducer()

    val uiState: StateFlow<ChartUiState> = combine(
        repository.getAllWeights(),
        repository.getUserProfile()
    ) { weights, profile ->
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
                modelProducer.tryRunTransaction {
                    lineSeries {
                        series(xValues, yValues)
                        if (movingAvg.size > 1) {
                            series(movingAvg.map { it.first }, movingAvg.map { it.second })
                        }
                    }
                }
            }
            
            var estimatedDate: LocalDate? = null
            if (sortedWeights.size >= 2) {
                val first = sortedWeights.first()
                val last = sortedWeights.last()
                val days = ChronoUnit.DAYS.between(first.dateTime.toLocalDate(), last.dateTime.toLocalDate())
                
                if (days > 0) {
                    val ratePerDay = (last.weight - first.weight) / days.toFloat()
                    val toGoal = targetWeight - last.weight
                    
                    if ((toGoal > 0 && ratePerDay > 0) || (toGoal < 0 && ratePerDay < 0)) {
                        val daysToGoal = (toGoal / ratePerDay).toLong()
                        if (daysToGoal in 1..3650) {
                            estimatedDate = last.dateTime.toLocalDate().plusDays(daysToGoal)
                        }
                    }
                }
            }

            ChartUiState(
                currentWeight = currentWeight,
                totalChange = totalDelta,
                startWeight = startWeight,
                minWeight = minWeight,
                maxWeight = maxWeight,
                targetWeight = targetWeight,
                estimatedDate = estimatedDate,
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
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
