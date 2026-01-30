package com.heknot.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heknot.app.data.repository.HeknotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    repository: HeknotRepository
) : ViewModel() {

    // Verifica si hay un perfil de usuario guardado (id=1)
    val isUserOnboarded: StateFlow<Boolean?> = repository.getUserProfile()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // null = Cargando / Desconocido
        )

    val isDarkMode: StateFlow<Boolean?> = repository.getUserProfile()
        .map { it?.isDarkMode } // null = seguir sistema
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
