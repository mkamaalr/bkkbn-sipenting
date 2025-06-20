package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO pembungkus untuk respons API yang berisi daftar kecamatan di bawah kunci "data"
@JsonClass(generateAdapter = true)
data class KecamatanListResponse(
    @Json(name = "data") val data: List<KecamatanDto>
)
