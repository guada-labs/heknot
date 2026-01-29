package com.fittrack.app.ui.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.repository.FitTrackRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChartViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    // Vico 2.0 usa CartesianChartModelProducer
    val modelProducer = CartesianChartModelProducer()

    val uiState: StateFlow<ChartUiState> = repository.getAllWeights()
        .map { weights ->
            val sortedWeights = weights.sortedBy { it.dateTime }

            if (sortedWeights.isNotEmpty()) {
                val xValues = sortedWeights.map { it.dateTime.toLocalDate().toEpochDay().toDouble() } // Vico prefiere Number/Double
                val yValues = sortedWeights.map { it.weight.toDouble() }

                // Actualizar el productor de datos
                viewModelScope.launch {
                    modelProducer.tryRunTransaction {
                        lineSeries {
                            series(xValues, yValues)
                        }
                    }
                }

                val currentWeight = sortedWeights.last().weight
                val startWeight = sortedWeights.first().weight
                val totalDelta = currentWeight - startWeight
                val minWeight = sortedWeights.minOf { it.weight }
                val maxWeight = sortedWeights.maxOf { it.weight }

                ChartUiState(
                    currentWeight = currentWeight,
                    totalChange = totalDelta,
                    startWeight = startWeight,
                    minWeight = minWeight,
                    maxWeight = maxWeight,
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
    val totalChange: Float = 0f,
    val minWeight: Float = 0f,
    val maxWeight: Float = 0f,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
