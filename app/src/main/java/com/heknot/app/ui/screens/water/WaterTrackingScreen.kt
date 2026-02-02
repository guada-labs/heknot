package com.heknot.app.ui.screens.water

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.ui.AppViewModelProvider
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingScreen(
    viewModel: WaterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val currentTotal by viewModel.currentTotal.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val todaysLogs by viewModel.todaysLogs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "WaterProgress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hidratación", fontWeight = FontWeight.Black) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- MAIN PROGRESS CIRCLE ---
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(280.dp)
                ) {
                    // Background Circle
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 24.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                    
                    // Progress Circle
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 24.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                    
                    // Center Text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.WaterDrop, 
                            contentDescription = null, 
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$currentTotal",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "/ $dailyGoal ml",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // --- QUICK ADD ACTIONS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAddButton(amount = 250, onClick = { viewModel.addWater(250) })
                    QuickAddButton(amount = 500, onClick = { viewModel.addWater(500) })
                    QuickAddButton(amount = 1000, onClick = { viewModel.addWater(1000) })
                }
            }
            
            // --- RECENT LOGS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Historial de hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (todaysLogs.isEmpty()) {
                item {
                    Text(
                        "No has tomado agua hoy.\n¡Mantente hidratado!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                items(todaysLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalDrink, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${log.amountMl} ml",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Registrado hoy", // TODO: Add time if available or just generic
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.deleteWaterLog(log) }) {
                                Icon(Icons.Rounded.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun QuickAddButton(amount: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
             contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp) 
        ) {
            Icon(Icons.Default.Add, null)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("+$amount ml", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
