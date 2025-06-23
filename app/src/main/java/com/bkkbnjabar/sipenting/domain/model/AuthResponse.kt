package com.bkkbnjabar.sipenting.domain.model


data class AuthResponse(
    val accessToken: String,
    val refreshToken: String?,
    val userName: String?,
    val userEmail: String?
)