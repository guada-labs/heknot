package com.heknot.app.data.repository

import com.heknot.app.data.local.database.dao.TrainingDao
import com.heknot.app.data.local.database.entity.RoutineExercise
import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import kotlinx.coroutines.flow.Flow

class OfflineTrainingRepository(private val trainingDao: TrainingDao) : TrainingRepository {
    override fun getAllEquipment(): Flow<List<UserEquipment>> = trainingDao.getAllEquipment()

    override suspend fun updateEquipmentAvailability(equipmentId: String, isAvailable: Boolean): Int =
        trainingDao.updateEquipmentAvailability(equipmentId, isAvailable)

    override suspend fun upsertEquipment(equipment: UserEquipment): Long =
        trainingDao.upsertEquipment(equipment)

    override fun getAllPlans(): Flow<List<WorkoutPlan>> = trainingDao.getAllPlans()

    override fun getPlanById(planId: Long): Flow<WorkoutPlan?> = trainingDao.getPlanById(planId)

    override suspend fun insertPlan(plan: WorkoutPlan): Long = trainingDao.insertPlan(plan)

    override fun getRoutinesForPlan(planId: Long): Flow<List<WorkoutRoutine>> =
        trainingDao.getRoutinesForPlan(planId)

    override suspend fun insertRoutine(routine: WorkoutRoutine): Long =
        trainingDao.insertRoutine(routine)

    override fun getExercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>> =
        trainingDao.getExercisesForRoutine(routineId)

    override suspend fun insertRoutineExercise(exercise: RoutineExercise): Long =
        trainingDao.insertRoutineExercise(exercise)
}
