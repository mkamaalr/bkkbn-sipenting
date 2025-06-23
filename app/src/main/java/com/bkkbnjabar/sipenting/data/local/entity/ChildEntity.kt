package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0, // ID unik lokal
    val serverId: String? = null, // ID dari server jika sudah diunggah
    val name: String,
    val nik: String?, // NIK anak jika ada
    val dateOfBirth: String, // Format DD/MM/YYYY
    val gender: String, // 'L' for Male, 'P' for Female
    val kecamatanId: Int?,
    val kecamatanName: String?,
    val fullAddress: String,
    val parentName: String, // Nama orang tua
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD, // Status awal adalah pending upload
    val createdAt: Long = System.currentTimeMillis(), // Timestamp pembuatan lokal
    val updatedAt: Long = System.currentTimeMillis() // Timestamp pembaruan lokal
    // Tambahkan bidang lain yang relevan untuk Anak di sini
    // Contoh:
    // val weightAtBirth: Double?,
    // val heightAtBirth: Double?,
    // val parentNik: String?
)