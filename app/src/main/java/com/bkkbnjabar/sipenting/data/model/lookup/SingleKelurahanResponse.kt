package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO pembungkus untuk respons API yang berisi SATU objek kelurahan di bawah kunci "data"
@JsonClass(generateAdapter = true)
data class SingleKelurahanResponse(
    @Json(name = "data") val data: KelurahanDto
)