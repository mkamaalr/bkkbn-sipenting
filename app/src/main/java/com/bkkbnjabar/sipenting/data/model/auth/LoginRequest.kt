package com.bkkbnjabar.sipenting.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for the login request body.
 * This class structures the data that will be sent to the server for authentication.
 *
 * @param email The user's email. It can be null.
 * @param password The user's password. It can be null.
 */
data class LoginRequest(
    @SerializedName("username")
    val email: String?,

    @SerializedName("password")
    val password: String?
)
