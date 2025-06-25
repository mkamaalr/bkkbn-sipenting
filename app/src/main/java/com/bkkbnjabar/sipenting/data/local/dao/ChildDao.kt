package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    /**
     * Inserts a new child record.
     * @param child The child entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity)

    /**
     * Gets all child records from the database.
     * @return A Flow emitting a list of all children.
     */
    @Query("SELECT * FROM child ORDER BY name ASC")
    fun getAllChildren(): Flow<List<ChildEntity>>
}