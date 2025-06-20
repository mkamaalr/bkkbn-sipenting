package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KecamatanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kecamatans: List<KecamatanEntity>)

    @Query("SELECT * FROM kecamatans ORDER BY name ASC")
    fun getAllKecamatans(): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatans WHERE kabupatenId = :kabupatenId ORDER BY name ASC")
    fun getKecamatansByKabupaten(kabupatenId: Int): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatans WHERE id = :kecamatanId")
    suspend fun getKecamatanById(kecamatanId: Int): KecamatanEntity?

    @Query("DELETE FROM kecamatans")
    suspend fun deleteAll()
}