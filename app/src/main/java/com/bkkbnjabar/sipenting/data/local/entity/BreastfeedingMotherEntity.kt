package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(tableName = "breastfeeding_mothers")
data class BreastfeedingMotherEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0, // ID unik lokal
    val serverId: String? = null, // ID dari server jika sudah diunggah
    val name: String,
    val nik: String,
    val dateOfBirth: String, // Format DD/MM/YYYY
    val phoneNumber: String,
    val kecamatanId: Int?,
    val kecamatanName: String?,
    val fullAddress: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD, // Status awal adalah pending upload
    val createdAt: Long = System.currentTimeMillis(), // Timestamp pembuatan lokal
    val updatedAt: Long = System.currentTimeMillis() // Timestamp pembaruan lokal
    // Tambahkan semua bidang lain yang relevan untuk Ibu Menyusui di sini
    // Contoh:
    // val childId: String?, // ID anak yang disusui (jika ada relasi)
    // val breastfeedingStartDate: String? // Tanggal mulai menyusui
)