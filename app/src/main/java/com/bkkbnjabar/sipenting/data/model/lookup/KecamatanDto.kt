package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KecamatanDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "kabupaten_id")
    val kabupatenId: Int?, // Dibuat nullable
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class KecamatanListResponse(
    @Json(name = "data")
    val data: List<KecamatanDto>?
)
