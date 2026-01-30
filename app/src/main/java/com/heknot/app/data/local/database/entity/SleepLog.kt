package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hours: Float,
    val quality: Int, // 1 to 5
    val date: LocalDate = LocalDate.now(),
    val notes: String? = null
)
