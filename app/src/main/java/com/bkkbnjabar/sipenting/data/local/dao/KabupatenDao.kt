package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KabupatenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKabupaten(kabupaten: KabupatenEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKabupatens(kabupatens: List<KabupatenEntity>) // Method for bulk insert

    @Query("SELECT * FROM kabupaten")
    fun getAllKabupatens(): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kabupaten WHERE provinsiId = :provinsiId")
    fun getKabupatensByProvinsi(provinsiId: Int): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kabupaten WHERE id = :kabupatenId")
    suspend fun getKabupatenById(kabupatenId: Int): KabupatenEntity?

    @Query("DELETE FROM kabupaten")
    suspend fun deleteAllKabupatens()
}
