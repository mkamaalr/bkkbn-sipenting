package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreastfeedingMotherUploadRequest(
    @Json(name = "name") val name: String,
    @Json(name = "nik") val nik: String,
    @Json(name = "date_of_birth") val dateOfBirth: String, // Format DD/MM/YYYY
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "full_address") val fullAddress: String
    // Tambahkan bidang lain yang diperlukan oleh API server Anda untuk Ibu Menyusui di sini
    // Contoh:
    // @Json(name = "child_id") val childId: String?, // ID anak yang disusui
    // @Json(name = "breastfeeding_start_date") val breastfeedingStartDate: String?
)
