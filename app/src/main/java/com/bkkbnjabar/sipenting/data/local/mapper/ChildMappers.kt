package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus


/**
 * Converts the registration form data object into a database entity object.
 * It handles nullable fields by providing default values to ensure the entity is valid.
 */
fun ChildData.toEntity(): ChildEntity {
    return ChildEntity(
        localId = this.localId ?: 0, // Use 0 for auto-generation if localId is null
        name = this.name ?: "",
        nik = this.nik ?: "",
        dateOfBirth = this.dateOfBirth ?: "",
        phoneNumber = this.phoneNumber,
        provinsiName = this.provinsiName ?: "",
        provinsiId = this.provinsiId ?: 0,
        kabupatenName = this.kabupatenName ?: "",
        kabupatenId = this.kabupatenId ?: 0,
        kecamatanName = this.kecamatanName ?: "",
        kecamatanId = this.kecamatanId ?: 0,
        kelurahanName = this.kelurahanName ?: "",
        kelurahanId = this.kelurahanId ?: 0,
        rwName = this.rwName ?: "",
        rwId = this.rwId ?: 0,
        rtName = this.rtName ?: "",
        rtId = this.rtId ?: 0,
        fullAddress = this.fullAddress ?: "",
        syncStatus = this.syncStatus ?: SyncStatus.PENDING,
        motherId = this.motherId ?: 0,
    )
}

/**
 * ADDED: Converts a database entity object into a registration form data object.
 * This is useful when editing or adding a new visit to an existing mother.
 */
fun ChildEntity.toRegistrationData(): ChildData {
    return ChildData(
        localId = this.localId,
        motherId = this.motherId,
        name = this.name,
        nik = this.nik,
        dateOfBirth = this.dateOfBirth,
        phoneNumber = this.phoneNumber,
        provinsiName = this.provinsiName,
        provinsiId = this.provinsiId,
        kabupatenName = this.kabupatenName,
        kabupatenId = this.kabupatenId,
        kecamatanName = this.kecamatanName,
        kecamatanId = this.kecamatanId,
        kelurahanName = this.kelurahanName,
        kelurahanId = this.kelurahanId,
        rwName = this.rwName,
        rwId = this.rwId,
        rtName = this.rtName,
        rtId = this.rtId,
        fullAddress = this.fullAddress,
        syncStatus = this.syncStatus,
        createdAt = this.createdAt ?: System.currentTimeMillis()
    )
}