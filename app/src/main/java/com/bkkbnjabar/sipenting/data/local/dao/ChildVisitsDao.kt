package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}
