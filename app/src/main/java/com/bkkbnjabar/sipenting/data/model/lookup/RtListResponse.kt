package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class RtListResponse(@Json(name = "data") val data: List<RtDto>)
