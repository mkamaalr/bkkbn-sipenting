package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildVisitsDao {

    /**
     * Inserts a single child visit record.
     * If a visit with the same primary key exists, it will be replaced.
     * @param visit The visit entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: ChildVisitsEntity)

    /**
     * Updates an existing child visit record.
     * @param visit The visit entity with updated information.
     */
    @Update
    suspend fun updateVisit(visit: ChildVisitsEntity)

    /**
     * Retrieves all visits for a specific child, ordered by the visit date descending.
     * @param childId The localId of the child.
     * @return A Flow emitting a list of all visits for that child.
     */
    @Query("SELECT * FROM child_visits WHERE childId = :childId ORDER BY visitDate DESC")
    fun getVisitsForChild(childId: Int): Flow<List<ChildVisitsEntity>>

    /**
     * Retrieves a single visit by its unique local visit ID.
     * @param localVisitId The primary key of the visit.
     * @return A Flow emitting the ChildVisitsEntity, or null if not found.
     */
    @Query("SELECT * FROM child_visits WHERE localVisitId = :localVisitId")
    fun getVisitById(localVisitId: Int): Flow<ChildVisitsEntity?>

    /**
     * Retrieves a list of all child visits that have been saved locally but not yet
     * synced to the server.
     * @return A list of ChildVisitsEntity with a 'PENDING' sync status.
     */
    @Query("SELECT * FROM child_visits WHERE syncStatus = 'PENDING'")
    suspend fun getPendingVisits(): List<ChildVisitsEntity>

    /**
     * A transactional function that first deletes all existing child visit records
     * and then inserts a fresh list from the server. Using @Transaction ensures this
     * operation is atomic and safe.
     * @param visits The new list of visits fetched from the server.
     */
    @Transaction
    suspend fun clearAndInsertAll(visits: List<ChildVisitsEntity>) {
        deleteAll()
        insertAll(visits)
    }

    @Query("DELETE FROM child_visits")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visits: List<ChildVisitsEntity>)
}
