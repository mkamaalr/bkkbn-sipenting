package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnantMotherDao {
    /**
     * Inserts a new pregnant mother record. If there's a conflict, it replaces the old one.
     * @param pregnantMother The entity to insert.
     * @return The row ID of the newly inserted mother.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregnantMother(pregnantMother: PregnantMotherEntity): Long

    /**
     * Gets all pregnant mother records from the database, ordered by creation date.
     * @return A Flow emitting a list of all pregnant mothers.
     */
    @Query("SELECT * FROM pregnant_mother ORDER BY createdAt DESC")
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>

    /**
     * Gets a single pregnant mother by her local ID.
     * @param localId The local primary key of the mother.
     * @return A Flow emitting the specific PregnantMotherEntity or null if not found.
     */
    @Query("SELECT * FROM pregnant_mother WHERE localId = :localId")
    fun getPregnantMotherById(localId: Int): Flow<PregnantMotherEntity?>

}
