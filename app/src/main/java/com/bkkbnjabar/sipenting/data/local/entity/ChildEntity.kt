package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * Represents the "child" table in the local Room database.
 * This entity is linked to a mother via a foreign key.
 */
@Entity(
    tableName = "child",
    foreignKeys = [
        ForeignKey(
            entity = PregnantMotherEntity::class, // Assuming a child is linked to a PregnantMother record
            parentColumns = ["localId"],
            childColumns = ["motherLocalId"],
            onDelete = ForeignKey.SET_NULL // If mother is deleted, don't delete the child record
        )
    ],
    indices = [Index(value = ["motherLocalId"])]
)
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val id: String? = null, // Server ID after sync
    val motherLocalId: Int?, // Foreign key to the mother
    val name: String,
    val nik: String?,
    val dateOfBirth: String,
    val gender: String, // e.g., "Laki-laki" or "Perempuan"
    val birthWeight: Double?, // in kg
    val birthHeight: Double?, // in cm
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
