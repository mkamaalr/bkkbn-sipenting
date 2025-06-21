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
    suspend fun insertPregnantMother(pregnantMother: PregnantMotherEntity): Long // Nama diubah dari 'insert'

    @Update
    suspend fun updatePregnantMother(pregnantMother: PregnantMotherEntity) // Nama diubah dari 'update'

    @Query("SELECT * FROM pregnant_mothers ORDER BY createdAt DESC") // Membutuhkan field createdAt di entity
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>

    @Query("SELECT * FROM pregnant_mothers WHERE syncStatus = :status")
    suspend fun getPregnantMothersBySyncStatus(status: SyncStatus): List<PregnantMotherEntity>

    @Query("SELECT * FROM pregnant_mothers WHERE localId = :localId")
    suspend fun getPregnantMotherById(localId: Int): PregnantMotherEntity?

    @Query("DELETE FROM pregnant_mothers WHERE localId = :localId")
    suspend fun deletePregnantMother(localId: Int)
}
