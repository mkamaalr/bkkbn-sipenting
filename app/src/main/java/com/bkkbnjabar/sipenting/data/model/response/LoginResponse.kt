package com.bkkbnjabar.sipenting.data.model.response

import com.bkkbnjabar.sipenting.data.model.user.UserDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "message")
    val message: String,
    @Json(name = "user")
    val user: UserDto,
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "token_type")
    val tokenType: String
)

