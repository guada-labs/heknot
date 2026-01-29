package com.fittrack.app.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fittrack.app.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onRestartApp: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val backupStatus by viewModel.backupStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher para Exportar
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }

    // Launcher para Importar
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importBackup(it) }
    }

    LaunchedEffect(backupStatus) {
        when (val status = backupStatus) {
            is BackupStatus.Success -> {
                snackbarHostState.showSnackbar(status.message)
                viewModel.clearStatus()
            }
            is BackupStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)
                viewModel.clearStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        val isDarkMode by viewModel.isDarkMode.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            
            // --- Sección de Interfaz ---
            Text(
                text = "Interfaz",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Seguir Sistema
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.BrightnessAuto, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tema del Sistema")
                }
                Switch(
                    checked = isDarkMode == null,
                    onCheckedChange = { if (it) viewModel.setDarkMode(null) else viewModel.setDarkMode(true) }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forzar Modo Oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = if (isDarkMode != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Modo Oscuro Permanente",
                        color = if (isDarkMode != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                Switch(
                    checked = isDarkMode ?: true,
                    onCheckedChange = { viewModel.setDarkMode(it) },
                    enabled = isDarkMode != null
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // --- Sección de Backup ---
            Text(
                text = "Copia de Seguridad (Privacidad)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Exporta tus datos a un archivo JSON para tener una copia física fuera de la app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { exportLauncher.launch("FitTrack_Backup_${System.currentTimeMillis()}.json") },
                modifier = Modifier.fillMaxWidth(),
                enabled = backupStatus !is BackupStatus.Loading
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar Datos (JSON)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json", "application/octet-stream")) },
                modifier = Modifier.fillMaxWidth(),
                enabled = backupStatus !is BackupStatus.Loading
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Importar Datos (JSON)")
            }

            if (backupStatus is BackupStatus.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // --- Sección de Peligro ---
            Text(
                text = "Zona de Peligro",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    viewModel.resetApp(onSuccess = onRestartApp)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Borrar Todo y Reiniciar")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Esto eliminará tu perfil, todos los registros de peso, ejercicios y comidas. No se puede deshacer.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
