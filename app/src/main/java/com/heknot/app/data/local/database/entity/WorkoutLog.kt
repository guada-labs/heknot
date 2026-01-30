package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class WorkoutType(
    val displayName: String,
    val category: WorkoutCategory,
    val metMin: Float,
    val metMax: Float,
    val requiresDistance: Boolean = false,
    val iconName: String = "FitnessCenter" // Nombre del icono Material
) {
    // CARDIO
    WALK("Caminata", WorkoutCategory.CARDIO, 2.5f, 4.5f, true, "DirectionsWalk"),
    RUN("Correr", WorkoutCategory.CARDIO, 7.0f, 12.0f, true, "DirectionsRun"),
    BIKE("Bicicleta", WorkoutCategory.CARDIO, 4.0f, 10.0f, true, "DirectionsBike"),
    BIKE_STATIONARY("Bicicleta Estática", WorkoutCategory.CARDIO, 5.0f, 9.0f, false, "FitnessCenter"),
    HIKING("Senderismo", WorkoutCategory.CARDIO, 5.0f, 8.0f, true, "Terrain"),
    SWIM("Natación", WorkoutCategory.CARDIO, 6.0f, 11.0f, true, "Pool"),
    ROWING("Remo", WorkoutCategory.CARDIO, 6.0f, 12.0f, false, "Rowing"),
    JUMP_ROPE("Saltar la Cuerda", WorkoutCategory.CARDIO, 8.0f, 12.0f, false, "FitnessCenter"),
    ELLIPTICAL("Elíptica", WorkoutCategory.CARDIO, 5.0f, 8.0f, false, "FitnessCenter"),
    STAIR_CLIMBING("Subir Escaleras", WorkoutCategory.CARDIO, 6.0f, 9.0f, false, "Stairs"),
    DANCING("Baile", WorkoutCategory.CARDIO, 4.5f, 7.0f, false, "MusicNote"),
    BOXING("Boxeo", WorkoutCategory.CARDIO, 7.0f, 10.0f, false, "SportsKabaddi"),
    SOCCER("Fútbol", WorkoutCategory.CARDIO, 7.0f, 10.0f, false, "SportsSoccer"),
    BASKETBALL("Baloncesto", WorkoutCategory.CARDIO, 6.0f, 9.0f, false, "SportsBasketball"),
    TENNIS("Tenis", WorkoutCategory.CARDIO, 6.0f, 8.0f, false, "SportsTennis"),
    
    // FUERZA
    GYM("Gimnasio", WorkoutCategory.STRENGTH, 3.0f, 8.0f, false, "FitnessCenter"),
    WEIGHTLIFTING("Levantamiento de Pesas", WorkoutCategory.STRENGTH, 5.0f, 8.0f, false, "FitnessCenter"),
    BODYWEIGHT("Ejercicios con Peso Corporal", WorkoutCategory.STRENGTH, 3.5f, 6.0f, false, "SelfImprovement"),
    CROSSFIT("CrossFit", WorkoutCategory.STRENGTH, 6.0f, 10.0f, false, "FitnessCenter"),
    CALISTHENICS("Calistenia", WorkoutCategory.STRENGTH, 4.0f, 7.0f, false, "SelfImprovement"),
    
    // FLEXIBILIDAD
    YOGA("Yoga", WorkoutCategory.FLEXIBILITY, 2.5f, 4.0f, false, "SelfImprovement"),
    PILATES("Pilates", WorkoutCategory.FLEXIBILITY, 3.0f, 5.0f, false, "SelfImprovement"),
    STRETCHING("Estiramientos", WorkoutCategory.FLEXIBILITY, 2.0f, 3.0f, false, "Accessibility"),
    
    // ACTIVIDAD DIARIA
    SLEEP("Dormir", WorkoutCategory.REST, 0.9f, 1.0f, false, "Bedtime"),
    SITTING("Sentado / Trabajo", WorkoutCategory.DAILY_ACTIVITY, 1.3f, 1.5f, false, "Work"),
    STANDING("De pie", WorkoutCategory.DAILY_ACTIVITY, 2.0f, 2.5f, false, "Accessibility"),
    LIGHT_WALKING("Caminata Ligera", WorkoutCategory.DAILY_ACTIVITY, 2.0f, 3.0f, false, "DirectionsWalk"),
    HOUSEWORK("Tareas del Hogar", WorkoutCategory.DAILY_ACTIVITY, 2.5f, 4.0f, false, "CleaningServices"),
    GARDENING("Jardinería", WorkoutCategory.DAILY_ACTIVITY, 3.0f, 5.0f, false, "Yard"),
    COOKING("Cocinar", WorkoutCategory.DAILY_ACTIVITY, 2.0f, 3.0f, false, "Restaurant"),
    
    // DEPORTES ESPECÍFICOS
    VOLLEYBALL("Voleibol", WorkoutCategory.CARDIO, 4.0f, 6.0f, false, "SportsVolleyball"),
    GOLF("Golf", WorkoutCategory.DAILY_ACTIVITY, 3.0f, 5.0f, false, "GolfCourse"),
    SKATING("Patinaje", WorkoutCategory.CARDIO, 5.0f, 8.0f, false, "Skateboarding"),
    SKIING("Esquí", WorkoutCategory.CARDIO, 5.0f, 9.0f, false, "DownhillSkiing"),
    CLIMBING("Escalada", WorkoutCategory.STRENGTH, 6.0f, 10.0f, false, "Terrain"),
    
    // OTROS
    OTHER("Otro", WorkoutCategory.DAILY_ACTIVITY, 3.0f, 6.0f, false, "FitnessCenter");

    /**
     * Calcula el MET efectivo basado en el rating de esfuerzo (1-5).
     * 1 = muy fácil (MET mínimo)
     * 5 = máximo esfuerzo (MET máximo)
     */
    fun getEffectiveMet(effortRating: Int?): Float {
        val effort = (effortRating ?: 3).coerceIn(1, 5)
        val range = metMax - metMin
        return metMin + (range * (effort - 1) / 4f)
    }

    companion object {
        /**
         * Obtiene todas las actividades de una categoría específica.
         */
        fun getByCategory(category: WorkoutCategory): List<WorkoutType> {
            return values().filter { it.category == category }
        }
    }
}

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val type: WorkoutType,
    val completed: Boolean = true,
    
    // Métricas
    val durationMinutes: Int? = null,
    val distanceKm: Float? = null,
    val caloriesBurned: Int? = null,
    
    // Feedback subjetivo
    val effortRating: Int? = null, // 1 a 5 (Cansancio)
    val moodRating: Int? = null,   // 1 a 5 (Estado de ánimo)
    
    val notes: String? = null
)
