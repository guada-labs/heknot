package com.fittrack.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.app.data.local.database.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeights(): Flow<List<WeightEntry>>
    
    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT :limit")
    fun getRecentWeights(limit: Int): Flow<List<WeightEntry>>
    
    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLastWeight(): Flow<WeightEntry?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weightEntry: WeightEntry): Long
    
    @Update
    suspend fun update(weightEntry: WeightEntry): Int
    
    @Delete
    suspend fun delete(weightEntry: WeightEntry): Int
}
