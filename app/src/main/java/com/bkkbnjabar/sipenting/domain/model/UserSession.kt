package com.bkkbnjabar.sipenting.domain.model

data class UserSession(
    val userId: String, // uuid dari user Laravel
    val name: String,
    val userName: String,
    val accessToken: String,
    val isLoggedIn: Boolean,
    val uuid: String,
    val email: String,
    val role: String?,
    val userImageUrl: String?,
    val userImagePath: String?,
    val kelurahanId: Int? // <<< UBAH KE INT?
)
