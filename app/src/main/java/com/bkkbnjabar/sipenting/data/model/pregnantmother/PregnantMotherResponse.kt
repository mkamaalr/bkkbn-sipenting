package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PregnantMotherResponse(
    @Json(name = "message") val message: String,
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
    // Tambahkan field lain dari respons API di sini
)
