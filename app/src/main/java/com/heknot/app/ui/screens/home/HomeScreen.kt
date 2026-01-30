package com.heknot.app.ui.screens.home

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingDown
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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.data.local.database.entity.WorkoutType
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.ui.components.AddMealDialog
import com.heknot.app.ui.components.AddWeightDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToWorkout: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToChart: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val lastWeight by viewModel.lastWeight.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    
    var showWeightDialog by remember { mutableStateOf(false) }
    var showMealDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }

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
                                "Heknot", 
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            userProfile?.let {
                                Text(
                                    "Hola, ${it.name}", 
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (streakInfo.currentStreak > 0) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                            TextButton(
                                onClick = { showStreakDialog = true },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                com.heknot.app.ui.components.ConsistencyShieldBadge(streak = streakInfo.currentStreak)
                            }
                        }
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
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

        if (showStreakDialog) {
            com.heknot.app.ui.components.StreakDetailDialog(
                streakInfo = streakInfo,
                onDismiss = { showStreakDialog = false }
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

            // Balance Calórico (Nutrición + Ejercicio)
            item {
                Text(
                    "Balance Diario", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                val balance = viewModel.calorieBalance.collectAsState().value
                CalorieBalanceCard(
                    balance = balance,
                    streak = streakInfo.currentStreak,
                    onClick = { showStreakDialog = true } // Al hacer clic en el balance también puede abrir la racha
                )
            }

            // Herramientas / Links
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Herramientas", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortcutLinkItem(
                        title = "Plan de Entrenamiento",
                        subtitle = "Gestiona tus rutinas y ejercicios",
                        icon = Icons.Default.FitnessCenter,
                        onClick = onNavigateToWorkout
                    )
                    ShortcutLinkItem(
                        title = "Diario de Comidas",
                        subtitle = "Ver detalle de calorías y macros",
                        icon = Icons.Default.Restaurant,
                        onClick = onNavigateToNutrition
                    )
                    ShortcutLinkItem(
                        title = "Progreso Visual",
                        subtitle = "Ver gráficas y evolución de peso",
                        icon = Icons.Default.BarChart,
                        onClick = onNavigateToChart
                    )
                }
            }

            item { Spacer(modifier = Modifier.size(32.dp)) }
        }
    }
}

@Composable
fun SummaryMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    value, 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    unit, 
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                title, 
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ShortcutLinkItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    subtitle, 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ArrowForwardIos, 
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
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
fun CalorieBalanceCard(
    balance: CalorieBalance,
    streak: Int,
    onClick: () -> Unit
) {
    // Si el objetivo es perder peso, un balance negativo (déficit) es bueno (verde/primary)
    // Si es positivo (superávit), alerta (error/tertiary)
    val isDeficit = balance.netBalance <= 0
    val statusColor = if (isDeficit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header con Racha
            Row(
                Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Título y Balance
                Column {
                    Text("BALANCE NETO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text(
                        "${if (balance.netBalance > 0) "+" else ""}${balance.netBalance}", 
                        style = MaterialTheme.typography.displaySmall, 
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                    Text(
                        if (isDeficit) "Déficit (Bajando)" else "Superávit (Subiendo)", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Columna derecha: Llama dinámica basada en el esfuerzo
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                    val burnIntensity = (balance.burned / 1000f).coerceIn(0f, 1f)
                    Box(contentAlignment = Alignment.Center) {
                        com.heknot.app.ui.components.StylizedParticleFlame(
                            modifier = Modifier.size(72.dp),
                            intensity = burnIntensity
                        )
                    }
                }
            }
            
            
            Spacer(Modifier.height(16.dp))
            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                 // Metricas con signos contables
                 MetricColumn("Ingesta", "+${balance.consumed}", Icons.Default.Restaurant, MaterialTheme.colorScheme.onSurface)
                 MetricColumn("Gasto Base", "-${balance.tdee}", Icons.Default.Flag, MaterialTheme.colorScheme.onSurfaceVariant)
                 MetricColumn("Ejercicio", "-${balance.burned}", Icons.Default.FitnessCenter, MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun MetricColumn(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = color.copy(alpha = 0.7f))
            Spacer(Modifier.size(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.size(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
