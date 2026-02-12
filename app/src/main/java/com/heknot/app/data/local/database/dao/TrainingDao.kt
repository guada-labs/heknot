package com.heknot.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.heknot.app.data.local.database.entity.RoutineExercise
import com.heknot.app.data.local.database.entity.UserEquipment
import com.heknot.app.data.local.database.entity.WorkoutPlan
import com.heknot.app.data.local.database.entity.WorkoutRoutine
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    // --- User Equipment ---
    @Query("SELECT * FROM user_equipment")
    fun getAllEquipment(): Flow<List<UserEquipment>>

    @Upsert
    suspend fun upsertEquipment(equipment: UserEquipment): Long

    @Query("UPDATE user_equipment SET isAvailable = :isAvailable WHERE equipmentId = :equipmentId")
    suspend fun updateEquipmentAvailability(equipmentId: String, isAvailable: Boolean): Int

    // --- Workout Plans ---
    @Query("SELECT * FROM workout_plans")
    fun getAllPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanById(planId: Long): Flow<WorkoutPlan?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    // --- Workout Routines ---
    @Query("SELECT * FROM workout_routines WHERE planId = :planId ORDER BY dayNumber ASC")
    fun getRoutinesForPlan(planId: Long): Flow<List<WorkoutRoutine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutine): Long

    // --- Routine Exercises ---
    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderInRoutine ASC")
    fun getExercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(exercise: RoutineExercise): Long

    // --- Complex Queries / Transactions ---
    @Transaction
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    suspend fun getPlanDetails(planId: Long): WorkoutPlan? // Simplified for now, will add relation classes if needed

    @Query("DELETE FROM workout_plans")
    suspend fun deleteAllPlans(): Int
}
