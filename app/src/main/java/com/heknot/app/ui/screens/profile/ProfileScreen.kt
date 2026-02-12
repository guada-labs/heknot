package com.heknot.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.FitnessGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToEquipment: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveProfile(onBack) }) {
                        Text("Guardar", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Datos Personales ---
            ProfileSectionCard(title = "Datos Personales", icon = Icons.Default.Badge) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Sexo biológico", 
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    Gender.values().forEachIndexed { index, gender ->
                        SegmentedButton(
                            selected = uiState.gender == gender,
                            onClick = { viewModel.updateGender(gender) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = Gender.values().size),
                            icon = {
                                val icon = when(gender) {
                                    Gender.MALE -> Icons.Default.Male
                                    Gender.FEMALE -> Icons.Default.Female
                                    Gender.OTHER -> Icons.Default.MoreHoriz
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        ) {
                            Text(
                                text = when(gender) {
                                    Gender.MALE -> "Hombre"
                                    Gender.FEMALE -> "Mujer"
                                    Gender.OTHER -> "Otro"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uiState.age,
                        onValueChange = viewModel::updateAge,
                        label = { Text("Edad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    OutlinedTextField(
                        value = uiState.height,
                        onValueChange = viewModel::updateHeight,
                        label = { Text("Altura (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }

            // --- Estilo de Vida ---
            ProfileSectionCard(title = "Estilo de Vida", icon = Icons.Default.DirectionsRun) {
                Text(
                    "¿Qué tan activo eres?", 
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                ActivityLevel.values().forEach { level ->
                    val isSelected = uiState.activityLevel == level
                    Surface(
                        onClick = { viewModel.updateActivityLevel(level) },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateActivityLevel(level) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = level.displayName, 
                                    style = MaterialTheme.typography.bodyLarge, 
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = level.description, 
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // --- Objetivos ---
            ProfileSectionCard(title = "Objetivos Fitness", icon = Icons.Default.Flag) {
                FitnessGoal.values().forEach { goal ->
                    val isSelected = uiState.fitnessGoal == goal
                    Surface(
                        onClick = { viewModel.updateFitnessGoal(goal) },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when(goal) {
                                    FitnessGoal.LOSE_WEIGHT -> Icons.Default.TrendingDown
                                    FitnessGoal.MAINTAIN_WEIGHT -> Icons.Default.TrendingFlat
                                    FitnessGoal.GAIN_WEIGHT -> Icons.Default.TrendingUp
                                },
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                goal.displayName, 
                                style = MaterialTheme.typography.bodyLarge, 
                                color = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.targetWeight,
                    onValueChange = viewModel::updateTargetWeight,
                    label = { Text("Peso Objetivo (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium
                )
            }

            // --- Calculadora de Peso Ideal ---
            uiState.idealWeightResults?.let { results ->
                ProfileSectionCard(title = "Análisis de Peso Ideal", icon = Icons.Default.Calculate) {
                    Text(
                        "Rango de IMC Saludable",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${results.healthyBmiRange.first}kg - ${results.healthyBmiRange.second}kg",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    
                    Text(
                        "Basado en un IMC de 18.5 - 24.9 para tu altura.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Estimaciones por Fórmulas Médicas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        IdealWeightItem(
                            label = "Fórmula de Devine",
                            weight = results.devine,
                            onApply = { viewModel.updateTargetWeight(results.devine.toString()) }
                        )
                        IdealWeightItem(
                            label = "Fórmula de Robinson",
                            weight = results.robinson,
                            onApply = { viewModel.updateTargetWeight(results.robinson.toString()) }
                        )
                        IdealWeightItem(
                            label = "Fórmula de Miller",
                            weight = results.miller,
                            onApply = { viewModel.updateTargetWeight(results.miller.toString()) }
                        )
                    }
                }
            }

            // --- Medidas ---
            ProfileSectionCard(title = "Medidas Corporales", icon = Icons.Default.Straighten) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeasurementField("Cuello", uiState.neckCm, viewModel::updateNeck, Modifier.weight(1f))
                        MeasurementField("Cintura", uiState.waistCm, viewModel::updateWaist, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeasurementField("Cadera", uiState.hipCm, viewModel::updateHip, Modifier.weight(1f))
                        MeasurementField("Pecho", uiState.chestCm, viewModel::updateChest, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeasurementField("Brazo", uiState.armCm, viewModel::updateArm, Modifier.weight(1f))
                        MeasurementField("Muslo", uiState.thighCm, viewModel::updateThigh, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeasurementField("Pantorrilla", uiState.calfCm, viewModel::updateCalf, Modifier.weight(1f))
                        MeasurementField("% Grasa", uiState.bodyFatPercentage, viewModel::updateBodyFat, Modifier.weight(1f))
                    }
                }
            }
            
            // --- Entrenamiento ---
            ProfileSectionCard(title = "Entrenamiento", icon = Icons.Default.FitnessCenter) {
                ListItem(
                    headlineContent = { Text("Mi Equipamiento") },
                    supportingContent = { Text("Gestiona las herramientas que tienes para tus rutinas") },
                    leadingContent = { Icon(Icons.Default.Inventory, contentDescription = null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onNavigateToEquipment() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified // Just for style
            )
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun MeasurementField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun IdealWeightItem(label: String, weight: Float, onApply: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text("${weight}kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        TextButton(
            onClick = onApply,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Usar como meta", style = MaterialTheme.typography.labelSmall)
        }
    }
}
