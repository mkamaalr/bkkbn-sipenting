package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnantMotherVisitsDao {
    /**
     * Inserts a new visit record.
     * @param visit The visit entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: PregnantMotherVisitsEntity)

    /**
     * Gets all visit records for a specific pregnant mother, ordered by visit date.
     * @param motherId The local ID of the mother.
     * @return A Flow emitting a list of visits for the specified mother.
     */
    @Query("SELECT * FROM pregnant_mother_visits WHERE pregnantMotherLocalId = :motherId ORDER BY createdAt DESC")
    fun getVisitsForMother(motherId: Int): Flow<List<PregnantMotherVisitsEntity>>

    @Update
    suspend fun updateVisit(visit: PregnantMotherVisitsEntity)

    @Query("SELECT * FROM pregnant_mother_visits WHERE localVisitId = :visitId")
    fun getVisitById(visitId: Int): Flow<PregnantMotherVisitsEntity?>

    @Query("SELECT * FROM pregnant_mother_visits WHERE id = :serverId LIMIT 1")
    suspend fun findByServerId(serverId: String): PregnantMotherVisitsEntity?

    @Query("SELECT * FROM pregnant_mother_visits WHERE syncStatus = 'PENDING'")
    suspend fun getPendingVisits(): List<PregnantMotherVisitsEntity>

    @Transaction
    suspend fun clearAndInsertAll(visit: List<PregnantMotherVisitsEntity>) {
        deleteAll()
        insertAll(visit)
    }

    @Query("DELETE FROM pregnant_mother_visits")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visit: List<PregnantMotherVisitsEntity>)
}
