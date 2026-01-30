package com.heknot.app.ui.screens.workout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mapea el nombre del icono (String) al ImageVector correspondiente de Material Icons.
 */
object WorkoutIconMapper {
    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            "DirectionsWalk" -> Icons.Default.DirectionsWalk
            "DirectionsRun" -> Icons.Default.DirectionsRun
            "DirectionsBike" -> Icons.Default.DirectionsBike
            "FitnessCenter" -> Icons.Default.FitnessCenter
            "Terrain" -> Icons.Default.Terrain
            "Pool" -> Icons.Default.Pool
            "Rowing" -> Icons.Default.Rowing
            "Stairs" -> Icons.Default.Stairs
            "MusicNote" -> Icons.Default.MusicNote
            "SportsKabaddi" -> Icons.Default.SportsKabaddi
            "SportsSoccer" -> Icons.Default.SportsSoccer
            "SportsBasketball" -> Icons.Default.SportsBasketball
            "SportsTennis" -> Icons.Default.SportsTennis
            "SelfImprovement" -> Icons.Default.SelfImprovement
            "Accessibility" -> Icons.Default.Accessibility
            "Bedtime" -> Icons.Default.Bedtime
            "Work" -> Icons.Default.Work
            "CleaningServices" -> Icons.Default.CleaningServices
            "Yard" -> Icons.Default.Yard
            "Restaurant" -> Icons.Default.Restaurant
            "SportsVolleyball" -> Icons.Default.SportsVolleyball
            "GolfCourse" -> Icons.Default.GolfCourse
            "Skateboarding" -> Icons.Default.Skateboarding
            "DownhillSkiing" -> Icons.Default.DownhillSkiing
            else -> Icons.Default.FitnessCenter // Fallback
        }
    }
}
