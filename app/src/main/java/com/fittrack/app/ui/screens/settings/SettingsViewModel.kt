package com.fittrack.app.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.local.backup.BackupManager
import com.fittrack.app.data.repository.FitTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: FitTrackRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val backupStatus: StateFlow<BackupStatus> = _backupStatus

    val isDarkMode: StateFlow<Boolean?> = repository.getUserProfile()
        .map { it?.isDarkMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setDarkMode(enabled: Boolean?) {
        viewModelScope.launch {
            repository.updateDarkMode(enabled)
        }
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Loading
            val result = backupManager.exportBackup(uri)
            if (result.isSuccess) {
                _backupStatus.value = BackupStatus.Success("Backup exportado correctamente")
            } else {
                _backupStatus.value = BackupStatus.Error("Error al exportar: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Loading
            val result = backupManager.importBackup(uri)
            if (result.isSuccess) {
                _backupStatus.value = BackupStatus.Success("Backup importado. Los datos se han mezclado.")
            } else {
                _backupStatus.value = BackupStatus.Error("Error al importar: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun clearStatus() {
        _backupStatus.value = BackupStatus.Idle
    }

    fun resetApp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.resetData()
            onSuccess()
        }
    }
}

sealed class BackupStatus {
    object Idle : BackupStatus()
    object Loading : BackupStatus()
    data class Success(val message: String) : BackupStatus()
    data class Error(val message: String) : BackupStatus()
}
