package com.bkkbnjabar.sipenting.data.model.child

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- DTOs for Child's Mother ---

@JsonClass(generateAdapter = true)
data class ChildMotherDto(
    @Json(name = "id") val id: String,
    val name: String?,
    val nik: String?,
    // ... add all other fields from your API ...
)

data class ChildMotherUploadRequest(
    val name: String?,
    val nik: String?
    // ... all other fields
)

// --- DTOs for Child ---

@JsonClass(generateAdapter = true)
data class ChildDto(
    @Json(name = "id") val id: String,
    @Json(name = "mother_id") val motherId: String,
    val name: String?,
    val nik: String?,
    val date_of_birth: String?
    // ... all other fields
)

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

// --- DTOs for Child Visit ---

@JsonClass(generateAdapter = true)
data class ChildVisitDto(
    @Json(name = "id") val id: String,
    @Json(name = "child_id") val childId: String,
    val visit_date: String?
    // ... all other fields from your API for a visit
)

data class ChildVisitUploadRequest(
    val child_id: String,
    val visit_date: String?
    // ... all other fields
)
