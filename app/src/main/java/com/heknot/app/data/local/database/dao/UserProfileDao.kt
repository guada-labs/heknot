package com.heknot.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heknot.app.data.local.database.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    // Obtener el único perfil (id=1)
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>
    
    // Versión síncrona/suspend para comprobaciones
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userProfile: UserProfile): Long
    
    @Update
    suspend fun update(userProfile: UserProfile): Int
    
    // Actualizar solo peso actual (Retorna filas afectadas para evitar error KSP)
    @Query("UPDATE user_profile SET currentWeight = :newWeight WHERE id = 1")
    suspend fun updateCurrentWeight(newWeight: Float): Int

    @Query("UPDATE user_profile SET isDarkMode = :enabled WHERE id = 1")
    suspend fun updateDarkMode(enabled: Boolean?): Int

    @Query("UPDATE user_profile SET biometricEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometricEnabled(enabled: Boolean): Int

    @Query("UPDATE user_profile SET neckCm = :neck, waistCm = :waist, hipCm = :hip, chestCm = :chest, armCm = :arm, thighCm = :thigh, calfCm = :calf WHERE id = 1")
    suspend fun updateMeasurements(
        neck: Float?, 
        waist: Float?, 
        hip: Float?,
        chest: Float?,
        arm: Float?,
        thigh: Float?,
        calf: Float?
    ): Int
}
