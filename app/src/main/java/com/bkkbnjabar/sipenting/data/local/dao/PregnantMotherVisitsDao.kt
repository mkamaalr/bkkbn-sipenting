package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnantMotherVisitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregnantMotherVisit(visit: PregnantMotherVisitsEntity): Long

    @Update
    suspend fun updatePregnantMotherVisit(visit: PregnantMotherVisitsEntity)

    @Delete
    suspend fun deletePregnantMotherVisit(visit: PregnantMotherVisitsEntity)

    @Query("SELECT * FROM pregnant_mother_visits WHERE localVisitId = :localVisitId")
    suspend fun getPregnantMotherVisitById(localVisitId: Int): PregnantMotherVisitsEntity?

    @Query("SELECT * FROM pregnant_mother_visits WHERE pregnantMotherLocalId = :pregnantMotherLocalId ORDER BY visitDate DESC")
    fun getVisitsForPregnantMother(pregnantMotherLocalId: Int): Flow<List<PregnantMotherVisitsEntity>>

    @Query("DELETE FROM pregnant_mother_visits WHERE localVisitId = :localVisitId")
    suspend fun deletePregnantMotherVisitById(localVisitId: Int)

    @Query("DELETE FROM pregnant_mother_visits WHERE pregnantMotherLocalId = :pregnantMotherLocalId")
    suspend fun deleteAllVisitsForPregnantMother(pregnantMotherLocalId: Int)
}
