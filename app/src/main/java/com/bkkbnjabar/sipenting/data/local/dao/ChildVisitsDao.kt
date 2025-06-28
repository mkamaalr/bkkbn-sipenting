package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildVisitsDao {
    /**
     * Inserts a new visit record.
     * @param visit The visit entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: ChildVisitsEntity)

    /**
     * Gets all visit records for a specific pregnant mother, ordered by visit date.
     * @param childId The local ID of the mother.
     * @return A Flow emitting a list of visits for the specified mother.
     */
    @Query("SELECT * FROM child_visits WHERE localVisitId = :childId ORDER BY createdAt DESC")
    fun getVisitsForChild(childId: Int): Flow<List<ChildVisitsEntity>>

    @Update
    suspend fun updateVisit(visit: ChildVisitsEntity)

    @Query("SELECT * FROM child_visits WHERE localVisitId = :visitId")
    fun getVisitById(visitId: Int): Flow<ChildVisitsEntity?>
}
