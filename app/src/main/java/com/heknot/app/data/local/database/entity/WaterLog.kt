package com.heknot.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amountMl: Int,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val type: BeverageType = BeverageType.WATER
) {
    val date: java.time.LocalDate
        get() = dateTime.toLocalDate()
}
