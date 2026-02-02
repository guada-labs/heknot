package com.heknot.app.ui.screens.nutrition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterLogSheet(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var waterAmount by remember { mutableStateOf(250) }
    var showManualInput by remember { mutableStateOf(false) }
    val maxLimit = 3000 // UI Max for the radial
    
    // Water-themed colors
    val waterBlue = Color(0xFF00B2FF)
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp)
                .navigationBarsPadding() // Respect system navigation bars
                .verticalScroll(rememberScrollState()) // Allow scrolling if needed
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                "Hidratación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Circular Slider
            CircularWaterSlider(
                value = waterAmount,
                maxValue = maxLimit,
                onValueChange = { waterAmount = it },
                color = waterBlue,
                modifier = Modifier.size(260.dp),
                onTextClick = { showManualInput = true }
            )

            // Quick Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(250, 500, 1000).forEach { amount ->
                    OutlinedButton(
                        onClick = { waterAmount = amount },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.dp,
                            color = if (waterAmount == amount) waterBlue else MaterialTheme.colorScheme.outlineVariant
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (waterAmount == amount) waterBlue.copy(alpha = 0.1f) else Color.Transparent
                        )
                    ) {
                        Text("${amount}ml", color = if (waterAmount == amount) waterBlue else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Confirm Action
            Button(
                onClick = { onConfirm(waterAmount) },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = waterBlue)
            ) {
                Icon(Icons.Default.WaterDrop, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Registrar ${waterAmount}ml", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Manual Input Dialog
    if (showManualInput) {
        var tempAmount by remember { mutableStateOf(waterAmount.toString()) }
        AlertDialog(
            onDismissRequest = { showManualInput = false },
            title = { Text("Cantidad exacta") },
            text = {
                OutlinedTextField(
                    value = tempAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tempAmount = it },
                    suffix = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    tempAmount.toIntOrNull()?.let { waterAmount = it }
                    showManualInput = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showManualInput = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun CircularWaterSlider(
    value: Int,
    maxValue: Int,
    color: Color,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onTextClick: () -> Unit
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val sweepAngle = 260f
    val startAngle = 140f
    
    val currentAngle = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) * sweepAngle
    
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val touchPoint = change.position
                        var angle = atan2(touchPoint.y - center.y, touchPoint.x - center.x) * (180f / PI.toFloat())
                        angle = (angle - startAngle + 360) % 360
                        val progress = (angle / sweepAngle).coerceIn(0f, 1.1f) // Slight overflow allowed for UX
                        if (progress <= 1.05f) {
                            val newValue = (progress.coerceIn(0f, 1f) * maxValue).roundToInt()
                            onValueChange((newValue / 50) * 50) // Snap to 50ml
                        }
                        change.consume()
                    }
                }
        ) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.minDimension - strokeWidth * 2) / 2f
            val arcSize = Size(radius * 2, radius * 2)
            val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)

            // 1. Background Track
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )

            // 2. Ticks (Premium Scoring)
            val tickCount = 60
            for (i in 0..tickCount) {
                val tickAngleDeg = startAngle + (i.toFloat() / tickCount) * sweepAngle
                val tickAngleRad = tickAngleDeg * (PI.toFloat() / 180f)
                val innerRadius = radius - strokeWidth - 4.dp.toPx()
                val isMajor = i % 10 == 0
                val tickLen = if (isMajor) 12.dp.toPx() else 6.dp.toPx()
                
                val start = Offset(center.x + innerRadius * cos(tickAngleRad), center.y + innerRadius * sin(tickAngleRad))
                val end = Offset(center.x + (innerRadius - tickLen) * cos(tickAngleRad), center.y + (innerRadius - tickLen) * sin(tickAngleRad))
                
                drawLine(
                    color = if (i.toFloat() / tickCount <= value.toFloat() / maxValue) color else trackColor,
                    start = start,
                    end = end,
                    strokeWidth = if (isMajor) 2.5.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 3. Progress arc
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = currentAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )

            // 4. Thumb with Glow
            val thumbAngleRad = (startAngle + currentAngle) * (PI.toFloat() / 180f)
            val thumbPos = Offset(center.x + radius * cos(thumbAngleRad), center.y + radius * sin(thumbAngleRad))
            
            drawCircle(color = color.copy(alpha = 0.3f), radius = 22.dp.toPx(), center = thumbPos)
            drawCircle(color = Color.White, radius = 14.dp.toPx(), center = thumbPos)
            drawCircle(color = color, radius = 10.dp.toPx(), center = thumbPos)
        }

        // 5. Center Content (Clickable)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onTextClick)
                .padding(24.dp)
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = "ml",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = color.copy(alpha = 0.5f))
        }
    }
}

