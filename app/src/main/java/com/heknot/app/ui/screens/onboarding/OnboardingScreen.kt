package com.heknot.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.FitnessGoal

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.validationErrorMessage) {
        uiState.validationErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            OnboardingBottomBar(
                currentStep = uiState.currentStep,
                totalSteps = 8,
                onBack = { viewModel.previousStep() },
                onNext = {
                    if (uiState.currentStep < 8) {
                        viewModel.nextStep()
                    } else {
                        viewModel.saveUserProfile(onNavigateToHome)
                    }
                },
                isAnalyzing = uiState.isAnalyzing
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding() // Maneja el teclado
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LinearProgressIndicator(
                progress = { uiState.currentStep / 8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "StepTransition"
            ) { step ->
                StepContent(
                    step = step,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun OnboardingBottomBar(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isAnalyzing: Boolean
) {
    BottomAppBar(
        actions = {
            if (currentStep > 1) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        },
        floatingActionButton = {
            Button(
                onClick = onNext,
                enabled = !isAnalyzing,
                modifier = Modifier.height(56.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (currentStep == totalSteps) "Comenzar" else "Continuar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Composable
fun StepContent(
    step: Int,
    uiState: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (step) {
            1 -> StepWelcome(uiState.name, viewModel::updateName)
            2 -> StepLifestyle(uiState.lifestyleStory, viewModel::updateLifestyleStory)
            3 -> StepBasicHealth(
                uiState.gender, uiState.age, uiState.height, uiState.currentWeight,
                viewModel::updateGender, viewModel::updateAge, viewModel::updateHeight, viewModel::updateCurrentWeight
            )
            4 -> StepAiInsight(uiState.activityLevel, uiState.fitnessGoal, uiState.aiNarrative, viewModel::updateActivityLevel, viewModel::updateFitnessGoal)
            5 -> StepGoal(uiState.targetWeight, viewModel::updateTargetWeight)
            6 -> StepMeasurements1(uiState.neckCm, uiState.waistCm, uiState.hipCm, viewModel::updateNeck, viewModel::updateWaist, viewModel::updateHip)
            7 -> StepMeasurements2(
                uiState.chestCm, uiState.armCm, uiState.thighCm, uiState.calfCm, uiState.bodyFatPercentage,
                viewModel::updateChest, viewModel::updateArm, viewModel::updateThigh, viewModel::updateCalf, viewModel::updateBodyFat
            )
            8 -> StepSummary(uiState)
        }
    }
}

@Composable
fun StepWelcome(name: String, onNameChange: (String) -> Unit) {
    Text(
        text = "¡BIENVENIDO!",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "¿Cómo deberíamos llamarte?",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Tu nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun StepLifestyle(story: String, onStoryChange: (String) -> Unit) {
    Text(
        text = "TU HISTORIA",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Cuéntanos sobre tu estilo de vida, qué haces a diario y qué quieres lograr. Nuestra IA analizará esto para personalizar tu plan.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedTextField(
        value = story,
        onValueChange = onStoryChange,
        label = { Text("Ej: Trabajo en oficina, camino 30 min y quiero perder grasa...") },
        modifier = Modifier.fillMaxWidth().height(150.dp),
        maxLines = 5
    )
}

@Composable
fun StepBasicHealth(
    gender: Gender, age: String, height: String, weight: String,
    onGender: (Gender) -> Unit, onAge: (String) -> Unit, onHeight: (String) -> Unit, onWeight: (String) -> Unit
) {
    Text(
        text = "DATOS BÁSICOS",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    
    // Gender Selection Redesign
    Text(
        "Sexo biológico",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Gender.values().forEach { g ->
            val isSelected = gender == g
            OutlinedCard(
                onClick = { onGender(g) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                    else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                  else MaterialTheme.colorScheme.onSurface
                ),
                border = CardDefaults.outlinedCardBorder(isSelected)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val icon = when (g) {
                        Gender.MALE -> Icons.Default.Male
                        Gender.FEMALE -> Icons.Default.Female
                        Gender.OTHER -> Icons.Default.MoreHoriz
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(g.displayName, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    OutlinedTextField(
        value = age,
        onValueChange = onAge,
        label = { Text("Edad") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = height,
        onValueChange = onHeight,
        label = { Text("Altura (cm)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = weight,
        onValueChange = onWeight,
        label = { Text("Peso actual (kg)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StepAiInsight(
    activity: ActivityLevel, goal: FitnessGoal, narrative: String,
    onActivity: (ActivityLevel) -> Unit, onGoal: (FitnessGoal) -> Unit
) {
    Text(
        text = "ANÁLISIS IA",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    if (narrative.isNotEmpty()) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Text(narrative, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    Text("Confirmar Nivel de Actividad", style = MaterialTheme.typography.labelLarge)
    ActivityLevel.values().forEach { level ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = activity == level, onClick = { onActivity(level) })
            Text(level.displayName, style = MaterialTheme.typography.bodyMedium)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    Text("Confirmar Meta", style = MaterialTheme.typography.labelLarge)
    FitnessGoal.values().forEach { g ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = goal == g, onClick = { onGoal(g) })
            Text(g.displayName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun StepGoal(target: String, onTarget: (String) -> Unit) {
    Text(
        text = "TU META",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text("Hemos estimado este peso objetivo basado en tu análisis, pero puedes ajustarlo.")
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedTextField(
        value = target,
        onValueChange = onTarget,
        label = { Text("Peso objetivo (kg)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
fun StepMeasurements1(neck: String, waist: String, hip: String, onNeck: (String) -> Unit, onWaist: (String) -> Unit, onHip: (String) -> Unit) {
    Text("MEDIDAS (OPCIONAL)", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Tener estas medidas nos ayuda a calcular mejor tu grasa corporal.")
    Spacer(modifier = Modifier.height(24.dp))
    OutlinedTextField(value = neck, onValueChange = onNeck, label = { Text("Cuello (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = waist, onValueChange = onWaist, label = { Text("Cintura (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = hip, onValueChange = onHip, label = { Text("Cadera (cm)") }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun StepMeasurements2(chest: String, arm: String, thigh: String, calf: String, fat: String, onChest: (String) -> Unit, onArm: (String) -> Unit, onThigh: (String) -> Unit, onCalf: (String) -> Unit, onFat: (String) -> Unit) {
    Text("AVANZADO", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(24.dp))
    OutlinedTextField(value = chest, onValueChange = onChest, label = { Text("Pecho (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = arm, onValueChange = onArm, label = { Text("Brazo (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = thigh, onValueChange = onThigh, label = { Text("Muslo (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = calf, onValueChange = onCalf, label = { Text("Pantorrilla (cm)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = fat, onValueChange = onFat, label = { Text("% Grasa Corporal") }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun StepSummary(uiState: OnboardingUiState) {
    Text("¡TODO LISTO!", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(32.dp))
    Text("Hola, ${uiState.name}. Estamos listos para comenzar tu viaje hacia los ${uiState.targetWeight} kg.", textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Usaremos tu nivel de actividad ${uiState.activityLevel.displayName} para rastrear tu progreso.")
}
