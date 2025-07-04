package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(
    tableName = "child",
    // --- ADD THIS FOREIGN KEY DEFINITION ---
    foreignKeys = [
        ForeignKey(
            entity = ChildMotherEntity::class,
            parentColumns = ["localId"], // The primary key in the mother table
            childColumns = ["motherId"],  // The new foreign key field in this table
            onDelete = ForeignKey.CASCADE // If a mother is deleted, her children are also deleted
        )
    ],
    indices = [Index(value = ["motherId"])] // Add an index for better query performance
)
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    // --- ADD THIS FIELD TO STORE THE MOTHER'S ID ---
    val motherId: Int,

    val id: String? = null, // ID from server after sync
    val name: String,
    val nik: String,
    val dateOfBirth: String,
    val phoneNumber: String?,
    val provinsiName: String,
    val provinsiId: Int,
    val kabupatenName: String,
    val kabupatenId: Int,
    val kecamatanName: String,
    val kecamatanId: Int,
    val kelurahanName: String,
    val kelurahanId: Int,
    val rwName: String,
    val rwId: Int,
    val rtName: String,
    val rtId: Int,
    val fullAddress: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)