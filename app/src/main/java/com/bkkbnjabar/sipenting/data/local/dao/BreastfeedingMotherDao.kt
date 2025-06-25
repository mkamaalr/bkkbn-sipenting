package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BreastfeedingMotherDao {

    /**
     * Inserts a new breastfeeding mother record.
     * @param mother The entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreastfeedingMother(mother: BreastfeedingMotherEntity)

    /**
     * Gets all breastfeeding mother records from the database.
     * @return A Flow emitting a list of all breastfeeding mothers.
     */
    @Query("SELECT * FROM breastfeeding_mother ORDER BY name ASC")
    fun getAllBreastfeedingMothers(): Flow<List<BreastfeedingMotherEntity>>
}