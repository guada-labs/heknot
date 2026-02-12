package com.heknot.app.data.local.database

import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import com.heknot.app.data.local.database.entity.RoutineExercise

object SeedData {
    val defaultEquipment = listOf(
        UserEquipment("dumbbells", "Mancuernas"),
        UserEquipment("bench", "Banco ajustable"),
        UserEquipment("stationary_bike", "Bicicleta Estática"),
        UserEquipment("resistance_bands", "Bandas de Resistencia"),
        UserEquipment("pull_up_bar", "Barra de Dominadas"),
        UserEquipment("mat", "Tapete / Colchoneta")
    )

    // Un plan inicial de ejemplo: "Iniciación en Casa"
    val initialPlan = WorkoutPlan(
        id = 1,
        title = "Iniciación en Casa",
        description = "Un plan equilibrado para empezar con poco equipo.",
        difficulty = "PRINCIPIANTE",
        goal = "ACONDICIONAMIENTO",
        durationWeeks = 4,
        suggestedEquipment = "mat, dumbbells"
    )

    val initialRoutines = listOf(
        WorkoutRoutine(id = 1, planId = 1, dayNumber = 1, title = "Día 1: Cuerpo Completo"),
        WorkoutRoutine(id = 2, planId = 1, dayNumber = 2, title = "Día 2: Cardio y Core", isCardioBlock = true),
        WorkoutRoutine(id = 3, planId = 1, dayNumber = 3, title = "Día 3: Cuerpo Completo")
    )

    val initialRoutineExercises = listOf(
        RoutineExercise(id = 1, routineId = 1, exerciseId = "push_ups", sets = 3, reps = 12, orderInRoutine = 1),
        RoutineExercise(id = 2, routineId = 1, exerciseId = "squats", sets = 3, reps = 15, orderInRoutine = 2),
        RoutineExercise(id = 3, routineId = 1, exerciseId = "plank", sets = 3, durationSeconds = 30, orderInRoutine = 3),
        RoutineExercise(id = 4, routineId = 2, exerciseId = "stationary_bike", alternativeExerciseId = "jumping_jacks", sets = 1, durationSeconds = 600, orderInRoutine = 1)
    )

    // Lista completa de planes para pre-población
    val plans = listOf(
        initialPlan,
        WorkoutPlan(
            id = 2,
            title = "Hipertrofia: Gym Total",
            description = "Maximiza el crecimiento muscular con este plan avanzado de pesas.",
            difficulty = "AVANZADO",
            goal = "GANAR_MUSCULO",
            durationWeeks = 8,
            suggestedEquipment = "dumbbells, bench, pull_up_bar"
        ),
        WorkoutPlan(
            id = 3,
            title = "Cardio Quemagrasas",
            description = "Mejora tu resistencia y quema calorías con sesiones intensas.",
            difficulty = "INTERMEDIO",
            goal = "PERDIDA_PESO",
            durationWeeks = 6,
            suggestedEquipment = "stationary_bike, resistance_bands"
        )
    )

    val routineList = initialRoutines + listOf(
        WorkoutRoutine(id = 4, planId = 2, dayNumber = 1, title = "Empuje (Pecho, Hombro, Tríceps)"),
        WorkoutRoutine(id = 5, planId = 2, dayNumber = 2, title = "Tracción (Espalda, Bíceps)"),
        WorkoutRoutine(id = 6, planId = 2, dayNumber = 3, title = "Piernas y Core"),
        WorkoutRoutine(id = 7, planId = 3, dayNumber = 1, title = "Intervalos de Alta Intensidad", isCardioBlock = true),
        WorkoutRoutine(id = 8, planId = 3, dayNumber = 2, title = "Resistencia Aeróbica", isCardioBlock = true)
    )

    val exerciseList = initialRoutineExercises + listOf(
        RoutineExercise(id = 5, routineId = 4, exerciseId = "bench_press", sets = 4, reps = 10, orderInRoutine = 1),
        RoutineExercise(id = 6, routineId = 4, exerciseId = "shoulder_press", sets = 3, reps = 12, orderInRoutine = 2),
        RoutineExercise(id = 7, routineId = 7, exerciseId = "stationary_bike", sets = 1, durationSeconds = 1800, orderInRoutine = 1)
    )
}
