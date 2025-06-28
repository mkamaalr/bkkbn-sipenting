package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherWithLatestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BreastfeedingMotherDao {
    /**
     * Inserts a new pregnant mother record. If there's a conflict, it replaces the old one.
     * @param breastfeedingMother The entity to insert.
     * @return The row ID of the newly inserted mother.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreastfeedingMother(breastfeedingMother: BreastfeedingMotherEntity): Long

    /**
     * Gets all pregnant mother records from the database, ordered by creation date.
     * @return A Flow emitting a list of all pregnant mothers.
     */
    @Query("SELECT * FROM breastfeeding_mother ORDER BY createdAt DESC")
    fun getAllBreastfeedingMothers(): Flow<List<BreastfeedingMotherEntity>>

    /**
     * Gets a single pregnant mother by her local ID.
     * @param localId The local primary key of the mother.
     * @return A Flow emitting the specific BreastfeedingMotherEntity or null if not found.
     */
    @Query("SELECT * FROM breastfeeding_mother WHERE localId = :localId")
    fun getBreastfeedingMotherById(localId: Int): Flow<BreastfeedingMotherEntity?>

    /**
     * Gets all pregnant mother records, joining the status, next visit date, and pregnancy age
     * from their most recent visit.
     * @return A Flow emitting a list of the combined data objects.
     */
    @Query("""
        SELECT 
            pm.*,
            (SELECT pmv.pregnantMotherStatusId FROM pregnant_mother_visits pmv WHERE pmv.pregnantMotherLocalId = pm.localId ORDER BY pmv.createdAt DESC LIMIT 1) as pregnantMotherStatusId,
            (SELECT pmv.nextVisitDate FROM pregnant_mother_visits pmv WHERE pmv.pregnantMotherLocalId = pm.localId ORDER BY pmv.createdAt DESC LIMIT 1) as nextVisitDate,
            (SELECT pmv.pregnancyWeekAge FROM pregnant_mother_visits pmv WHERE pmv.pregnantMotherLocalId = pm.localId ORDER BY pmv.createdAt DESC LIMIT 1) as pregnancyWeekAge
        FROM 
            pregnant_mother pm 
        ORDER BY 
            pm.createdAt DESC
    """)
    fun getAllMothersWithLatestStatus(): Flow<List<BreastfeedingMotherWithLatestStatus>>

}