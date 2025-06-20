package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class KelurahanListResponse(@Json(name = "data") val data: List<KelurahanDto>)
