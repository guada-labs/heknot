package com.heknot.app.ui.screens.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.ui.AppViewModelProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.text.font.FontWeight
import com.patrykandpatrick.vico.compose.common.insets
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    onBack: (() -> Unit)? = null,
    viewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Date Formatter for X axis (dd/MM)
    val dateValueFormatter = CartesianValueFormatter { _, x, _ ->
        LocalDate.ofEpochDay(x.toLong()).format(DateTimeFormatter.ofPattern("dd/MM"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mi Progreso", 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            // Stats Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCard(
                    title = "Cambio Total",
                    value = if (uiState.totalChange > 0) "+${String.format("%.1f", uiState.totalChange)}" else String.format("%.1f", uiState.totalChange),
                    params = "kg",
                    isPositive = uiState.totalChange <= 0,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.size(16.dp))
                
                StatCard(
                    title = "Peso Actual",
                    value = uiState.currentWeight.toString(),
                    params = "kg",
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.isLoading) {
                Text("Cargando gráfica...")
            } else if (uiState.isEmpty) {
                Text("No hay suficientes datos para mostrar la gráfica.")
            } else {
            // Vico 2.0 Chart with Premium Styling
    Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary
                    
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(
                                lineProvider = LineCartesianLayer.LineProvider.series(
                                    LineCartesianLayer.rememberLine(
                                        fill = LineCartesianLayer.LineFill.single(fill(primaryColor)),
                                        stroke = LineCartesianLayer.LineStroke.Continuous(),
                                        areaFill = LineCartesianLayer.AreaFill.single(fill(primaryColor.copy(alpha = 0.15f)))
                                    ),
                                    LineCartesianLayer.rememberLine(
                                        fill = LineCartesianLayer.LineFill.single(fill(secondaryColor.copy(alpha = 0.5f))),
                                        stroke = LineCartesianLayer.LineStroke.Continuous(),
                                        areaFill = LineCartesianLayer.AreaFill.single(fill(secondaryColor.copy(alpha = 0.1f)))
                                    )
                                )
                            ),
                            startAxis = VerticalAxis.rememberStart(
                                label = rememberTextComponent(
                                    color = onSurfaceColor
                                ),
                                itemPlacer = VerticalAxis.ItemPlacer.step(),
                                line = rememberLineComponent(fill = fill(onSurfaceColor.copy(alpha = 0.2f))),
                                guideline = rememberLineComponent(fill = fill(onSurfaceColor.copy(alpha = 0.05f)))
                            ),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                label = rememberTextComponent(
                                    color = onSurfaceColor,
                                    padding = insets(horizontal = 4.dp)
                                ),
                                line = rememberLineComponent(fill = fill(onSurfaceColor.copy(alpha = 0.2f))),
                                valueFormatter = dateValueFormatter,
                                itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                                    spacing = { 3 },
                                    addExtremeLabelPadding = true
                                )
                            )
                        ),
                        modelProducer = viewModel.modelProducer,
                        scrollState = rememberVicoScrollState(),
                        zoomState = rememberVicoZoomState(initialZoom = Zoom.max(Zoom.fixed(), Zoom.Content)),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Resumen Semanal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Secondary Stats Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Agua (Media)",
                        value = "${uiState.weeklyWaterAvg}",
                        params = "/ ${uiState.waterGoal} ml",
                        isPositive = uiState.weeklyWaterAvg >= uiState.waterGoal * 0.8f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    StatCard(
                        title = "Entrenamientos",
                        value = "${uiState.workoutFrequency}",
                        params = "sesiones",
                        isPositive = uiState.workoutFrequency >= 3,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                StatCard(
                    title = "Velocidad de Peso (7d)",
                    value = (if (uiState.weightVelocity > 0) "+" else "") + String.format("%.2f", uiState.weightVelocity),
                    params = "kg/semana",
                    isPositive = uiState.weightVelocity <= 0,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    params: String,
    isPositive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isPositive) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
            else MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = " $params",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
