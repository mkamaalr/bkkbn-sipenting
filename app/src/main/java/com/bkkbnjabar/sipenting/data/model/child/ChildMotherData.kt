package com.bkkbnjabar.sipenting.data.model.child

import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * A clean data class representing a Child's Mother, for use in the UI and domain layers.
 */
data class ChildMotherData(
    val localId: Int = 0,
    val id: String? = null,
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
    val syncStatus: SyncStatus? = SyncStatus.PENDING,
    val createdAt: Long? = System.currentTimeMillis()
)