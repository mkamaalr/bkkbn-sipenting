package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData

/**
 * Converts the registration form data object into a database entity object.
 * It handles nullable fields by providing default values to ensure the entity is valid.
 */
fun PregnantMotherRegistrationData.toEntity(): PregnantMotherEntity {
    return PregnantMotherEntity(
        localId = this.localId ?: 0, // Use 0 for auto-generation if localId is null
        registrationDate = this.registrationDate ?: "",
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
        syncStatus = this.syncStatus
    )
}