package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreastfeedingMotherUploadResponse(
    @Json(name = "id") val id: String?, // ID dari server setelah berhasil upload
    @Json(name = "message") val message: String? // Pesan konfirmasi dari server
)
