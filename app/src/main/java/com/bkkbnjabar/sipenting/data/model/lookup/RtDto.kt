package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RtDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "rw_id")
    val rwId: Int?, // Dibuat nullable
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class RtListResponse(
    @Json(name = "data")
    val data: List<RtDto>?
)
