package com.bkkbnjabar.sipenting.data.model.child

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChildUploadResponse(
    @Json(name = "id") val id: String?, // ID dari server setelah berhasil upload
    @Json(name = "message") val message: String? // Pesan konfirmasi dari server
)
