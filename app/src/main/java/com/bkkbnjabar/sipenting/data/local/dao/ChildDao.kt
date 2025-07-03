package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildWithLatestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    /**
     * Inserts a single child into the child table.
     * If a child with the same primary key already exists, it will be replaced.
     * @param child The child entity to insert.
     * @return The row ID of the inserted child.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity): Long

    @Update
    suspend fun updateChild(child: ChildEntity)


    /**
     * Retrieves a single child by their local ID.
     * @param localId The primary key of the child.
     * @return A Flow emitting the ChildEntity, or null if not found.
     */
    @Query("SELECT * FROM child WHERE localId = :localId")
    fun getChildById(localId: Int): Flow<ChildEntity?>

    /**
     * Retrieves all children from the database, ordered by name.
     * This is useful for displaying the main list of children.
     * @return A Flow emitting a list of all ChildEntity.
     */
    @Query("SELECT * FROM child ORDER BY name ASC")
    fun getAllChildren(): Flow<List<ChildEntity>>

    /**
     * Retrieves all children belonging to a specific mother.
     * @param motherId The localId of the mother.
     * @return A Flow emitting a list of ChildEntity.
     */
    @Query("SELECT * FROM child WHERE motherId = :motherId ORDER BY dateOfBirth DESC")
    fun getChildrenForMother(motherId: Int): Flow<List<ChildEntity>>

    @Query("""
        SELECT 
            pm.*,
            (SELECT pmv.pregnantMotherStatusId FROM child_visits pmv WHERE pmv.childId = pm.localId ORDER BY pmv.createdAt DESC LIMIT 1) as pregnantMotherStatusId,
            (SELECT pmv.nextVisitDate FROM child_visits pmv WHERE pmv.childId = pm.localId ORDER BY pmv.createdAt DESC LIMIT 1) as nextVisitDate
        FROM 
            child pm 
        ORDER BY 
            pm.createdAt DESC
    """)
    fun getAllChildsWithLatestStatus(): Flow<List<ChildWithLatestStatus>>

    @Query("SELECT * FROM child WHERE syncStatus = 'PENDING'")
    suspend fun getPendingChildren(): List<ChildEntity>

    @Query("SELECT * FROM child WHERE id = :serverId LIMIT 1")
    suspend fun findByServerId(serverId: String): ChildEntity?

    @Transaction
    suspend fun clearAndInsertAll(children: List<ChildEntity>) {
        deleteAll()
        insertAll(children)
    }

    @Query("DELETE FROM child")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(children: List<ChildEntity>)
}
