package com.heknot.app.ui.screens.nutrition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.heknot.app.data.local.database.entity.FoodCategory
import com.heknot.app.data.local.database.entity.FoodItem
import com.heknot.app.data.local.database.entity.ServingUnit

import android.graphics.Bitmap

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.heknot.app.util.GenerativePixelArtGenerator
import com.heknot.app.util.GeminiNutritionParser
import com.heknot.app.util.ScannedNutrition
import androidx.compose.ui.graphics.asImageBitmap
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemFormSheet(
    existingItem: FoodItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem, Boolean) -> Unit,
    onSmartParse: (String, (FoodItem) -> Unit) -> Unit = { _, _ -> },
    onScanLabel: ((Int, Float, Float, Float) -> Unit) -> Unit = { _ -> }
) {
    val isEditMode = existingItem != null && existingItem.id != 0L
    
    // State
    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var calories by remember { mutableStateOf(existingItem?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(existingItem?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(existingItem?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(existingItem?.fat?.toString() ?: "") } // State var 'fat'

    // Extended Details
    var brand by remember { mutableStateOf(existingItem?.brand ?: "") }
    var showDetails by remember { mutableStateOf(false) } // Default collapsed

    // Smart Features State
    var smartDescription by remember { mutableStateOf("") }
    var isGeneratingPixelArt by remember { mutableStateOf(false) }
    var isAnalyzingScan by remember { mutableStateOf(false) }
    var showLabelScanner by remember { mutableStateOf(false) }
    var foodImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showVisualSelector by remember { mutableStateOf(false) }
    var showAIWorkshop by remember { mutableStateOf(false) }
    var workshopInitialBitmap by remember { mutableStateOf<Bitmap?>(null) }

    
    // Visual Identity State
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("") }
    
    // Pixel Art Logic
    var originalBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var pixelGridSize by remember { mutableFloatStateOf(12f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val geminiParser = remember { GeminiNutritionParser(context) }
    val pixelArtGenerator = remember { GenerativePixelArtGenerator(context) }

    
    // Detailed fields (defaulting if new)
    var selectedCategory by remember { mutableStateOf(existingItem?.category ?: FoodCategory.OTHER) }
    var servingSize by remember { mutableStateOf(existingItem?.servingSize?.toString() ?: "100") }
    var selectedUnit by remember { mutableStateOf(existingItem?.servingUnit ?: ServingUnit.GRAMS) }
    var fiber by remember { mutableStateOf(existingItem?.fiber?.toString() ?: "") }
    var sugar by remember { mutableStateOf(existingItem?.sugar?.toString() ?: "") }
    var sodium by remember { mutableStateOf(existingItem?.sodium?.toString() ?: "") }
    // Simulation logic for Pixel Art completion
    LaunchedEffect(isGeneratingPixelArt) {
        if (isGeneratingPixelArt) {
            kotlinx.coroutines.delay(3000)
            isGeneratingPixelArt = false
        }
    }

    // Colors
    val proteinColor = Color(0xFF4CAF50)
    val carbsColor = Color(0xFFFF9800)
    val fatColor = Color(0xFFF44336)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding() // Avoid keyboard overlap
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Initial breathing room

            // --- HEADER: IMAGE & IDENTITY ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Visual Avatar (Photo / Icon / Emoji)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .clickable { showVisualSelector = true },

                    contentAlignment = Alignment.Center
                ) {
                    if (isGeneratingPixelArt) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
                    } else if (foodImageBitmap != null) {
                        Image(
                            bitmap = foodImageBitmap!!,
                            contentDescription = "Food Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedEmoji.isNotEmpty()) {
                        Text(selectedEmoji, style = MaterialTheme.typography.displaySmall)
                    } else {
                        val icon = if (selectedCategory == FoodCategory.BEVERAGES) Icons.Default.WaterDrop else Icons.Rounded.Fastfood
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                    }
                    
                    // Edit Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp)
                    ) {
                         Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(10.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (isEditMode) "Editar Ingrediente" else "Nuevo Ingrediente", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Clasifica y define macros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // Pixel Grid Customization
                    if (originalBitmap != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Resolución: ${pixelGridSize.toInt()}px",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Slider(
                            value = pixelGridSize,
                            onValueChange = { 
                                pixelGridSize = it 
                                scope.launch {
                                    originalBitmap?.let { bitmap ->
                                        isGeneratingPixelArt = true
                                        foodImageBitmap = pixelArtGenerator.generatePixelArt(bitmap, it.toInt()).asImageBitmap()
                                        isGeneratingPixelArt = false
                                    }
                                }
                            },
                            valueRange = 16f..128f,
                            steps = 3 // 16, 32, 64, 128
                        )
                    }
                }
            }

            // --- SMART DESCRIPTION (AUTO-COMPLETE) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "✨ Autocompletar (Descripción)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = smartDescription,
                        onValueChange = { smartDescription = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Galleta Tosh de miel...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        trailingIcon = {
                             if (smartDescription.isNotEmpty()) {
                                 IconButton(onClick = {
                                     onSmartParse(smartDescription) { result ->
                                         name = result.name
                                         calories = result.calories.toString()
                                         protein = result.protein.toString()
                                         carbs = result.carbs.toString()
                                         fat = result.fat.toString()
                                         servingSize = result.servingSize.toString()
                                         isGeneratingPixelArt = true
                                     }
                                 }) {
                                     Icon(Icons.Default.AutoAwesome, "Generar", tint = MaterialTheme.colorScheme.primary)
                                 }
                             }
                        }
                    )
                }
            }

            // --- SCANNER SHORTCUT ---
            OutlinedButton(
                onClick = { showLabelScanner = true },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escanear Tabla Nutricional")
            }

            if (showLabelScanner) {
                LabelScannerSheet(
                    geminiParser = geminiParser,
                    onDismiss = { showLabelScanner = false },
                    onResult = { result ->
                        showLabelScanner = false
                        if (result.name != null) name = result.name
                        if (result.brand != null) brand = result.brand
                        if (result.calories > 0) calories = result.calories.toString()
                        if (result.protein > 0f) protein = result.protein.toString()
                        if (result.carbs > 0f) carbs = result.carbs.toString()
                        if (result.fat > 0f) fat = result.fat.toString()
                        if (result.sugar > 0f) sugar = result.sugar.toString()
                        if (result.fiber > 0f) fiber = result.fiber.toString()
                        if (result.sodium > 0f) sodium = result.sodium.toString()
                        if (result.servingSize > 0f) servingSize = result.servingSize.toString()
                        
                        // Map category string to enum
                        result.category?.let { catStr ->
                            try {
                                selectedCategory = com.heknot.app.data.local.database.entity.FoodCategory.valueOf(catStr.uppercase())
                            } catch (e: Exception) { /* ignore */ }
                        }
                    }
                )
            }

            if (isAnalyzingScan) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Gemini Nano analizando etiqueta...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (showVisualSelector) {
                VisualSelectorSheet(
                    onDismiss = { showVisualSelector = false },
                    onEmojiSelected = { 
                        selectedEmoji = it
                        foodImageBitmap = null
                        showVisualSelector = false
                    },
                    onPhotoCaptured = { bitmap ->
                        workshopInitialBitmap = bitmap
                        showVisualSelector = false
                        showAIWorkshop = true
                    }
                )
            }

            if (showAIWorkshop && workshopInitialBitmap != null) {
                AIWorkshopSheet(
                    initialBitmap = workshopInitialBitmap!!,
                    onDismiss = { showAIWorkshop = false },
                    onResult = { result ->
                        foodImageBitmap = result.asImageBitmap()
                        showAIWorkshop = false
                    }
                )
            }

            // --- BASIC FIELDS ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Calories
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                    label = { Text("Calorías") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Macros (Compact Row)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                
                // Helper Composable for Macros
                @Composable
                fun MacroField(label: String, valStr: String, color: Color, onValChange: (String) -> Unit) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = valStr,
                            onValueChange = onValChange,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
                        )
                    }
                }

                MacroField("Carbs", carbs, carbsColor) { carbs = it }
                MacroField("Prot", protein, proteinColor) { protein = it }
                MacroField("Grasa", fat, fatColor) { fat = it }
            }

            // --- EXPANDABLE DETAILS ---
            AnimatedVisibility(visible = showDetails) {
                 Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                         OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Marca (Opcional)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = servingSize,
                            onValueChange = { servingSize = it },
                            label = { Text("Porción (${selectedUnit.name})") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                             keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                   
                    // Micros
                     Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                         OutlinedTextField(value = fiber, onValueChange = { fiber = it }, label = { Text("Fibra") }, modifier = Modifier.weight(1f))
                         OutlinedTextField(value = sugar, onValueChange = { sugar = it }, label = { Text("Azúcar") }, modifier = Modifier.weight(1f))
                         OutlinedTextField(value = sodium, onValueChange = { sodium = it }, label = { Text("Sodio") }, modifier = Modifier.weight(1f))
                     }
                 }
            }
            
            TextButton(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (showDetails) "Menos detalles" else "Más detalles")
                Icon(if (showDetails) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null)
            }

            // --- ACTIONS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                         if (name.isNotEmpty() && calories.isNotEmpty()) {
                            val item = FoodItem(
                                id = existingItem?.id ?: 0L,
                                name = name,
                                calories = calories.toIntOrNull() ?: 0,
                                protein = protein.toFloatOrNull() ?: 0f,
                                carbs = carbs.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f,
                                brand = brand,
                                category = selectedCategory,
                                servingSize = servingSize.toFloatOrNull() ?: 100f,
                                servingUnit = selectedUnit,
                                fiber = fiber.toFloatOrNull() ?: 0f,
                                sugar = sugar.toFloatOrNull() ?: 0f,
                                sodium = sodium.toFloatOrNull() ?: 0f
                            )
                            onConfirm(item, false) // Save only
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Text("Guardar")
                }

                Button(
                    onClick = {
                        if (name.isNotEmpty() && calories.isNotEmpty()) {
                            val item = FoodItem(
                                id = existingItem?.id ?: 0L,
                                name = name,
                                calories = calories.toIntOrNull() ?: 0,
                                protein = protein.toFloatOrNull() ?: 0f,
                                carbs = carbs.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f,
                                brand = brand,
                                category = selectedCategory,
                                servingSize = servingSize.toFloatOrNull() ?: 100f,
                                servingUnit = selectedUnit,
                                fiber = fiber.toFloatOrNull() ?: 0f,
                                sugar = sugar.toFloatOrNull() ?: 0f,
                                sodium = sodium.toFloatOrNull() ?: 0f
                            )
                            onConfirm(item, true) // Save & Log
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Registrar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealOptionsSheet(
    onDismiss: () -> Unit,
    onNavigateToMealPhoto: () -> Unit,
    onNavigateToManualBuilder: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Registrar Comida",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // 1. Photo / AI Estimate (Meals)
            Card(
                onClick = onNavigateToMealPhoto,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Escanear / Foto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Toma una foto a tu plato",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 2. Manual / Recipe
            OutlinedButton(
                onClick = onNavigateToManualBuilder,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(Icons.Rounded.LocalDining, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Armar Manualmente")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualSelectorSheet(
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        QuickCaptureSheet(
            onDismiss = { showCamera = false },
            onCaptured = { bitmap ->
                onPhotoCaptured(bitmap)
                showCamera = false
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Identidad Visual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VisualOptionCard(
                    icon = Icons.Default.CameraAlt,
                    label = "Cámara",
                    modifier = Modifier.weight(1f),
                    onClick = { showCamera = true }
                )
                VisualOptionCard(
                    icon = Icons.Default.Image,
                    label = "Galería",
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO */ }
                )
            }

            Text("Emojis Rápidos", style = MaterialTheme.typography.labelMedium)
            val emojis = listOf("🍎", "🥤", "🍖", "🥦", "🍕", "🍰", "🍚", "🥑")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                emojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onEmojiSelected(emoji) }
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun VisualOptionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCaptureSheet(onDismiss: () -> Unit, onCaptured: (Bitmap) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Box(modifier = Modifier.fillMaxWidth().height(450.dp).background(Color.Black)) {
            var pView: PreviewView? by remember { mutableStateOf(null) }
            
            CameraPreview(
                flashEnabled = false,
                onPreviewViewCreated = { pView = it },
                onTextFound = {}
            )
            
            // Reticle
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .align(Alignment.Center)
            )

            Button(
                onClick = { pView?.bitmap?.let { onCaptured(it) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(48.dp)
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Capturar Item")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
