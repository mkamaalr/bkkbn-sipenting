package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.ChildMotherEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the repository that handles data operations for ChildMotherEntity.
 */
interface ChildMotherRepository {

    /**
     * Inserts a mother's record into the local database.
     * @param mother The mother entity to insert.
     * @return The local row ID of the newly inserted mother.
     */
    suspend fun insertMother(mother: ChildMotherEntity): Long

    /**
     * Retrieves a single mother by her local ID.
     * @param localId The primary key of the mother.
     * @return A Flow emitting the ChildMotherEntity, or null if not found.
     */
    fun getMotherById(localId: Int): Flow<ChildMotherEntity?>

    /**
     * Retrieves all mothers from the database.
     * @return A Flow emitting a list of all ChildMotherEntity.
     */
    fun getAllMothers(): Flow<List<ChildMotherEntity>>
    suspend fun uploadPendingData()
    suspend fun syncFromServer()
}
