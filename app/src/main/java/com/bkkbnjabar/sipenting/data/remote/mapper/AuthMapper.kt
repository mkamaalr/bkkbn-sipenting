package com.bkkbnjabar.sipenting.data.remote.mapper

// Pastikan path import untuk AuthResponse Anda benar
import com.bkkbnjabar.sipenting.data.model.response.LoginResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.data.model.user.UserDto

/**
 * Extension function to convert AuthResponse DTO to UserSession Domain Model.
 * Fungsi ekstensi untuk mengonversi AuthResponse DTO ke UserSession Domain Model.
 */
fun LoginResponse.toUserSession(): UserSession {
    // Access the nested user object from LoginResponse.
    // Mengakses objek user yang ter-nested dari LoginResponse.
    val userDto = this.user

    // Ensure we handle cases where userDto might be null,
    // and its properties might also be null.
    // Pastikan kita menangani kasus di mana userDto mungkin null,
    // dan propertinya juga bisa null.
    return UserSession(
        userId = this.user.uuid,
        name = this.user.name,
        userName = this.user.username,
        accessToken = this.accessToken,
        isLoggedIn = true,
        uuid = this.user.uuid,
        email = this.user.email,
        role = this.user.role,
        userImageUrl = this.user.userImageUrl,
        userImagePath = this.user.userImagePath,
        kelurahanId = this.user.kelurahanId // <<< Akan langsung memetakan Int?
    )
}
