package com.heknot.app.ui.screens.nutrition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.data.local.database.entity.FoodItem
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.ui.screens.water.WaterViewModel
import com.heknot.app.ui.screens.nutrition.WaterLogSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    waterViewModel: WaterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val summary by viewModel.dailyNutritionSummary.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val foodItems by viewModel.allFoodItems.collectAsState(initial = emptyList())
    
    // Water State
    val waterGoal by waterViewModel.dailyGoal.collectAsState()
    val waterCurrent by waterViewModel.currentTotal.collectAsState()
    val waterProgress by waterViewModel.progress.collectAsState()
    val waterLogs by waterViewModel.todaysLogs.collectAsState()
    
    // UI State
    var showFoodItemForm by remember { mutableStateOf(false) }
    var showAddOptions by remember { mutableStateOf(false) }
    var showWaterDialog by remember { mutableStateOf(false) }
    var showMealScanner by remember { mutableStateOf(false) }
    var editingFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Resumen de Hoy", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExpandableFab(
                isExpanded = isFabExpanded,
                onToggle = { isFabExpanded = !isFabExpanded },
                onAddIngredient = {
                    isFabExpanded = false
                    editingFoodItem = null
                    showFoodItemForm = true // Direct to manual form for ingredients
                },
                onAddMeal = { 
                    isFabExpanded = false
                    showAddOptions = true // Open selection sheet for meals
                },
                onAddWater = {
                    isFabExpanded = false
                    showWaterDialog = true
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. DAILY SUMMARY ---
                item {
                    val calorieGoal = 2500f // TODO: Get from preferences
                    val progressCalories = (summary.totalCalories.toFloat() / calorieGoal).coerceIn(0f, 1f)
                    
                    DataCard(title = "Energía y Macros") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Calories (Main Focus)
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    strokeWidth = 10.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                CircularProgressIndicator(
                                    progress = { progressCalories },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 10.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${summary.totalCalories}", 
                                        style = MaterialTheme.typography.headlineSmall, 
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text("kcal", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(24.dp))

                            // Macros (Right Side)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MacroBar("Proteína", summary.totalProtein, 150f, Color(0xFF4CAF50))
                                MacroBar("Carbos", summary.totalCarbs, 250f, Color(0xFFFF9800))
                                MacroBar("Grasas", summary.totalFat, 70f, Color(0xFFF44336))
                            }
                        }
                    }
                }

                // --- 2. HYDRATION (Simplified & Interactive) ---
                item {
                    Card(
                        onClick = { showWaterDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeWidth = 4.dp
                                )
                                CircularProgressIndicator(
                                    progress = { waterProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFF00B2FF),
                                    strokeWidth = 4.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Icon(Icons.Default.WaterDrop, null, tint = Color(0xFF00B2FF), modifier = Modifier.size(24.dp))
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hidratación", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text("$waterCurrent / $waterGoal ml", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            
                            IconButton(
                                onClick = { showWaterDialog = true },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF00B2FF).copy(alpha = 0.1f))
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color(0xFF00B2FF))
                            }
                        }
                    }
                }

                // --- 3. RECENT MEALS ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comidas de Hoy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                if (todayMeals.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Fastfood, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No has registrado comidas hoy.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(todayMeals) { meal ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Fastfood, null, tint = MaterialTheme.colorScheme.primary)
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(meal.description ?: "Comida", fontWeight = FontWeight.Bold)
                                    Text("${meal.calories} kcal • ${meal.type.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                
                // --- 4. MY FOODS ---
                item {
                    Text(
                        text = "Mis Alimentos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                if (foodItems.isEmpty()) {
                     item {
                        Text("No tienes ingredientes guardados.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                items(foodItems) { item ->
                    Card(
                        onClick = { 
                            editingFoodItem = item
                            showFoodItemForm = true
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Eco, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("${item.calories} kcal / ${item.servingSize}${item.servingUnit}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            IconButton(onClick = { viewModel.logFoodItemAsMeal(item) }) {
                                Icon(Icons.Rounded.Add, "Log", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
            
            // Dim background when FAB is expanded
            if (isFabExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                        .clickable { isFabExpanded = false }
                )
            }
        }
        

        // --- SHEETS ---
        
        // 1. Meal Options Sheet
        if (showAddOptions) {
            AddMealOptionsSheet(
                onDismiss = { showAddOptions = false },
                onNavigateToMealPhoto = {
                    showAddOptions = false
                    showMealScanner = true
                },
                onNavigateToManualBuilder = {
                    showAddOptions = false
                    // TODO: Navigate to Meal Builder (Recipe/Composite)
                }
            )
        }

        // 2. Ingredient Form (Single Item)
        if (showFoodItemForm) {
            FoodItemFormSheet(
                existingItem = editingFoodItem,
                onDismiss = { 
                    showFoodItemForm = false 
                    editingFoodItem = null
                },
                onConfirm = { foodItem, shouldLogDirectly ->
                    if (editingFoodItem == null || editingFoodItem?.id == 0L) {
                        viewModel.insertFoodItem(foodItem)
                    } else {
                        viewModel.updateFoodItem(foodItem)
                    }
                    if (shouldLogDirectly) viewModel.logFoodItemAsMeal(foodItem)
                    showFoodItemForm = false
                    editingFoodItem = null
                },
                onSmartParse = { description, callback ->
                    viewModel.parseFoodDescription(description, callback)
                },
                onScanLabel = { callback ->
                    viewModel.simulateScanLabel(callback)
                }
            )
        }
        
        if (showWaterDialog) {
            WaterLogSheet(
                onDismiss = { showWaterDialog = false },
                onConfirm = { amount ->
                    waterViewModel.addWater(amount)
                    showWaterDialog = false
                }
            )
        }

        if (showMealScanner) {
            MealPhotoScannerSheet(
                onDismiss = { showMealScanner = false },
                onResult = { description, cal, prot, carb, fat ->
                    // Logic to log the AI detected meal
                    viewModel.logMealFromFoodItem(
                        foodItem = com.heknot.app.data.local.database.entity.FoodItem(
                            name = description,
                            calories = cal,
                            protein = prot,
                            carbs = carb,
                            fat = fat,
                            servingSize = 100f, // Default serving size from AI scan
                            servingUnit = com.heknot.app.data.local.database.entity.ServingUnit.GRAMS
                        ),
                        servings = 1.0f,
                        mealType = com.heknot.app.data.local.database.entity.MealType.LUNCH // Default to lunch for simulator
                    )
                    showMealScanner = false
                }
            )
        }
    }
}



@Composable
fun DataCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun MacroBar(label: String, value: Float, goal: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(180.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(60.dp))
        Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier.fillMaxWidth((value / goal).coerceIn(0f, 1f)).fillMaxSize().background(color, RoundedCornerShape(4.dp)))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("${value.toInt()}g", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun QuickWaterChip(amount: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("+$amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExpandableFab(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onAddIngredient: () -> Unit,
    onAddMeal: () -> Unit,
    onAddWater: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                
                // 1. Water
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Agua", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(onClick = onAddWater, containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                        Icon(Icons.Default.WaterDrop, "Agua")
                    }
                }

                // 2. Ingredient
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ingrediente", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(onClick = onAddIngredient, containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Icon(Icons.Rounded.LocalDining, "Ingrediente")
                    }
                }
                
                // 3. Meal
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Comida", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(onClick = onAddMeal, containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Default.Fastfood, "Comida")
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Expandir menú"
            )
        }
    }
}
