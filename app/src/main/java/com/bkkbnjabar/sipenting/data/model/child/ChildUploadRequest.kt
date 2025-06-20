package com.bkkbnjabar.sipenting.data.model.child

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChildUploadRequest(
    @Json(name = "name") val name: String,
    @Json(name = "nik") val nik: String, // NIK anak jika ada
    @Json(name = "date_of_birth") val dateOfBirth: String, // Format DD/MM/YYYY
    @Json(name = "gender") val gender: String, // 'L' for Male, 'P' for Female
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "full_address") val fullAddress: String,
    @Json(name = "parent_name") val parentName: String // Nama orang tua
    // Tambahkan bidang lain yang diperlukan oleh API server Anda untuk Anak di sini
    // Contoh:
    // @Json(name = "weight_at_birth") val weightAtBirth: Double?,
    // @Json(name = "height_at_birth") val heightAtBirth: Double?
)