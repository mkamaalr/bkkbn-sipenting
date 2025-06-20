package com.bkkbnjabar.sipenting.data.model.lookup

import com.bkkbnjabar.sipenting.data.model.Kabupaten // Pastikan ini mengarah ke model domain
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KecamatanDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "kabupaten_id") val kabupatenId: Int,
    @Json(name = "kabupaten") val kabupaten: KabupatenDto? // Objek bersarang
)