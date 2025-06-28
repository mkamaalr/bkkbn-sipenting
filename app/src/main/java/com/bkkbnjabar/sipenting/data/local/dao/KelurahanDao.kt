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
    suspend fun insertKelurahan(kelurahan: KelurahanEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKelurahans(kelurahans: List<KelurahanEntity>) // Method for bulk insert

    @Query("SELECT * FROM kelurahan")
    fun getAllKelurahans(): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahan WHERE kecamatanId = :kecamatanId")
    fun getKelurahansByKecamatan(kecamatanId: Int): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahan WHERE id = :kelurahanId")
    suspend fun getKelurahanById(kelurahanId: Int): KelurahanEntity?

    @Query("DELETE FROM kelurahan")
    suspend fun deleteAllKelurahans()
}
