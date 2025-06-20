package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KelurahanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kelurahans: List<KelurahanEntity>)

    @Query("SELECT * FROM kelurahans ORDER BY name ASC")
    fun getAllKelurahans(): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahans WHERE kecamatanId = :kecamatanId ORDER BY name ASC")
    fun getKelurahansByKecamatan(kecamatanId: Int): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahans WHERE id = :kelurahanId")
    suspend fun getKelurahanById(kelurahanId: Int): KelurahanEntity?

    @Query("DELETE FROM kelurahans")
    suspend fun deleteAll()
}
