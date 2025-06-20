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
    suspend fun insertAll(kabupatens: List<KabupatenEntity>)

    @Query("SELECT * FROM kabupatens ORDER BY name ASC")
    fun getAllKabupatens(): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kabupatens WHERE provinsiId = :provinsiId ORDER BY name ASC")
    fun getKabupatensByProvinsi(provinsiId: Int): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kabupatens WHERE id = :kabupatenId")
    suspend fun getKabupatenById(kabupatenId: Int): KabupatenEntity?

    @Query("DELETE FROM kabupatens")
    suspend fun deleteAll()
}