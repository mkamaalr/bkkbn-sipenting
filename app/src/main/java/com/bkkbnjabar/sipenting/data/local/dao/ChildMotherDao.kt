package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildMotherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildMotherDao {

    /**
     * Inserts a single mother into the child_mother table.
     * If a mother with the same primary key already exists, it will be replaced.
     * @param mother The mother entity to insert.
     * @return The row ID of the inserted mother.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMother(mother: ChildMotherEntity): Long

    @Update
    suspend fun updateMother(mother: ChildMotherEntity)

    /**
     * Retrieves a single mother by her local ID.
     * @param localId The primary key of the mother.
     * @return A Flow emitting the ChildMotherEntity, or null if not found.
     */
    @Query("SELECT * FROM child_mother WHERE localId = :localId")
    fun getMotherById(localId: Int): Flow<ChildMotherEntity?>

    /**
     * Retrieves all mothers from the database, ordered by creation date.
     * @return A Flow emitting a list of all ChildMotherEntity.
     */
    @Query("SELECT * FROM child_mother ORDER BY createdAt DESC")
    fun getAllMothers(): Flow<List<ChildMotherEntity>>

    // --- ADD THESE FUNCTIONS FOR SYNCING ---
    @Query("SELECT * FROM child_mother WHERE syncStatus = 'PENDING'")
    suspend fun getPendingMothers(): List<ChildMotherEntity>

    @Query("SELECT * FROM child_mother WHERE id = :serverId LIMIT 1")
    suspend fun findByServerId(serverId: String): ChildMotherEntity?

    @Transaction
    suspend fun clearAndInsertAll(mothers: List<ChildMotherEntity>) {
        deleteAll()
        insertAll(mothers)
    }

    @Query("DELETE FROM child_mother")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mothers: List<ChildMotherEntity>)
}
