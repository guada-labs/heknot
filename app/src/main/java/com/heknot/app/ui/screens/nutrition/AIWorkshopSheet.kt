package com.heknot.app.ui.screens.nutrition

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heknot.app.util.GenerativePixelArtGenerator
import com.heknot.app.util.ImageUtils
import kotlinx.coroutines.launch

enum class WorkshopStep {
    EDIT,
    CLEANING,
    SYNTHESIS,
    DONE
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AIWorkshopSheet(
    initialBitmap: Bitmap,
    onDismiss: () -> Unit,
    onResult: (Bitmap) -> Unit
) {
    var currentStep by remember { mutableStateOf(WorkshopStep.EDIT) }
    var currentBitmap by remember { mutableStateOf(initialBitmap) }
    var isProcessing by remember { mutableStateOf(false) }
    var pixelSize by remember { mutableFloatStateOf(64f) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val generator = remember { GenerativePixelArtGenerator(context) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = when (currentStep) {
                    WorkshopStep.EDIT -> "Paso 1: Preparar Foto"
                    WorkshopStep.CLEANING -> "Paso 2: Limpiando Plato"
                    WorkshopStep.SYNTHESIS -> "Paso 3: Generando Icono"
                    WorkshopStep.DONE -> "¡Icono Listo!"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Preview Area
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(if (currentStep == WorkshopStep.CLEANING || currentStep == WorkshopStep.SYNTHESIS) Color.LightGray.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Checkered Background for Transparency
                if (currentStep == WorkshopStep.CLEANING || currentStep == WorkshopStep.SYNTHESIS) {
                    CheckeredBackground(Modifier.fillMaxSize())
                }

                Image(
                    bitmap = currentBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = if (currentStep == WorkshopStep.EDIT) ContentScale.Fit else ContentScale.Fit
                )

                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

            // Controls
            AnimatedContent(targetState = currentStep) { step ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        WorkshopStep.EDIT -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                IconButton(
                                    onClick = { currentBitmap = ImageUtils.rotate(currentBitmap, 90f) },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                ) {
                                    Icon(Icons.Rounded.RotateRight, "Girar")
                                }
                                Button(
                                    onClick = { currentStep = WorkshopStep.CLEANING },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.CleaningServices, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Quitar Fondo")
                                }
                            }
                        }
                        WorkshopStep.CLEANING -> {
                            Text(
                                "Detectando el plato para aislarlo del fondo...",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        isProcessing = true
                                        currentBitmap = generator.removeBackground(currentBitmap)
                                        isProcessing = false
                                        currentStep = WorkshopStep.SYNTHESIS
                                    }
                                },
                                enabled = !isProcessing,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Procesar Limpieza")
                            }
                        }
                        WorkshopStep.SYNTHESIS -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Resolución: ${pixelSize.toInt()}px", style = MaterialTheme.typography.labelMedium)
                                Slider(
                                    value = pixelSize,
                                    onValueChange = { pixelSize = it },
                                    valueRange = 16f..128f,
                                    steps = 3,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isProcessing = true
                                            currentBitmap = generator.generatePixelArt(currentBitmap, pixelSize.toInt())
                                            isProcessing = false
                                            currentStep = WorkshopStep.DONE
                                        }
                                    },
                                    enabled = !isProcessing,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generar Pixel Art")
                                }
                            }
                        }
                        WorkshopStep.DONE -> {
                            Button(
                                onClick = { onResult(currentBitmap) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Check, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Usar este Icono")
                            }
                            TextButton(onClick = { currentStep = WorkshopStep.EDIT }) {
                                Text("Volver a empezar")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CheckeredBackground(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val size = 20.dp.toPx()
        val numCols = (this.size.width / size).toInt() + 1
        val numRows = (this.size.height / size).toInt() + 1
        
        for (i in 0 until numCols) {
            for (j in 0 until numRows) {
                if ((i + j) % 2 == 0) {
                    drawRect(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        topLeft = androidx.compose.ui.geometry.Offset(i * size, j * size),
                        size = androidx.compose.ui.geometry.Size(size, size)
                    )
                }
            }
        }
    }
}
