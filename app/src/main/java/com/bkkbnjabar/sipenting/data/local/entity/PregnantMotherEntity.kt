package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pregnant_mothers")
data class PregnantMotherEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0, // ID unik lokal

    val serverId: Int? = null, // ID dari server jika sudah diunggah (null jika belum)

    // Data Ibu Hamil (diambil dari PregnantMotherRegistrationData)
    val name: String?, // Dibuat nullable agar konsisten dengan domain model
    val nik: String?,
    val dateOfBirth: String?, // Format DD/MM/YYYY
    val phoneNumber: String?,
    val husbandName: String?,
    val fullAddress: String?,
    val registrationDate: String?, // Tanggal pendaftaran

    // --- Data Domisili ---
    val provinsiName: String?,
    val provinsiId: Int?,
    val kabupatenName: String?,
    val kabupatenId: Int?,
    val kecamatanName: String?,
    val kecamatanId: Int?,
    val kelurahanName: String?,
    val kelurahanId: Int?,
    val rwName: String?,
    val rwId: Int?,
    val rtName: String?,
    val rtId: Int?,

    // Status Sinkronisasi
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD, // Status awal adalah pending upload

    // Timestamp
    val createdAt: Long = System.currentTimeMillis(), // Timestamp pembuatan lokal
    val updatedAt: Long = System.currentTimeMillis() // Timestamp pembaruan lokal
    // Tambahkan semua bidang lain yang relevan untuk Ibu Hamil di sini
)
