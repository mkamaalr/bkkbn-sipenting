package com.bkkbnjabar.sipenting.domain.model

/**
 * Data class representing the currently logged-in user's session data.
 * This is the "domain" model, clean and simple, used throughout the app.
 *
 * @param name The full name of the user.
 * @param username The username for login.
 * @param email The user's email address.
 * @param role The role of the user (e.g., "admin", "tpk").
 * @param userImageUrl A URL to the user's profile image.
 * @param kelurahanId The ID of the user's assigned village/kelurahan. This is crucial for pre-filling location data.
 * @param accessToken The Bearer token for authenticating API requests.
 */
data class UserSession(
    val name: String?,
    val username: String?,
    val email: String?,
    val role: String?,
    val userImageUrl: String?,
    val kelurahanId: Int?, // Tipe data sudah benar (Int?)
    val accessToken: String?
)
