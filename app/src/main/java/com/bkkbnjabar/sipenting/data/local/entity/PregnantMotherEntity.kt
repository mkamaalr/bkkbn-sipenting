package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(tableName = "pregnant_mother_table")
data class PregnantMotherEntity(
    @PrimaryKey(autoGenerate = true) // Biarkan Room mengelola ID
    val localId: Int? = null,
    val registrationDate: String?, // Tanggal pendaftaran (format ISO_LOCAL_DATEYYYY-MM-DD)
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
    val syncStatus: SyncStatus, // Tipe Enum langsung di sini
    val createdAt: String? = null // BARU: Menambahkan field createdAt untuk sorting
)
