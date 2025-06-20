package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnantMotherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pregnantMother: PregnantMotherEntity): Long // Mengembalikan ID lokal yang baru

    @Update
    suspend fun update(pregnantMother: PregnantMotherEntity)

    @Query("SELECT * FROM pregnant_mothers ORDER BY createdAt DESC")
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>

    @Query("SELECT * FROM pregnant_mothers WHERE syncStatus = :status")
    suspend fun getPregnantMothersBySyncStatus(status: SyncStatus): List<PregnantMotherEntity>

    @Query("SELECT * FROM pregnant_mothers WHERE localId = :localId")
    suspend fun getPregnantMotherById(localId: Int): PregnantMotherEntity?

    @Query("DELETE FROM pregnant_mothers WHERE localId = :localId")
    suspend fun deletePregnantMother(localId: Int)
}