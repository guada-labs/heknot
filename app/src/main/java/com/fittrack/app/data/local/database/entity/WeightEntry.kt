package com.fittrack.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val weight: Float,
    val date: LocalDate, // Fecha del registro (sin hora) para agrupar/graficar
    val timestamp: LocalDateTime = LocalDateTime.now(), // Momento exacto de creación
    
    val note: String? = null
)
