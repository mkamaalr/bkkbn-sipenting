package com.bkkbnjabar.sipenting.data.remote.mapper

// Pastikan path import untuk AuthResponse Anda benar
import com.bkkbnjabar.sipenting.data.model.response.LoginResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.data.model.user.UserDto
import com.bkkbnjabar.sipenting.domain.model.AuthResponse



/**
 * Mapper function to convert LoginResponse (data layer) to AuthResponse (domain layer).
 */
fun LoginResponse.toDomain(): AuthResponse {
    return AuthResponse(
        message = this.message,
        userSession = UserSession(
            name = this.user.name,
            username = this.user.username,
            email = this.user.email,
            role = this.user.role,
            userImageUrl = this.user.user_image_url,
            // Mengubah kelurahan_id dari String? (API) ke Int? (Domain)
            kelurahanId = this.user.kelurahan_id?.toIntOrNull(),
            accessToken = this.accessToken
        )
    )
}