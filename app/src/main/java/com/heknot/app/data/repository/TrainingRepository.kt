package com.heknot.app.data.repository

import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import com.heknot.app.data.local.database.entity.RoutineExercise
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar planes de entrenamiento, rutinas y equipamiento.
 */
interface TrainingRepository {
    fun getAllEquipment(): Flow<List<UserEquipment>>
    suspend fun updateEquipmentAvailability(equipmentId: String, isAvailable: Boolean): Int
    suspend fun upsertEquipment(equipment: UserEquipment): Long

    fun getAllPlans(): Flow<List<WorkoutPlan>>
    fun getPlanById(planId: Long): Flow<WorkoutPlan?>
    suspend fun insertPlan(plan: WorkoutPlan): Long

    fun getRoutinesForPlan(planId: Long): Flow<List<WorkoutRoutine>>
    suspend fun insertRoutine(routine: WorkoutRoutine): Long

    fun getExercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>>
    suspend fun insertRoutineExercise(exercise: RoutineExercise): Long
}
