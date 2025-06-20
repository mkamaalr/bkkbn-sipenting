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
    suspend fun insertAll(provinsis: List<ProvinsiEntity>)

    @Query("SELECT * FROM provinsis ORDER BY name ASC")
    fun getAllProvinsis(): Flow<List<ProvinsiEntity>>

    @Query("SELECT * FROM provinsis WHERE id = :provinsiId")
    suspend fun getProvinsiById(provinsiId: Int): ProvinsiEntity?

    @Query("DELETE FROM provinsis")
    suspend fun deleteAll()
}
