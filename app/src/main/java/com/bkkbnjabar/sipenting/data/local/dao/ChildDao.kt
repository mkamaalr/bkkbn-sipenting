package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(child: ChildEntity): Long

    @Update
    suspend fun update(child: ChildEntity)

    @Query("SELECT * FROM children ORDER BY createdAt DESC")
    fun getAllChildren(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE syncStatus = :status")
    suspend fun getChildrenBySyncStatus(status: SyncStatus): List<ChildEntity>

    @Query("SELECT * FROM children WHERE localId = :localId")
    suspend fun getChildById(localId: Int): ChildEntity?

    @Query("DELETE FROM children WHERE localId = :localId")
    suspend fun deleteChild(localId: Int)
}