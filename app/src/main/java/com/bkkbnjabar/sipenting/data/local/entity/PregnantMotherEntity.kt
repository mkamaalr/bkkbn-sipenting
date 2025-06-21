package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pregnant_mothers")
data class PregnantMotherEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int? = null,
    val registrationDate: String?, // Tanggal pendaftaran (format ISO_LOCAL_DATE YYYY-MM-DD)
    val name: String?,
    val nik: String?,
    val dateOfBirth: String?,
    val phoneNumber: String?,
    // Data Lokasi Pengguna (dari SharedPrefs, tidak diisi manual di form ini)
    val provinsiName: String?,
    val provinsiId: Int?,
    val kabupatenName: String?,
    val kabupatenId: Int?,
    val kecamatanName: String?,
    val kecamatanId: Int?,
    val kelurahanName: String?,
    val kelurahanId: Int?,
    // Data Lokasi yang dipilih dari dropdown
    val rwName: String?,
    val rwId: Int?,
    val rtName: String?,
    val rtId: Int?,
    // Data dari Fragment 2
    val husbandName: String?,
    val fullAddress: String?,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD,
    val createdAt: String? = null // BARU: Menambahkan field createdAt untuk sorting
)
