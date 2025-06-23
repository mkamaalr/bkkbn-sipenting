package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProvinsiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvinsi(provinsi: ProvinsiEntity) // Method for single insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProvinsis(provinsis: List<ProvinsiEntity>) // Method for bulk insert

    @Query("SELECT * FROM provinsi_table")
    fun getAllProvinsis(): Flow<List<ProvinsiEntity>>

    @Query("SELECT * FROM provinsi_table WHERE id = :provinsiId")
    suspend fun getProvinsiById(provinsiId: Int): ProvinsiEntity?

    @Query("DELETE FROM provinsi_table")
    suspend fun deleteAllProvinsis()
}
