package com.heknot.app.ui.screens.nutrition

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heknot.app.data.local.database.entity.FoodCategory
import com.heknot.app.data.local.database.entity.FoodItem
import com.heknot.app.data.local.database.entity.ServingUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemFormSheet(
    existingItem: FoodItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Identificadores de colores para darle vida
    val proteinColor = Color(0xFF4CAF50)
    val carbsColor = Color(0xFFFF9800)
    val fatColor = Color(0xFFF44336)
    
    // Local state for form fields
    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var brand by remember { mutableStateOf(existingItem?.brand ?: "") }
    
    var selectedCategory by remember { mutableStateOf(existingItem?.category ?: FoodCategory.OTHER) }
    var categoryExpanded by remember { mutableStateOf(false) }
    
    var servingSize by remember { mutableStateOf(existingItem?.servingSize?.toString() ?: "100") }
    var selectedUnit by remember { mutableStateOf(existingItem?.servingUnit ?: ServingUnit.GRAMS) }
    var unitExpanded by remember { mutableStateOf(false) }

    // Toggle: Input Per 100g vs Per Serving
    // Default to Per Serving unless explicitly requested? Actually per 100g is very common.
    var isPer100gMode by remember { mutableStateOf(false) }
    
    // Macros
    var calories by remember { mutableStateOf(existingItem?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(existingItem?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(existingItem?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(existingItem?.fat?.toString() ?: "") }

    var fiber by remember { mutableStateOf(existingItem?.fiber?.toString() ?: "") }
    var sugar by remember { mutableStateOf(existingItem?.sugar?.toString() ?: "") }
    var sodium by remember { mutableStateOf(existingItem?.sodium?.toString() ?: "") }

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
            // Header con estilo - Más limpio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (existingItem != null) "Editar Producto" else "Nuevo Producto",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        "Información nutricional",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), MaterialTheme.shapes.extraLarge)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- SECCIÓN 1: IDENTIDAD (CARD) ---
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FormSectionLabel("DETALLES DEL PRODUCTO", Icons.Rounded.Fastfood)
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre") },
                            placeholder = { Text("Ej. Avena Quaker") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = MaterialTheme.shapes.medium
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = brand,
                                onValueChange = { brand = it },
                                label = { Text("Marca") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                shape = MaterialTheme.shapes.medium
                            )

                            // Category Selector
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedCategory.name, 
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Categoría") },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { categoryExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    FoodCategory.values().forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category.name) },
                                            onClick = {
                                                selectedCategory = category
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // --- SECCIÓN 2: VALORES NUTRICIONALES (CARD ESTILO ETIQUETA) ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)), // Borde negro grueso fake
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header Etiqueta
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Nutrition Facts",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            // Toggle 100g
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isPer100gMode) "100g/ml" else "Porción",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(
                                    checked = isPer100gMode,
                                    onCheckedChange = { isPer100gMode = it },
                                    modifier = Modifier.scale(0.8f) // Custom fun needed or just standard size
                                )
                            }
                        }
                        
                        Divider(thickness = 4.dp, color = MaterialTheme.colorScheme.onSurface)

                        // Serving Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                             Text(
                                "Tamaño Porción",
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = servingSize,
                                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) servingSize = it },
                                    modifier = Modifier.width(80.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Unit
                                Box(modifier = Modifier.width(100.dp)) {
                                    Text(
                                        text = selectedUnit.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { unitExpanded = true }
                                            .padding(8.dp),
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Bold
                                    )
                                    DropdownMenu(
                                        expanded = unitExpanded,
                                        onDismissRequest = { unitExpanded = false }
                                    ) {
                                        ServingUnit.values().forEach { unit ->
                                            DropdownMenuItem(
                                                text = { Text(unit.name) },
                                                onClick = {
                                                    selectedUnit = unit
                                                    unitExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
                        
                        // Calories row
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Amount per serving", style = MaterialTheme.typography.labelSmall)
                                Text("Calories", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                            }
                            OutlinedTextField(
                                value = calories,
                                onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, textAlign = TextAlign.End),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }

                        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)

                        // Macros rows (Label style)
                        NutriRow("Total Fat", fat, "g", { fat = it }, isBold = true)
                        NutriRow("Total Carbohydrate", carbs, "g", { carbs = it }, isBold = true)
                        NutriRow("   Dietary Fiber", fiber, "g", { fiber = it })
                        NutriRow("   Total Sugars", sugar, "g", { sugar = it })
                        NutriRow("Protein", protein, "g", { protein = it }, isBold = true)
                        
                        Divider(thickness = 4.dp, color = MaterialTheme.colorScheme.onSurface)
                        
                        NutriRow("Sodium", sodium, "mg", { sodium = it }, isBold = true)
                    }
                }
                
                // Helper text
                if (isPer100gMode) {
                   Text(
                       "⚠️ Ingresando datos por 100g/ml. Se calcularán automáticamente para la porción de $servingSize ${selectedUnit.name}.",
                       style = MaterialTheme.typography.bodySmall,
                       color = MaterialTheme.colorScheme.tertiary
                   )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        if (name.isNotBlank() && calories.isNotBlank()) {
                            // Logic to handle Per 100g vs Per Serving conversion
                            val servingFactor = if (isPer100gMode) {
                                (servingSize.toFloatOrNull() ?: 100f) / 100f
                            } else {
                                1f
                            }
                            
                            val newItem = FoodItem(
                                id = existingItem?.id ?: 0,
                                name = name,
                                brand = brand.takeIf { it.isNotBlank() },
                                category = selectedCategory,
                                servingSize = servingSize.toFloatOrNull() ?: 100f,
                                servingUnit = selectedUnit,
                                calories = ((calories.toIntOrNull() ?: 0) * servingFactor).toInt(),
                                protein = (protein.toFloatOrNull() ?: 0f) * servingFactor,
                                carbs = (carbs.toFloatOrNull() ?: 0f) * servingFactor,
                                fat = (fat.toFloatOrNull() ?: 0f) * servingFactor,
                                fiber = (fiber.toFloatOrNull() ?: 0f) * servingFactor,
                                sugar = (sugar.toFloatOrNull() ?: 0f) * servingFactor,
                                sodium = (sodium.toFloatOrNull() ?: 0f) * servingFactor,
                                isCustom = true,
                                createdAt = existingItem?.createdAt ?: System.currentTimeMillis()
                            )
                            onConfirm(newItem)
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    enabled = name.isNotBlank() && calories.isNotBlank(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (existingItem != null) "Actualizar" else "Guardar")
                }
            }
        }
    }
}

@Composable
fun FormSectionLabel(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun NutriRow(
    label: String, 
    value: String, 
    unit: String, 
    onValueChange: (String) -> Unit, 
    isBold: Boolean = false
) {
    Column {
        Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), // More breathing room
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label, 
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Invisible generic text field that looks like plain text editing
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) onValueChange(it) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.End,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
