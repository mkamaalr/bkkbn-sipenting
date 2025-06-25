package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * Represents the "breastfeeding_mother" table in the local Room database.
 */
@Entity(tableName = "breastfeeding_mother")
data class BreastfeedingMotherEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val id: String? = null, // Server ID after sync
    val name: String,
    val nik: String,
    val dateOfBirth: String,
    val phoneNumber: String?,
    // Location details
    val provinsiId: Int,
    val kabupatenId: Int,
    val kecamatanId: Int,
    val kelurahanId: Int,
    val rwId: Int,
    val rtId: Int,
    val fullAddress: String,
    // Status specific to breastfeeding
    val lastChildBirthDate: String?,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)