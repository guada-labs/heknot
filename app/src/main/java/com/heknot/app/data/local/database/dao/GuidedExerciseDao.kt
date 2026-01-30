package com.heknot.app.data.local.database.dao

import androidx.room.*
import com.heknot.app.data.local.database.entity.GuidedExercise
import com.heknot.app.data.local.database.entity.WorkoutCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface GuidedExerciseDao {
    @Query("SELECT * FROM guided_exercises")
    fun getAllExercises(): Flow<List<GuidedExercise>>

    @Query("SELECT * FROM guided_exercises WHERE category = :category")
    fun getExercisesByCategory(category: WorkoutCategory): Flow<List<GuidedExercise>>

    @Query("SELECT * FROM guided_exercises WHERE id = :id")
    suspend fun getExerciseById(id: Int): GuidedExercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: GuidedExercise): Long

    @Delete
    suspend fun deleteExercise(exercise: GuidedExercise): Int
}

