package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreastfeedingMotherVisitsDao {
    /**
     * Inserts a new visit record.
     * @param visit The visit entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: BreastfeedingMotherVisitsEntity)

    /**
     * Gets all visit records for a specific pregnant mother, ordered by visit date.
     * @param motherId The local ID of the mother.
     * @return A Flow emitting a list of visits for the specified mother.
     */
    @Query("SELECT * FROM breastfeeding_mother_visits WHERE breastfeedingMotherId = :breastfeedingMotherId ORDER BY createdAt DESC")
    fun getVisitsForMother(breastfeedingMotherId: Int): Flow<List<BreastfeedingMotherVisitsEntity>>

    @Update
    suspend fun updateVisit(visit: BreastfeedingMotherVisitsEntity)

    @Query("SELECT * FROM breastfeeding_mother_visits WHERE localVisitId = :visitId")
    fun getVisitById(visitId: Int): Flow<BreastfeedingMotherVisitsEntity?>
}