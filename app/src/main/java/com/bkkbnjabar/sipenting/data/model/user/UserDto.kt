package com.bkkbnjabar.sipenting.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "uuid") val uuid: String,
    @Json(name = "name") val name: String,
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "role") val role: String,
    @Json(name = "user_image_url") val userImageUrl: String?,
    @Json(name = "user_image_path") val userImagePath: String?, // Sesuaikan dengan snake_case Laravel
    @Json(name = "kelurahan_id") val kelurahanId: Int?
    // Tambahkan field lain sesuai dengan respons API model User Laravel Anda
)
