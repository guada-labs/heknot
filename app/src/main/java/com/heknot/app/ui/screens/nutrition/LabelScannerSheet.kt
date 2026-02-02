package com.heknot.app.ui.screens.nutrition

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.heknot.app.util.GeminiNutritionParser
import com.heknot.app.util.ScannedNutrition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

enum class ScannerStep {
    CAMERA,
    REVIEW_OCR,
    REVIEW_LLM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScannerSheet(
    geminiParser: GeminiNutritionParser,
    onDismiss: () -> Unit,
    onResult: (ScannedNutrition) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var currentStep by remember { mutableStateOf(ScannerStep.CAMERA) }
    var currentFullText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf(ScannedNutrition()) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { if (currentStep == ScannerStep.CAMERA) BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasCameraPermission) {
                Text("Se requiere permiso de cámara para escanear.")
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Dar Permiso")
                }
            } else {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when(currentStep) {
                            ScannerStep.CAMERA -> "Escanear Tabla"
                            ScannerStep.REVIEW_OCR -> "Revisar Texto OCR"
                            ScannerStep.REVIEW_LLM -> "Resultados Gemini"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                    
                    if (currentStep != ScannerStep.CAMERA && !isAnalyzing) {
                        IconButton(onClick = { 
                            if (currentStep == ScannerStep.REVIEW_LLM) currentStep = ScannerStep.REVIEW_OCR
                            else currentStep = ScannerStep.CAMERA 
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    } else if (currentStep == ScannerStep.CAMERA) {
                        IconButton(onClick = { flashEnabled = !flashEnabled }) {
                            Icon(
                                if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash"
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "ScannerStepTransition"
                ) { step ->
                    when (step) {
                        ScannerStep.CAMERA -> CameraStepLayout(
                            flashEnabled = flashEnabled,
                            onPreviewViewCreated = { previewView = it },
                            onCapture = {
                                previewView?.bitmap?.let { bitmap ->
                                    isAnalyzing = true
                                    processImageForOcr(bitmap) { text ->
                                        currentFullText = text
                                        isAnalyzing = false
                                        currentStep = ScannerStep.REVIEW_OCR
                                    }
                                }
                            },
                            isLoading = isAnalyzing
                        )
                        ScannerStep.REVIEW_OCR -> ReviewOcrStepLayout(
                            text = currentFullText,
                            onAnalyze = {
                                isAnalyzing = true
                                scope.launch {
                                    parsedResult = geminiParser.parseNutritionText(currentFullText)
                                    isAnalyzing = false
                                    currentStep = ScannerStep.REVIEW_LLM
                                }
                            },
                            isLoading = isAnalyzing
                        )
                        ScannerStep.REVIEW_LLM -> ReviewLlmStepLayout(
                            result = parsedResult,
                            onApply = { onResult(parsedResult) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CameraStepLayout(
    flashEnabled: Boolean,
    onPreviewViewCreated: (PreviewView) -> Unit,
    onCapture: () -> Unit,
    isLoading: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp) // Increased height for S24 Ultra and long labels
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CameraPreview(
                flashEnabled = flashEnabled,
                onPreviewViewCreated = onPreviewViewCreated,
                onTextFound = {}
            )
            
            // Scanning Overlay (Rectangular Green Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Wider
                    .height(280.dp)    // Rectangular
                    .border(2.dp, Color.Green.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            )

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        Button(
            onClick = onCapture,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.PhotoCamera, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Capturar y Ver Texto")
        }
    }
}

@Composable
fun ReviewOcrStepLayout(
    text: String,
    onAnalyze: () -> Unit,
    isLoading: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text(
                    "Texto Detectado:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text.ifBlank { "No se detectó texto. Intenta de nuevo." },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Gemini Nano analizando...", style = MaterialTheme.typography.labelSmall)
        }

        Button(
            onClick = onAnalyze,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = text.isNotBlank() && !isLoading
        ) {
            Icon(Icons.Default.AutoAwesome, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Extraer con Gemini Nano")
        }
    }
}

@Composable
fun ReviewLlmStepLayout(
    result: ScannedNutrition,
    onApply: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (result.name != null) {
                    Text("Producto: ${result.name}", fontWeight = FontWeight.Bold)
                }
                if (result.brand != null) {
                    Text("Marca: ${result.brand}", style = MaterialTheme.typography.bodySmall)
                }
                
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoChip("Cal", result.calories.toString())
                    InfoChip("Prot", "${result.protein}g")
                    InfoChip("Carb", "${result.carbs}g")
                    InfoChip("Fat", "${result.fat}g")
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoChip("Porción", "${result.servingSize}${result.servingUnit}")
                    InfoChip("Fibra", "${result.fiber}g")
                    InfoChip("Azúcar", "${result.sugar}g")
                    InfoChip("Sodio", "${result.sodium}mg")
                }
            }
        }

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aplicar al Formulario")
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

private fun processImageForOcr(bitmap: android.graphics.Bitmap, onTextFound: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bitmap, 0)
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            onTextFound(visionText.text)
        }
        .addOnFailureListener {
            onTextFound("")
        }
}

@Composable
fun CameraPreview(
    flashEnabled: Boolean,
    onPreviewViewCreated: (PreviewView) -> Unit,
    onTextFound: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                onPreviewViewCreated(this)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, TextAnalyzer(onTextFound))
                    }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                    camera.cameraControl.enableTorch(flashEnabled)
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

private class TextAnalyzer(private val onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onTextFound(visionText.text)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
