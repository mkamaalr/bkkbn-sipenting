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
    suspend fun insertKecamatan(kecamatan: KecamatanEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKecamatans(kecamatans: List<KecamatanEntity>) // Method for bulk insert

    @Query("SELECT * FROM kecamatan_table")
    fun getAllKecamatans(): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatan_table WHERE kabupatenId = :kabupatenId")
    fun getKecamatansByKabupaten(kabupatenId: Int): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatan_table WHERE id = :kecamatanId")
    suspend fun getKecamatanById(kecamatanId: Int): KecamatanEntity?

    @Query("DELETE FROM kecamatan_table")
    suspend fun deleteAllKecamatans()
}
