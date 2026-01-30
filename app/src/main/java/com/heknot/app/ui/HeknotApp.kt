package com.heknot.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.heknot.app.ui.navigation.HeknotNavGraph
import com.heknot.app.ui.navigation.Screen
import com.heknot.app.ui.theme.HeknotTheme
import com.heknot.app.ui.AppViewModelProvider
import com.heknot.app.ui.MainViewModel

@Composable
fun HeknotApp(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isOnboardedState by viewModel.isUserOnboarded.collectAsState()
    val userDarkModePref by viewModel.isDarkMode.collectAsState()
    val isDarkMode = userDarkModePref ?: androidx.compose.foundation.isSystemInDarkTheme()

    // Esperar a que se cargue el estado (no mostrar nada o splash si es null)
    if (isOnboardedState == null) return 

    val startDestination = if (isOnboardedState == true) "main" else Screen.Onboarding.route

    HeknotTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        HeknotNavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}
