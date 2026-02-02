package com.heknot.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String = "", val icon: ImageVector? = null) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home", "Dashboard", Icons.Default.Home)
    data object Workout : Screen("workout", "Entrenar", Icons.Default.FitnessCenter)
    data object Nutrition : Screen("nutrition", "Nutrición", Icons.Default.Restaurant)
    data object Chart : Screen("chart", "Estadísticas", Icons.Default.BarChart)
    data object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
    data object Water : Screen("water", "Hidratación", Icons.Default.WaterDrop)
    data object History : Screen("history")
    
    // Nutrition Sub-screens
    data object RecipeForm : Screen("recipe_form?id={id}") {
        fun createRoute(id: Long? = null) = if (id != null) "recipe_form?id=$id" else "recipe_form"
    }
    data object RecipeDetail : Screen("recipe_detail/{id}") {
        fun createRoute(id: Long) = "recipe_detail/$id"
    }
}
