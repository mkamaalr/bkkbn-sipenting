package com.bkkbnjabar.sipenting.data.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "uuid")
    val uuid: String,
    @Json(name = "name")
    val name: String?,
    @Json(name = "username")
    val username: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "role")
    val role: String?,
    @Json(name = "user_image_url")
    val user_image_url: String?,
    @Json(name = "user_image_path")
    val user_image_path: String?,
    @Json(name = "kelurahan_id")
    val kelurahan_id: String? // Menerima sebagai String, akan diubah ke Int di mapper
)
