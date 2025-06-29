package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LookupItemDto(
    @Json(name = "id")
    val id: Int, // <-- Tambahkan ID dari server
    @Json(name = "name")
    val name: String,
    @Json(name = "is_risky")
    val isRisky: Boolean? = false // Default to false if not provided by the API
)

@JsonClass(generateAdapter = true)
data class LookupItemListResponse(
    @Json(name = "data")
    val data: List<LookupItemDto>?
)
