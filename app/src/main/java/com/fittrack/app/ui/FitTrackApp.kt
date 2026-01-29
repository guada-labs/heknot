package com.fittrack.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.fittrack.app.ui.navigation.FitTrackNavGraph
import com.fittrack.app.ui.navigation.Screen
import com.fittrack.app.ui.theme.FitTrackTheme
import com.fittrack.app.ui.AppViewModelProvider
import com.fittrack.app.ui.MainViewModel

@Composable
fun FitTrackApp(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isOnboardedState by viewModel.isUserOnboarded.collectAsState()

    // Esperar a que se cargue el estado (no mostrar nada o splash si es null)
    if (isOnboardedState == null) return 

    val startDestination = if (isOnboardedState == true) Screen.Home.route else Screen.Onboarding.route

    FitTrackTheme {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            FitTrackNavGraph(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
