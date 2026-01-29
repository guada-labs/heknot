package com.fittrack.app.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fittrack.app.ui.screens.chart.ChartScreen
import com.fittrack.app.ui.screens.history.HistoryScreen
import com.fittrack.app.ui.screens.home.HomeScreen
import com.fittrack.app.ui.screens.onboarding.OnboardingScreen
import com.fittrack.app.ui.screens.settings.SettingsScreen

@Composable
fun FitTrackNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Onboarding.route
) {
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
            HomeScreen(
                onNavigateToChart = { navController.navigate(Screen.Chart.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
        
        composable(Screen.Chart.route) {
            ChartScreen(onBack = { navController.popBackStack() })
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onRestartApp = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true } // Limpia todo el stack
                    }
                }
            )
        }
    }
}
