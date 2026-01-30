package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guided_exercises")
data class GuidedExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: WorkoutCategory,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val durationSeconds: Int? = null,
    val repetitions: Int? = null,
    val imageResName: String? = null, // Resource name for image/animation
    val videoUrl: String? = null
)

