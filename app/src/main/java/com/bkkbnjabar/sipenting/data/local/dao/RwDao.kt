package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RwDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rws: List<RwEntity>)

    @Query("SELECT * FROM rws ORDER BY name ASC")
    fun getAllRWS(): Flow<List<RwEntity>>

    @Query("SELECT * FROM rws WHERE kelurahanId = :kelurahanId ORDER BY name ASC")
    fun getRWSByKelurahan(kelurahanId: Int): Flow<List<RwEntity>>

    @Query("SELECT * FROM rws WHERE id = :rwId")
    suspend fun getRwById(rwId: Int): RwEntity?

    @Query("DELETE FROM rws")
    suspend fun deleteAll()
}
