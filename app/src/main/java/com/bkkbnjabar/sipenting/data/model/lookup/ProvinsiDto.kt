package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProvinsiDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?, // Memetakan ke 'name' dari Laravel resource
)
