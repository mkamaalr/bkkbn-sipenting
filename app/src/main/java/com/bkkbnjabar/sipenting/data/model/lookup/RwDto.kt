package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RwDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "kelurahan_id")
    val kelurahanId: Int?, // Dibuat nullable
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class RwListResponse(
    @Json(name = "data")
    val data: List<RwDto>?
)
