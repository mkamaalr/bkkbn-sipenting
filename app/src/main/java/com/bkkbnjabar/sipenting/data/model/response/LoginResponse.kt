package com.bkkbnjabar.sipenting.data.model.response

import com.bkkbnjabar.sipenting.data.model.user.UserDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "message") val message: String,
    @Json(name = "user") val user: UserDto, // Ini dia objek user ter-nested
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String
    // Hapus refreshToken karena API Anda tidak mengembalikannya di sini
    // @Json(name = "refresh_token") val refreshToken: String?
    // Hapus userName dan userEmail dari level ini
    // @Json(name = "user_name") val userName: String?
    // @Json(name = "user_email") val userEmail: String?
)
