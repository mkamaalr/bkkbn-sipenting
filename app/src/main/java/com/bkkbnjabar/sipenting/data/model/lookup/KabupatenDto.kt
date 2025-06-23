package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KabupatenDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "provinsi_id") val provinsiId: Int?,
    @Json(name = "provinsi") val provinsi: ProvinsiDto? // Objek bersarang
)