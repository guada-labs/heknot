package com.heknot.app.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.FitnessGoal
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.heknot.app.util.GoalEstimator

class ProfileViewModel(
    private val repository: HeknotRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = repository.getUserProfile().filterNotNull().first()
            uiState = ProfileUiState(
                name = profile.name ?: "",
                gender = profile.gender ?: Gender.MALE,
                age = profile.age?.toString() ?: "",
                height = profile.heightCm?.toString() ?: "",
                activityLevel = profile.activityLevel,
                fitnessGoal = profile.fitnessGoal,
                targetWeight = profile.targetWeight.toString(),
                neckCm = profile.neckCm?.toString() ?: "",
                waistCm = profile.waistCm?.toString() ?: "",
                hipCm = profile.hipCm?.toString() ?: "",
                chestCm = profile.chestCm?.toString() ?: "",
                armCm = profile.armCm?.toString() ?: "",
                thighCm = profile.thighCm?.toString() ?: "",
                calfCm = profile.calfCm?.toString() ?: "",
                bodyFatPercentage = profile.bodyFatPercentage?.toString() ?: "",
                originalProfile = profile
            )
            calculateIdealWeight()
        }
    }

    private fun calculateIdealWeight() {
        val heightInt = uiState.height.toIntOrNull() ?: 0
        if (heightInt > 0) {
            uiState = uiState.copy(
                idealWeightResults = GoalEstimator.getIdealWeightEstimates(heightInt, uiState.gender)
            )
        }
    }

    fun updateName(value: String) { uiState = uiState.copy(name = value) }
    fun updateGender(value: Gender) { 
        uiState = uiState.copy(gender = value)
        calculateIdealWeight()
    }
    fun updateAge(value: String) { uiState = uiState.copy(age = value) }
    fun updateHeight(value: String) { 
        uiState = uiState.copy(height = value)
        calculateIdealWeight()
    }
    fun updateActivityLevel(value: ActivityLevel) { uiState = uiState.copy(activityLevel = value) }
    fun updateFitnessGoal(value: FitnessGoal) { uiState = uiState.copy(fitnessGoal = value) }
    fun updateTargetWeight(value: String) { uiState = uiState.copy(targetWeight = value) }
    
    fun updateNeck(value: String) { uiState = uiState.copy(neckCm = value) }
    fun updateWaist(value: String) { uiState = uiState.copy(waistCm = value) }
    fun updateHip(value: String) { uiState = uiState.copy(hipCm = value) }
    fun updateChest(value: String) { uiState = uiState.copy(chestCm = value) }
    fun updateArm(value: String) { uiState = uiState.copy(armCm = value) }
    fun updateThigh(value: String) { uiState = uiState.copy(thighCm = value) }
    fun updateCalf(value: String) { uiState = uiState.copy(calfCm = value) }
    fun updateBodyFat(value: String) { uiState = uiState.copy(bodyFatPercentage = value) }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val original = uiState.originalProfile ?: return@launch
            val updatedProfile = original.copy(
                name = uiState.name.ifBlank { null },
                gender = uiState.gender,
                age = uiState.age.toIntOrNull(),
                heightCm = uiState.height.toIntOrNull(),
                activityLevel = uiState.activityLevel,
                fitnessGoal = uiState.fitnessGoal,
                targetWeight = uiState.targetWeight.toFloatOrNull() ?: original.targetWeight,
                neckCm = uiState.neckCm.toFloatOrNull(),
                waistCm = uiState.waistCm.toFloatOrNull(),
                hipCm = uiState.hipCm.toFloatOrNull(),
                chestCm = uiState.chestCm.toFloatOrNull(),
                armCm = uiState.armCm.toFloatOrNull(),
                thighCm = uiState.thighCm.toFloatOrNull(),
                calfCm = uiState.calfCm.toFloatOrNull(),
                bodyFatPercentage = uiState.bodyFatPercentage.toFloatOrNull()
            )
            
            if (updatedProfile != null) {
                repository.insertOrUpdateUserProfile(updatedProfile)
                onSuccess()
            }
        }
    }
}

data class ProfileUiState(
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val age: String = "",
    val height: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessGoal: FitnessGoal = FitnessGoal.MAINTAIN_WEIGHT,
    val targetWeight: String = "",
    val neckCm: String = "",
    val waistCm: String = "",
    val hipCm: String = "",
    val chestCm: String = "",
    val armCm: String = "",
    val thighCm: String = "",
    val calfCm: String = "",
    val bodyFatPercentage: String = "",
    val idealWeightResults: GoalEstimator.IdealWeightResults? = null,
    val originalProfile: UserProfile? = null
)
