package com.heknot.app.data.repository

import kotlinx.coroutines.flow.Flow

data class Exercise(
    val id: String,
    val name: String,
    val category: String,
    val difficulty: String,
    val instructions: String,
    val caloriesPerMinute: Double,
    val primaryMuscles: List<String>
)

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    fun getExerciseById(id: String): Flow<Exercise?>
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>
    fun searchExercises(query: String): Flow<List<Exercise>>
}
