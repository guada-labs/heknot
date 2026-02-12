package com.heknot.app.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.local.database.entity.UserProfile
import com.heknot.app.data.local.database.entity.WeightEntry
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.FitnessGoal
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.util.GeminiOnboardingAnalyst
import com.heknot.app.util.GoalEstimator
import java.time.LocalDate
import java.time.LocalDateTime

class OnboardingViewModel(
    private val repository: HeknotRepository,
    private val analyst: GeminiOnboardingAnalyst
) : ViewModel() {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    init {
        loadExistingData()
    }

    private fun loadExistingData() {
        viewModelScope.launch {
            repository.getUserProfile().first()?.let { profile ->
                uiState = uiState.copy(
                    name = profile.name ?: "",
                    gender = profile.gender ?: Gender.MALE,
                    currentWeight = profile.currentWeight.toString(),
                    targetWeight = profile.targetWeight.toString(),
                    height = profile.heightCm?.toString() ?: "",
                    age = profile.age?.toString() ?: "",
                    activityLevel = profile.activityLevel,
                    fitnessGoal = profile.fitnessGoal,
                    neckCm = profile.neckCm?.toString() ?: "",
                    waistCm = profile.waistCm?.toString() ?: "",
                    hipCm = profile.hipCm?.toString() ?: "",
                    chestCm = profile.chestCm?.toString() ?: "",
                    armCm = profile.armCm?.toString() ?: "",
                    thighCm = profile.thighCm?.toString() ?: "",
                    calfCm = profile.calfCm?.toString() ?: "",
                    bodyFatPercentage = profile.bodyFatPercentage?.toString() ?: ""
                )
            }
        }
    }


    fun nextStep() {
        if (validateStep(uiState.currentStep)) {
            if (uiState.currentStep == 2) { // Just finished Lifestyle Story
                runAnalysis()
            }
            uiState = uiState.copy(currentStep = uiState.currentStep + 1)
        }
    }

    fun previousStep() {
        if (uiState.currentStep > 1) {
            uiState = uiState.copy(currentStep = uiState.currentStep - 1)
        }
    }

    private fun runAnalysis() {
        viewModelScope.launch {
            uiState = uiState.copy(isAnalyzing = true)
            val result = analyst.analyzeLifestyle(uiState.lifestyleStory)
            uiState = uiState.copy(
                activityLevel = result.suggestedActivityLevel,
                fitnessGoal = result.suggestedFitnessGoal,
                aiNarrative = result.healthNarrative,
                isAnalyzing = false
            )
            // After analysis, also estimate target weight if not set
            if (uiState.targetWeight.isBlank()) {
                val estimated = GoalEstimator.estimateTargetWeight(
                    heightCm = uiState.height.toIntOrNull() ?: 170,
                    currentWeightKg = uiState.currentWeight.toFloatOrNull() ?: 70f,
                    gender = uiState.gender,
                    fitnessGoal = result.suggestedFitnessGoal
                )
                uiState = uiState.copy(targetWeight = estimated.toString())
            }
        }
    }

    fun updateName(name: String) { 
        uiState = uiState.copy(name = name, validationErrorMessage = null) 
    }
    fun updateGender(gender: Gender) { 
        uiState = uiState.copy(gender = gender, validationErrorMessage = null) 
    }
    fun updateCurrentWeight(weight: String) { 
        uiState = uiState.copy(currentWeight = weight, validationErrorMessage = null) 
    }
    fun updateTargetWeight(weight: String) { 
        uiState = uiState.copy(targetWeight = weight, validationErrorMessage = null) 
    }
    fun updateHeight(height: String) { 
        uiState = uiState.copy(height = height, validationErrorMessage = null) 
    }
    fun updateAge(age: String) { 
        uiState = uiState.copy(age = age, validationErrorMessage = null) 
    }
    fun updateLifestyleStory(story: String) { 
        uiState = uiState.copy(lifestyleStory = story, validationErrorMessage = null) 
    }
    fun updateActivityLevel(level: ActivityLevel) { 
        uiState = uiState.copy(activityLevel = level, validationErrorMessage = null) 
    }
    fun updateFitnessGoal(goal: FitnessGoal) { 
        uiState = uiState.copy(fitnessGoal = goal, validationErrorMessage = null) 
    }
    
    // Body Measurement Updates
    fun updateNeck(value: String) { uiState = uiState.copy(neckCm = value) }
    fun updateWaist(value: String) { uiState = uiState.copy(waistCm = value) }
    fun updateHip(value: String) { uiState = uiState.copy(hipCm = value) }
    fun updateChest(value: String) { uiState = uiState.copy(chestCm = value) }
    fun updateArm(value: String) { uiState = uiState.copy(armCm = value) }
    fun updateThigh(value: String) { uiState = uiState.copy(thighCm = value) }
    fun updateCalf(value: String) { uiState = uiState.copy(calfCm = value) }
    fun updateBodyFat(value: String) { uiState = uiState.copy(bodyFatPercentage = value) }

    fun saveUserProfile(onSuccess: () -> Unit) {
        if (!validateInput()) return

        viewModelScope.launch {
            val currentWeightFloat = parseDecimal(uiState.currentWeight) ?: 0f
            val targetWeightFloat = parseDecimal(uiState.targetWeight) ?: 0f
            val heightInt = parseInt(uiState.height)
            val ageInt = parseInt(uiState.age)

            // Get existing profile to preserve some settings (like creation date, reminder, etc.)
            val existingProfile = repository.getUserProfile().first()

            // 1. Crear/Actualizar Perfil
            val userProfile = UserProfile(
                id = 1,
                name = uiState.name.ifBlank { null },
                gender = uiState.gender,
                currentWeight = currentWeightFloat,
                startWeight = existingProfile?.startWeight ?: currentWeightFloat,
                targetWeight = targetWeightFloat,
                heightCm = heightInt,
                age = ageInt,
                activityLevel = uiState.activityLevel,
                fitnessGoal = uiState.fitnessGoal,
                waterGoal = GoalEstimator.estimateWaterGoal(currentWeightFloat),
                neckCm = uiState.neckCm.toFloatOrNull(),
                waistCm = uiState.waistCm.toFloatOrNull(),
                hipCm = uiState.hipCm.toFloatOrNull(),
                chestCm = uiState.chestCm.toFloatOrNull(),
                armCm = uiState.armCm.toFloatOrNull(),
                thighCm = uiState.thighCm.toFloatOrNull(),
                calfCm = uiState.calfCm.toFloatOrNull(),
                bodyFatPercentage = uiState.bodyFatPercentage.toFloatOrNull(),
                createdAt = existingProfile?.createdAt ?: LocalDate.now()
            )
            repository.insertOrUpdateUserProfile(userProfile)

            // 2. Crear registro de peso solo si el peso cambió significativamente o si no hay registros hoy
            // Para simplificar y asegurar consistencia con el onboarding, lo agregamos siempre como un hito.
            val initialWeightEntry = WeightEntry(
                weight = currentWeightFloat,
                dateTime = LocalDateTime.now(),
                neckCm = uiState.neckCm.toFloatOrNull(),
                waistCm = uiState.waistCm.toFloatOrNull(),
                hipCm = uiState.hipCm.toFloatOrNull(),
                chestCm = uiState.chestCm.toFloatOrNull(),
                armCm = uiState.armCm.toFloatOrNull(),
                thighCm = uiState.thighCm.toFloatOrNull(),
                calfCm = uiState.calfCm.toFloatOrNull(),
                note = if (existingProfile == null) "Inicio del viaje 💪" else "Actualización desde Onboarding 🎯"
            )
            repository.insertWeight(initialWeightEntry)

            onSuccess()
        }
    }

    private fun parseDecimal(value: String): Float? {
        return value.trim().replace(',', '.').toFloatOrNull()
    }

    private fun parseInt(value: String): Int? {
        return value.trim().toIntOrNull()
    }

    private fun validateStep(step: Int): Boolean {
        return when (step) {
            1 -> {
                val valid = uiState.name.trim().isNotBlank()
                if (!valid) uiState = uiState.copy(validationErrorMessage = "Por favor, ingresa tu nombre")
                valid
            }
            3 -> {
                val weightValid = parseDecimal(uiState.currentWeight) != null
                val heightValid = parseInt(uiState.height) != null
                val ageValid = parseInt(uiState.age) != null
                
                when {
                    !ageValid -> uiState = uiState.copy(validationErrorMessage = "Ingresa una edad válida")
                    !heightValid -> uiState = uiState.copy(validationErrorMessage = "Ingresa una altura válida (cm)")
                    !weightValid -> uiState = uiState.copy(validationErrorMessage = "Ingresa un peso válido (kg)")
                }
                
                weightValid && heightValid && ageValid
            }
            5 -> {
                val valid = parseDecimal(uiState.targetWeight) != null
                if (!valid) uiState = uiState.copy(validationErrorMessage = "Ingresa un peso objetivo válido")
                valid
            }
            else -> true
        }
    }

    private fun validateInput(): Boolean {
        return parseDecimal(uiState.currentWeight) != null && 
               parseDecimal(uiState.targetWeight) != null
    }
}

data class OnboardingUiState(
    val currentStep: Int = 1,
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val currentWeight: String = "",
    val targetWeight: String = "",
    val height: String = "",
    val age: String = "",
    val lifestyleStory: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessGoal: FitnessGoal = FitnessGoal.MAINTAIN_WEIGHT,
    val aiNarrative: String = "",
    val isAnalyzing: Boolean = false,
    
    // Advanced Body Measurements
    val neckCm: String = "",
    val waistCm: String = "",
    val hipCm: String = "",
    val chestCm: String = "",
    val armCm: String = "",
    val thighCm: String = "",
    val calfCm: String = "",
    val bodyFatPercentage: String = "",
    
    val validationErrorMessage: String? = null
)
