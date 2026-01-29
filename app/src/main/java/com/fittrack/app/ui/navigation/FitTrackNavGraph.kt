package com.fittrack.app.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fittrack.app.ui.screens.home.HomeScreen
import com.fittrack.app.ui.screens.onboarding.OnboardingScreen

@Composable
fun FitTrackNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Determinar destino inicial (luego agregaremos lógica para verificar si ya completó onboarding)
    val startDestination = Screen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Limpiar stack para no volver al onboarding
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.History.route) {
            Text("History Screen Placeholder")
        }
        
        composable(Screen.Chart.route) {
            Text("Chart Screen Placeholder")
        }
        
        composable(Screen.Settings.route) {
            Text("Settings Screen Placeholder")
        }
    }
}
