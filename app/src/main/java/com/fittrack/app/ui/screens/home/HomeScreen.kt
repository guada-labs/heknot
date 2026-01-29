package com.fittrack.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fittrack.app.data.local.database.entity.WorkoutType
import com.fittrack.app.ui.AppViewModelProvider
import com.fittrack.app.ui.components.AddMealDialog
import com.fittrack.app.ui.components.AddWeightDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToChart: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val lastWeight by viewModel.lastWeight.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val streak by viewModel.streak.collectAsState()
    
    var showWeightDialog by remember { mutableStateOf(false) }
    var showMealDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "FitTrack", 
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            userProfile?.let {
                                Text(
                                    "Hola, ${it.name}", 
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        if (streak > 0) {
                            com.fittrack.app.ui.components.AnimatedStreakBadge(streak = streak)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showWeightDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Peso")
            }
        }
    ) { innerPadding ->
        
        if (showWeightDialog) {
            AddWeightDialog(
                onDismiss = { showWeightDialog = false },
                onConfirm = { dateTime, weight ->
                    viewModel.addWeight(weight, dateTime)
                    showWeightDialog = false
                }
            )
        }

        if (showMealDialog) {
            AddMealDialog(
                onDismiss = { showMealDialog = false },
                onConfirm = { type, description ->
                    viewModel.logMeal(type, description)
                    showMealDialog = false
                }
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de Peso Actual
            item {
                val startWeight = userProfile?.startWeight ?: 0f
                val currentWeight = lastWeight?.weight ?: userProfile?.startWeight ?: 0f
                val targetWeight = userProfile?.targetWeight ?: 0f
                
                // Calcular progreso (0.0 a 1.0)
                val totalToLose = startWeight - targetWeight
                val progress = if (totalToLose > 0) {
                    ((startWeight - currentWeight) / totalToLose).coerceIn(0f, 1f)
                } else {
                    0f
                }

                WeightCard(
                    currentWeight = currentWeight,
                    targetWeight = targetWeight,
                    progress = progress,
                    onClick = onNavigateToChart
                )
            }

            // Acciones Rápidas
            item {
                Text(
                    "Accesos Rápidos", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Caminata",
                        icon = Icons.Default.DirectionsWalk,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.logWorkout(WorkoutType.WALK) }
                    )
                    QuickActionCard(
                        title = "Rutina",
                        icon = Icons.Default.FitnessCenter,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.logWorkout(WorkoutType.HOME) }
                    )
                }
            }

            // Comidas de Hoy
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Comidas de Hoy", 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showMealDialog = true }) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "Agregar Comida",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        "${todayMeals.size} registros",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (todayMeals.isEmpty()) {
                item {
                    Text(
                        "No has registrado comidas aún.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(todayMeals) { meal ->
                MealItem(meal)
            }

            // Espacio para que el FAB no tape contenido
            item { Spacer(modifier = Modifier.size(80.dp)) }
        }
    }
}

@Composable
fun WeightCard(
    currentWeight: Float,
    targetWeight: Float,
    progress: Float,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            // Icono de flecha en la esquina
            Icon(
                imageVector = Icons.Default.DoubleArrow,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Column {
                Text(
                    "PESO ACTUAL", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$currentWeight", 
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "kg", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Meta: $targetWeight kg", 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "${(progress * 100).toInt()}%", 
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun MealItem(meal: com.fittrack.app.data.local.database.entity.MealLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(meal.type.name, fontWeight = FontWeight.Bold)
                Text(meal.description, style = MaterialTheme.typography.bodySmall)
            }
            if (meal.calories != null) {
                Text("Cal: ${meal.calories}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
