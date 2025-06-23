package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnantMotherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregnantMother(pregnantMother: PregnantMotherEntity): Long

    @Update
    suspend fun updatePregnantMother(pregnantMother: PregnantMotherEntity)

    @Delete
    suspend fun deletePregnantMother(pregnantMother: PregnantMotherEntity)

    @Query("SELECT * FROM pregnant_mother_table WHERE localId = :localId")
    suspend fun getPregnantMotherById(localId: Int): PregnantMotherEntity?

    @Query("SELECT * FROM pregnant_mother_table")
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>

    @Query("DELETE FROM pregnant_mother_table WHERE localId = :localId")
    suspend fun deletePregnantMotherById(localId: Int)


}
