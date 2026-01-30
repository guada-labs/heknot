package com.heknot.app.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heknot.app.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
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
                title = { 
                    Text(
                        "Configuración", 
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
        val isDarkMode by viewModel.isDarkMode.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            // --- Sección de Interfaz ---
            Text(
                text = "Personalización",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Tema de la aplicación",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Opción: CLARO
                    SegmentedButton(
                        selected = isDarkMode == false,
                        onClick = { viewModel.setDarkMode(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                        icon = { Icon(Icons.Default.LightMode, contentDescription = null) }
                    ) {
                        Text("Claro")
                    }
                    
                    // Opción: SISTEMA
                    SegmentedButton(
                        selected = isDarkMode == null,
                        onClick = { viewModel.setDarkMode(null) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                        icon = { Icon(Icons.Default.BrightnessAuto, contentDescription = null) }
                    ) {
                        Text("Auto")
                    }
                    
                    // Opción: OSCURO
                    SegmentedButton(
                        selected = isDarkMode == true,
                        onClick = { viewModel.setDarkMode(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                        icon = { Icon(Icons.Default.DarkMode, contentDescription = null) }
                    ) {
                        Text("Oscuro")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val biometricEnabled by viewModel.biometricEnabled.collectAsState()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Seguridad Biométrica",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Solicitar huella o rostro al abrir la app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = { viewModel.setBiometricEnabled(it) }
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
                onClick = { exportLauncher.launch("heknot_Backup_${System.currentTimeMillis()}.json") },
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
