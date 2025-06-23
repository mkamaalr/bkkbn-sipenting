package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KelurahanDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "kecamatan") val kecamatan: KecamatanDto? // Objek bersarang
)
