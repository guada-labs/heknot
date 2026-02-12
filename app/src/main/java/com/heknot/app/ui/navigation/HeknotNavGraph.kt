package com.heknot.app.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heknot.app.ui.screens.chart.ChartScreen
import com.heknot.app.ui.screens.history.HistoryScreen
import com.heknot.app.ui.screens.home.HomeScreen
import com.heknot.app.ui.screens.onboarding.OnboardingScreen
import com.heknot.app.ui.screens.settings.SettingsScreen
import com.heknot.app.ui.screens.profile.ProfileScreen
import com.heknot.app.ui.screens.training.equipment.EquipmentScreen

import com.heknot.app.ui.screens.main.MainScreen
import com.heknot.app.ui.screens.training.catalog.PlanCatalogScreen
import com.heknot.app.ui.screens.training.detail.WorkoutDetailScreen

@Composable
fun HeknotNavGraph(
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
                    navController.navigate("main") {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainScreen(
                onRestartApp = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToPlanCatalog = {
                    navController.navigate(Screen.PlanCatalog.route)
                },
                onNavigateToWorkoutDetail = { id ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(id))
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToEquipment = { navController.navigate(Screen.Equipment.route) }
            )
        }

        composable(Screen.Equipment.route) {
            EquipmentScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.PlanCatalog.route) {
            PlanCatalogScreen(
                onBack = { navController.popBackStack() },
                onPlanClick = { id -> 
                    navController.navigate(Screen.WorkoutDetail.createRoute(id)) 
                }
            )
        }

        composable(Screen.WorkoutDetail.route) {
            WorkoutDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
