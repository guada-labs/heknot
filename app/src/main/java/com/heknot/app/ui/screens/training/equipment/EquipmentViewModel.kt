package com.heknot.app.ui.screens.training.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.repository.TrainingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquipmentViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    val equipmentState: StateFlow<List<UserEquipment>> = trainingRepository.getAllEquipment()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleEquipment(equipmentId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            trainingRepository.updateEquipmentAvailability(equipmentId, isAvailable)
        }
    }
}
