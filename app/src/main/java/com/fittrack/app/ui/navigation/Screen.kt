package com.fittrack.app.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object History : Screen("history")
    data object Chart : Screen("chart")
    data object Settings : Screen("settings")
}
