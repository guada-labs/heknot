package com.heknot.app.data.local.database.dao

import androidx.room.*
import com.heknot.app.data.local.database.entity.WaterLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WaterDao {
    // We assume dateTime is stored as TEXT in ISO format, so we can use LIKE to match the date prefix.
    @Query("SELECT * FROM water_logs WHERE dateTime LIKE :date || '%'")
    fun getWaterLogsByDate(date: LocalDate): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE dateTime LIKE :date || '%'")
    fun getTotalWaterByDate(date: LocalDate): Flow<Int?>

    @Query("SELECT * FROM water_logs ORDER BY dateTime DESC")
    fun getAllWaterLogs(): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLog): Long

    @Delete
    suspend fun deleteWaterLog(waterLog: WaterLog): Int

    @Query("DELETE FROM water_logs")
    suspend fun deleteAllWaterLogs(): Int
}
