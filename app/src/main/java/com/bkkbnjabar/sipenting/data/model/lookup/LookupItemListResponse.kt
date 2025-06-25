package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LookupItemDto(
    @Json(name = "id")
    val id: Int, // <-- Tambahkan ID dari server
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class LookupItemListResponse(
    @Json(name = "data")
    val data: List<LookupItemDto>?
)
