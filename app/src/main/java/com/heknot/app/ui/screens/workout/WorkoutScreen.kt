package com.heknot.app.ui.screens.workout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.data.local.database.entity.WorkoutLog
import com.heknot.app.data.local.database.entity.WorkoutType
import com.heknot.app.data.local.database.entity.WorkoutCategory
import com.heknot.app.ui.AppViewModelProvider
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onNavigateToPlanCatalog: () -> Unit = {},
    onNavigateToWorkoutDetail: (Long) -> Unit = {},
    viewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val logs by viewModel.workoutLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRoutineType by remember { mutableStateOf<WorkoutType?>(null) }
    var editingLog by remember { mutableStateOf<WorkoutLog?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Actividad", 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    ) 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Actividad")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // New Section: Training Plans
            item {
                Text(
                    "Planes de Entrenamiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    onClick = onNavigateToPlanCatalog,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Explora Catálogo",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Encuentra el plan perfecto para ti",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Sección de Rutinas Preestablecidas
            item {
                Text(
                    "Rutinas y Actividades", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoutineCard(
                        title = "Gym",
                        icon = Icons.Default.FitnessCenter,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            selectedRoutineType = WorkoutType.GYM
                            showAddDialog = true 
                        }
                    )
                    RoutineCard(
                        title = "Cardio",
                        icon = Icons.Default.DirectionsRun,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            selectedRoutineType = WorkoutType.RUN
                            showAddDialog = true 
                        }
                    )
                    RoutineCard(
                        title = "Zen",
                        icon = Icons.Default.SelfImprovement,
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            selectedRoutineType = WorkoutType.OTHER
                            showAddDialog = true 
                        }
                    )
                }
            }

            // Registro Reciente
            item {
                Text(
                    "Actividad Reciente", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (logs.isEmpty()) {
                item {
                    EmptyActivityState()
                }
            }

            items(logs) { log ->
                ActivityLogItem(
                    log = log,
                    onEdit = { editingLog = it },
                    onDelete = { viewModel.deleteActivity(it) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (showAddDialog || editingLog != null) {
            ActivityFormSheet(
                initialType = selectedRoutineType ?: WorkoutType.WALK,
                existingLog = editingLog,
                onDismiss = { 
                    showAddDialog = false
                    editingLog = null
                    selectedRoutineType = null
                },
                onConfirm = { type, duration, distance, calories, effort, mood, notes ->
                    if (editingLog != null) {
                        viewModel.updateActivity(
                            editingLog!!, type, duration, distance, calories, effort, mood, notes
                        )
                    } else {
                        viewModel.logActivity(
                            type, duration, distance, calories, effort, mood, notes
                        )
                    }
                    showAddDialog = false
                    editingLog = null
                    selectedRoutineType = null
                },
                onEstimateCalories = { t, d, e ->
                    viewModel.estimateCalories(t, d, e)
                }
            )
        }
    }
}

@Composable
fun RoutineCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityLogItem(
    log: WorkoutLog,
    onEdit: (WorkoutLog) -> Unit,
    onDelete: (WorkoutLog) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onEdit(log) },
                onLongClick = { showMenu = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = WorkoutIconMapper.getIcon(log.type.iconName),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(log.type.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text(log.dateTime.format(formatter), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!log.notes.isNullOrBlank()) {
                        Text(log.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("${log.caloriesBurned ?: 0}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text("kcal", style = MaterialTheme.typography.labelSmall)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${log.durationMinutes ?: 0} min", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = { 
                        onEdit(log)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                    onClick = { 
                        onDelete(log)
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                )
            }
        }
    }
}

@Composable
fun EmptyActivityState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay actividad registrada",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormSheet(
    initialType: WorkoutType,
    existingLog: WorkoutLog? = null,
    onDismiss: () -> Unit,
    onConfirm: (WorkoutType, Int, Float?, Int?, Int?, Int?, String?) -> Unit,
    onEstimateCalories: (WorkoutType, Int, Int) -> Int
) {
    var type by remember { mutableStateOf(existingLog?.type ?: initialType) }
    var duration by remember { mutableStateOf(existingLog?.durationMinutes?.toString() ?: "30") }
    var distance by remember { mutableStateOf(existingLog?.distanceKm?.toString() ?: "") }
    
    // Estado de calorías y flag de edición manual
    var calories by remember { mutableStateOf(existingLog?.caloriesBurned?.toString() ?: "") }
    var isCaloriesManuallyEdited by remember { mutableStateOf(false) } // Empezamos siempre en modo automático

    var effort by remember { mutableStateOf(existingLog?.effortRating?.toFloat() ?: 3f) }
    var mood by remember { mutableStateOf(existingLog?.moodRating?.toFloat() ?: 3f) }
    var notes by remember { mutableStateOf(existingLog?.notes ?: "") }
    var showAllActivities by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Recálculo automático inteligente
    LaunchedEffect(type, duration, effort) {
        // Validamos si estamos exactamente en el estado inicial de una edición
        // para evitar sobrescribir calorías personalizadas al abrir el diálogo
        val isInitialState = existingLog != null && 
                             type == existingLog.type && 
                             duration == existingLog.durationMinutes.toString() && 
                             effort == existingLog.effortRating?.toFloat()

        if (!isCaloriesManuallyEdited) {
            // Solo calculamos si es un nuevo registro O si el usuario ha cambiado algún valor (ya no es initial state)
            if (existingLog == null || !isInitialState) {
                val mins = duration.toIntOrNull() ?: 0
                if (mins > 0) {
                    val estimated = onEstimateCalories(type, mins, effort.toInt())
                    calories = estimated.toString()
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (existingLog != null) "Editar Actividad" else "Nueva Actividad",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Sección: Tipo de Actividad
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Tipo de Actividad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (!showAllActivities) {
                            // Mostrar solo las actividades más comunes
                            val commonActivities = listOf(
                                WorkoutType.WALK, WorkoutType.RUN, WorkoutType.BIKE,
                                WorkoutType.GYM, WorkoutType.SWIM, WorkoutType.YOGA,
                                WorkoutType.SLEEP, WorkoutType.SITTING
                            )

                            commonActivities.chunked(3).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    row.forEach { t ->
                                        ActivitySelectCard(
                                            type = t,
                                            isSelected = type == t,
                                            onClick = { type = t },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - row.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            TextButton(
                                onClick = { showAllActivities = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver todas las actividades")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            // Buscador
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar actividad...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mostrar todas organizadas por categoría (filtradas)
                            WorkoutCategory.values().forEach { category ->
                                val activities = WorkoutType.getByCategory(category)
                                    .filter { 
                                        searchQuery.isBlank() || 
                                        it.displayName.contains(searchQuery, ignoreCase = true)
                                    }
                                if (activities.isNotEmpty()) {
                                    Text(
                                        category.displayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    activities.chunked(3).forEach { row ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            row.forEach { t ->
                                                ActivitySelectCard(
                                                    type = t,
                                                    isSelected = type == t,
                                                    onClick = { type = t },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            repeat(3 - row.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }

                            TextButton(
                                onClick = { showAllActivities = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver menos")
                            }
                        }
                    }
                }

                // Sección: Métricas
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "Métricas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duración (minutos)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            singleLine = true
                        )

                        if (type.requiresDistance) {
                            OutlinedTextField(
                                value = distance,
                                onValueChange = { distance = it },
                                label = { Text("Distancia (km)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                                ),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = calories,
                            onValueChange = { 
                                calories = it
                                isCaloriesManuallyEdited = true
                            },
                            label = { Text("Calorías (calculado automáticamente)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            singleLine = true,
                            trailingIcon = {
                                if (isCaloriesManuallyEdited) {
                                    IconButton(onClick = { isCaloriesManuallyEdited = false }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Recalcular")
                                    }
                                }
                            }
                        )
                    }
                }

                // Sección: Feedback Personal
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "Feedback Personal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Esfuerzo / Cansancio",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    getEffortEmoji(effort.toInt()),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Slider(
                                value = effort,
                                onValueChange = { effort = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Estado de ánimo",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    getMoodEmoji(mood.toInt()),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Slider(
                                value = mood,
                                onValueChange = { mood = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notas (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        onConfirm(
                            type,
                            duration.toIntOrNull() ?: 0,
                            distance.toFloatOrNull(),
                            calories.toIntOrNull(),
                            effort.toInt(),
                            mood.toInt(),
                            notes.ifBlank { null }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (existingLog != null) "Guardar" else "Registrar")
                }
            }
        }
    }
}

// Helpers para emojis
fun getEffortEmoji(effort: Int): String {
    return when (effort) {
        1 -> "😌" // Muy fácil
        2 -> "🙂" // Fácil
        3 -> "😐" // Moderado
        4 -> "😓" // Difícil
        5 -> "🥵" // Muy difícil
        else -> "😐"
    }
}

fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        1 -> "😞" // Muy mal
        2 -> "😕" // Mal
        3 -> "😐" // Neutral
        4 -> "🙂" // Bien
        5 -> "😄" // Excelente
        else -> "😐"
    }
}

@Composable
fun ActivitySelectCard(
    type: WorkoutType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = WorkoutIconMapper.getIcon(type.iconName),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 10.sp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
