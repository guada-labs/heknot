package com.heknot.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.heknot.app.ui.navigation.Screen
import com.heknot.app.ui.screens.chart.ChartScreen
import com.heknot.app.ui.screens.home.HomeScreen
import com.heknot.app.ui.screens.settings.SettingsScreen
import com.heknot.app.ui.screens.workout.WorkoutScreen
import com.heknot.app.ui.screens.nutrition.NutritionScreen

@Composable
fun MainScreen(
    onRestartApp: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPlanCatalog: () -> Unit,
    onNavigateToWorkoutDetail: (Long) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Workout,
        Screen.Nutrition,
        Screen.Chart,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp // Da un poco de profundidad para separar del contenido
            ) {
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    NavigationBarItem(
                        icon = { 
                            screen.icon?.let { 
                                Icon(
                                    it, 
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(30.dp)
                                ) 
                            } 
                        },
                        label = { },
                        alwaysShowLabel = false,
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                val startDestId = navController.graph.findStartDestination().id
                                popUpTo(startDestId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Evita que este Scaffold añada padding arriba/abajo él mismo
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToWorkout = {
                        navController.navigate(Screen.Workout.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToNutrition = {
                        navController.navigate(Screen.Nutrition.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToChart = { 
                        navController.navigate(Screen.Chart.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHistory = onNavigateToHistory
                )
            }
            composable(Screen.Workout.route) {
                WorkoutScreen(
                    onNavigateToPlanCatalog = onNavigateToPlanCatalog,
                    onNavigateToWorkoutDetail = onNavigateToWorkoutDetail
                )
            }
            composable(Screen.Nutrition.route) {
                NutritionScreen()
            }
            composable(Screen.Chart.route) {
                ChartScreen(onBack = null)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = null,
                    onNavigateToProfile = onNavigateToProfile,
                    onRestartApp = onRestartApp
                )
            }
        }
    }
}
