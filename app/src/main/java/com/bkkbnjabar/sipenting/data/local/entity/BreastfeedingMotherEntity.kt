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