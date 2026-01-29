package com.fittrack.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.app.data.repository.FitTrackRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    fun resetApp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.resetData()
            onSuccess()
        }
    }
}
