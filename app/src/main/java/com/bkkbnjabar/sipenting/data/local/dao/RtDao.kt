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
    suspend fun insertAll(rts: List<RtEntity>)

    @Query("SELECT * FROM rts ORDER BY name ASC")
    fun getAllRTS(): Flow<List<RtEntity>>

    @Query("SELECT * FROM rts WHERE rwId = :rwId ORDER BY name ASC")
    fun getRTSByRw(rwId: Int): Flow<List<RtEntity>>

    @Query("SELECT * FROM rts WHERE id = :rtId")
    suspend fun getRtById(rtId: Int): RtEntity?

    @Query("DELETE FROM rts")
    suspend fun deleteAll()
}