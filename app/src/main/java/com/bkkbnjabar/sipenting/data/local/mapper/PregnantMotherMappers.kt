package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Moshi instance untuk konversi List<String> ke/dari JSON String
private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
private val listOfStringsType = Types.newParameterizedType(List::class.java, String::class.java)
private val stringListAdapter = moshi.adapter<List<String>>(listOfStringsType)

/**
 * Mengkonversi List<String> ke JSON String.
 */
fun List<String>?.toJsonString(): String? {
    return this?.let { stringListAdapter.toJson(it) }
}

/**
 * Mengkonversi JSON String ke List<String>.
 */
fun String?.toListOfString(): List<String>? {
    return this?.let { stringListAdapter.fromJson(it) }
}

// --- Mapper untuk PregnantMotherData ---
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
        syncStatus = this.syncStatus, // Enum langsung
        createdAt = this.createdAt
    )
}

fun PregnantMotherRegistrationData.toPregnantMotherEntity(): PregnantMotherEntity {
    // Pastikan properti non-nullable di Entity memiliki nilai default yang sesuai
    val defaultRegistrationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val defaultName = "Unknown Name"
    val defaultNik = "Unknown NIK"

    return PregnantMotherEntity(
        localId = this.localId, // Biarkan ini null jika ingin Room autoGenerate
        registrationDate = this.registrationDate ?: defaultRegistrationDate,
        name = this.name ?: defaultName,
        nik = this.nik ?: defaultNik,
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
        syncStatus = this.syncStatus, // Enum langsung
        createdAt = this.createdAt
    )
}


