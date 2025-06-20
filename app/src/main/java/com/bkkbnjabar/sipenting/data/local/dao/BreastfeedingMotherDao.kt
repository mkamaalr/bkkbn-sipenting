package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BreastfeedingMotherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breastfeedingMother: BreastfeedingMotherEntity): Long

    @Update
    suspend fun update(breastfeedingMother: BreastfeedingMotherEntity)

    @Query("SELECT * FROM breastfeeding_mothers ORDER BY createdAt DESC")
    fun getAllBreastfeedingMothers(): Flow<List<BreastfeedingMotherEntity>>

    @Query("SELECT * FROM breastfeeding_mothers WHERE syncStatus = :status")
    suspend fun getBreastfeedingMothersBySyncStatus(status: SyncStatus): List<BreastfeedingMotherEntity>

    @Query("SELECT * FROM breastfeeding_mothers WHERE localId = :localId")
    suspend fun getBreastfeedingMotherById(localId: Int): BreastfeedingMotherEntity?

    @Query("DELETE FROM breastfeeding_mothers WHERE localId = :localId")
    suspend fun deleteBreastfeedingMother(localId: Int)
}