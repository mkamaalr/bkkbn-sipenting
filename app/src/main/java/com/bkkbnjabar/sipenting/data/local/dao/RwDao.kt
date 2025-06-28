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
    suspend fun insertRw(rw: RwEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRWS(rws: List<RwEntity>) // Method for bulk insert

    @Query("SELECT * FROM rw")
    fun getAllRWS(): Flow<List<RwEntity>>

    @Query("SELECT * FROM rw WHERE kelurahanId = :kelurahanId")
    fun getRWSByKelurahan(kelurahanId: Int): Flow<List<RwEntity>>

    @Query("SELECT * FROM rw WHERE id = :rwId")
    suspend fun getRwById(rwId: Int): RwEntity?

    @Query("DELETE FROM rw")
    suspend fun deleteAllRWS()
}
