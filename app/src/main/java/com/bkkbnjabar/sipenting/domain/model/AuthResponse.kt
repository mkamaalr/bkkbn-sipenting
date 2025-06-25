package com.bkkbnjabar.sipenting.domain.model


/**
 * Represents the entire response object from the login API.
 * This class wraps the user session data along with any other metadata
 * like a success message.
 *
 * @param message A message from the server, e.g., "Login successful".
 * @param userSession The actual session data for the logged-in user.
 */
data class AuthResponse(
    val message: String,
    val userSession: UserSession
)
