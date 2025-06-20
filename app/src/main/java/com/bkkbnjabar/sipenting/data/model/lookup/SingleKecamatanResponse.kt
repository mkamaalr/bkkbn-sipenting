package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO pembungkus untuk respons API yang berisi SATU objek kecamatan di bawah kunci "data"
@JsonClass(generateAdapter = true)
data class SingleKecamatanResponse(
    @Json(name = "data") val data: KecamatanDto
)
