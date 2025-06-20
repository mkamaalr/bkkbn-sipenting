package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PregnantMotherUploadResponse(
    @Json(name = "id") val id: String?, // ID dari server setelah berhasil upload
    @Json(name = "message") val message: String? // Pesan konfirmasi dari server
)
