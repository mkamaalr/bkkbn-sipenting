package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PregnantMotherUploadRequest(
    @Json(name = "name") val name: String,
    @Json(name = "nik") val nik: String,
    @Json(name = "date_of_birth") val dateOfBirth: String,
    @Json(name = "phone_number") val phoneNumber: String, // Tetap String non-nullable
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "kelurahan_id") val kelurahanId: Int?,
    @Json(name = "rw_id") val rwId: Int?,
    @Json(name = "rt_id") val rtId: Int?,
    @Json(name = "full_address") val fullAddress: String,
    @Json(name = "registration_date") val registrationDate: String,
    @Json(name = "husband_name") val husbandName: String // Tetap String non-nullable
    // Tambahkan bidang lain yang API server Anda butuhkan
)