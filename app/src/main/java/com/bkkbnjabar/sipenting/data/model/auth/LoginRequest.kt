package com.bkkbnjabar.sipenting.data.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username") val username: String? = null, // Mengizinkan nama pengguna atau email
    @Json(name = "email") val email: String? = null,
    @Json(name = "password") val password: String // Ini harus selalu ada
)
