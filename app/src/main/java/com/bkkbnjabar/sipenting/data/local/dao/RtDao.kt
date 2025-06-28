package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRt(rt: RtEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRTS(rts: List<RtEntity>) // Method for bulk insert

    @Query("SELECT * FROM rt")
    fun getAllRTS(): Flow<List<RtEntity>>

    @Query("SELECT * FROM rt WHERE rwId = :rwId")
    fun getRTSByRw(rwId: Int): Flow<List<RtEntity>>

    @Query("SELECT * FROM rt WHERE id = :rtId")
    suspend fun getRtById(rtId: Int): RtEntity?

    @Query("DELETE FROM rt")
    suspend fun deleteAllRTS()
}
