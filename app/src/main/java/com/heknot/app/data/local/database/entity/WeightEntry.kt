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
    
    val note: String? = null
)
