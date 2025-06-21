package com.bkkbnjabar.sipenting.data.model.pregnantmother

import android.os.Parcelable
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
data class PregnantMotherRegistrationData(
    val localId: Int? = null,
    val registrationDate: String? = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), // Default ke tanggal hari ini
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
    val husbandName: String? = null,
    val fullAddress: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD,
    val createdAt: String? = null // BARU: Menambahkan field createdAt
) : Parcelable
