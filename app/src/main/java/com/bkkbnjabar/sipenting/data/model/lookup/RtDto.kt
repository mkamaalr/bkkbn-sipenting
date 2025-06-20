package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RtDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "rw_id") val rwId: Int
    // Tidak perlu nested RwDto jika Anda tidak membutuhkannya di sini.
    // Jika API RtResource Laravel menyertakan Rw, tambahkan:
    // @Json(name = "rw") val rw: RwDto?
)
