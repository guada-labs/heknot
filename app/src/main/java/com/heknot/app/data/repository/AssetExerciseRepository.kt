package com.heknot.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.InputStreamReader

class AssetExerciseRepository(
    private val context: Context,
    private val gson: Gson = Gson()
) : ExerciseRepository {

    private val exercises: List<Exercise> by lazy {
        try {
            context.assets.open("exercises/exercises.json").use { inputStream ->
                val reader = InputStreamReader(inputStream)
                val itemType = object : TypeToken<List<Exercise>>() {}.type
                gson.fromJson<List<Exercise>>(reader, itemType) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getAllExercises(): Flow<List<Exercise>> = flow {
        emit(exercises)
    }.flowOn(Dispatchers.IO)

    override fun getExerciseById(id: String): Flow<Exercise?> = getAllExercises().map { list ->
        list.find { it.id == id }
    }

    override fun getExercisesByCategory(category: String): Flow<List<Exercise>> = getAllExercises().map { list ->
        list.filter { it.category.equals(category, ignoreCase = true) }
    }

    override fun searchExercises(query: String): Flow<List<Exercise>> = getAllExercises().map { list ->
        list.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true) ||
            it.primaryMuscles.any { muscle -> muscle.contains(query, ignoreCase = true) }
        }
    }
}
