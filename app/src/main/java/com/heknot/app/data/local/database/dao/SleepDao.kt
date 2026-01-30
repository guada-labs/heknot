package com.heknot.app.data.local.database.dao

import androidx.room.*
import com.heknot.app.data.local.database.entity.SleepLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs WHERE date = :date")
    fun getSleepLogsByDate(date: LocalDate): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun getAllSleepLogs(): Flow<List<SleepLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLog): Long

    @Delete
    suspend fun deleteSleepLog(sleepLog: SleepLog): Int

    @Query("DELETE FROM sleep_logs")
    suspend fun deleteAllSleepLogs(): Int
}