package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val weight: Float,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    
    // Optional measurements
    val neckCm: Float? = null,
    val waistCm: Float? = null,
    val hipCm: Float? = null,
    val chestCm: Float? = null,
    val armCm: Float? = null,
    val thighCm: Float? = null,
    val calfCm: Float? = null,
    
    val note: String? = null
)
