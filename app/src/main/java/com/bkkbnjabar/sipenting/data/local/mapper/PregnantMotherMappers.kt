package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Fungsi ekstensi untuk mengkonversi PregnantMotherRegistrationData (model domain/UI)
 * menjadi PregnantMotherEntity (entitas Room database).
 */
fun PregnantMotherRegistrationData.toPregnantMotherEntity(): PregnantMotherEntity {
    return PregnantMotherEntity(
        localId = this.localId,
        registrationDate = this.registrationDate,
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
        husbandName = this.husbandName,
        fullAddress = this.fullAddress,
        syncStatus = this.syncStatus ?: SyncStatus.PENDING_UPLOAD, // Default jika null
        createdAt = this.createdAt ?: this.registrationDate ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) // Gunakan createdAt, fallback ke registrationDate, fallback ke tanggal hari ini
    )
}

/**
 * Fungsi ekstensi untuk mengkonversi PregnantMotherEntity (entitas Room database)
 * menjadi PregnantMotherRegistrationData (model domain/UI).
 */
fun PregnantMotherEntity.toPregnantMotherRegistrationData(): PregnantMotherRegistrationData {
    return PregnantMotherRegistrationData(
        localId = this.localId,
        registrationDate = this.registrationDate,
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
        husbandName = this.husbandName,
        fullAddress = this.fullAddress,
        syncStatus = this.syncStatus,
        createdAt = this.createdAt
    )
}
