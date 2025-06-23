package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RwDto(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "kelurahan_id") val kelurahanId: Int?,
    // Tidak perlu nested KelurahanDto jika Anda tidak membutuhkannya di sini.
    // Jika API RwResource Laravel menyertakan Kelurahan, tambahkan:
     @Json(name = "kelurahan") val kelurahan: KelurahanDto?
)
