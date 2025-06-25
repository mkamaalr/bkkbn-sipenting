package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KabupatenDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "provinsi_id")
    val provinsiId: Int?, // Dibuat nullable
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class KabupatenListResponse(
    @Json(name = "data")
    val data: List<KabupatenDto>?
)
