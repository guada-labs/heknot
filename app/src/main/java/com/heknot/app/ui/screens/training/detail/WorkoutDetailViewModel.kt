package com.heknot.app.ui.screens.training.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import com.heknot.app.data.repository.TrainingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WorkoutDetailUiState(
    val plan: WorkoutPlan? = null,
    val routines: List<WorkoutRoutine> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val planId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    init {
        loadPlanDetails()
    }

    private fun loadPlanDetails() {
        viewModelScope.launch {
            combine(
                trainingRepository.getPlanById(planId),
                trainingRepository.getRoutinesForPlan(planId)
            ) { plan, routines ->
                WorkoutDetailUiState(
                    plan = plan,
                    routines = routines,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
