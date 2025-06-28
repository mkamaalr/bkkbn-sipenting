package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * Data class that holds the state for the Breastfeeding Mother's registration form (page 1).
 */
data class BreastfeedingMotherRegistrationData(
    val localId: Int? = null,
    val name: String? = null,
    val nik: String? = null,
    val dateOfBirth: String? = null,
    val phoneNumber: String? = null,
    val provinsiName: String? = null,
    val provinsiId: Int? = null,
    val kabupatenName: String? = null,
    val kabupatenId: Int? = null,
    val kecamatanName: String? = null,
    val kecamatanId: Int? = null,
    val kelurahanName: String? = null,
    val kelurahanId: Int? = null,
    val rwName: String? = null,
    val rwId: Int? = null,
    val rtName: String? = null,
    val rtId: Int? = null,
    val fullAddress: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: String? = null
)