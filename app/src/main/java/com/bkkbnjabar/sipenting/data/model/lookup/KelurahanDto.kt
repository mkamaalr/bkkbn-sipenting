package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KelurahanDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "kecamatan_id")
    val kecamatanId: Int?, // Dibuat nullable
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class KelurahanListResponse(
    @Json(name = "data")
    val data: List<KelurahanDto>?
)
