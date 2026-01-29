package com.fittrack.app.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.local.database.entity.UserProfile
import com.fittrack.app.data.local.database.entity.WeightEntry
import com.fittrack.app.data.repository.FitTrackRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class OnboardingViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    fun updateName(name: String) {
        uiState = uiState.copy(name = name)
    }

    fun updateCurrentWeight(weight: String) {
        uiState = uiState.copy(currentWeight = weight)
    }

    fun updateTargetWeight(weight: String) {
        uiState = uiState.copy(targetWeight = weight)
    }

    fun updateHeight(height: String) {
        uiState = uiState.copy(height = height)
    }

    fun updateAge(age: String) {
        uiState = uiState.copy(age = age)
    }

    fun saveUserProfile(onSuccess: () -> Unit) {
        if (!validateInput()) return

        viewModelScope.launch {
            val currentWeightFloat = uiState.currentWeight.toFloatOrNull() ?: 0f
            val targetWeightFloat = uiState.targetWeight.toFloatOrNull() ?: 0f
            val heightInt = uiState.height.toIntOrNull()
            val ageInt = uiState.age.toIntOrNull()

            // 1. Crear Perfil
            val userProfile = UserProfile(
                id = 1,
                name = uiState.name.ifBlank { null },
                currentWeight = currentWeightFloat,
                startWeight = currentWeightFloat, // El peso actual ES el inicial
                targetWeight = targetWeightFloat,
                heightCm = heightInt,
                age = ageInt,
                createdAt = LocalDate.now()
            )
            repository.insertOrUpdateUserProfile(userProfile)

            // 2. Crear primer registro de peso
            val initialWeightEntry = WeightEntry(
                weight = currentWeightFloat,
                dateTime = LocalDateTime.now(),
                note = "Inicio del viaje 💪"
            )
            repository.insertWeight(initialWeightEntry)

            onSuccess()
        }
    }

    private fun validateInput(): Boolean {
        val currentWeight = uiState.currentWeight.toFloatOrNull()
        val targetWeight = uiState.targetWeight.toFloatOrNull()

        val isValid = currentWeight != null && currentWeight > 0 &&
                      targetWeight != null && targetWeight > 0

        uiState = uiState.copy(showError = !isValid)
        return isValid
    }
}

data class OnboardingUiState(
    val name: String = "",
    val currentWeight: String = "",
    val targetWeight: String = "",
    val height: String = "",
    val age: String = "",
    val showError: Boolean = false
)
