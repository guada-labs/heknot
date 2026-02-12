package com.heknot.app.ui.screens.training.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.repository.TrainingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlanCatalogUiState(
    val plans: List<WorkoutPlan> = emptyList(),
    val filteredPlans: List<WorkoutPlan> = emptyList(),
    val difficultyFilter: String? = null,
    val isLoading: Boolean = true
)

class PlanCatalogViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanCatalogUiState())
    val uiState: StateFlow<PlanCatalogUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            trainingRepository.getAllPlans()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { allPlans ->
                    _uiState.update { 
                        it.copy(
                            plans = allPlans,
                            filteredPlans = applyFilters(allPlans, it.difficultyFilter),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun setDifficultyFilter(difficulty: String?) {
        _uiState.update { 
            it.copy(
                difficultyFilter = difficulty,
                filteredPlans = applyFilters(it.plans, difficulty)
            )
        }
    }

    private fun applyFilters(plans: List<WorkoutPlan>, difficulty: String?): List<WorkoutPlan> {
        return if (difficulty == null) {
            plans
        } else {
            plans.filter { it.difficulty == difficulty }
        }
    }
}
