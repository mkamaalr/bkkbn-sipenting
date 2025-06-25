package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.data.model.request.RefreshTokenRequest
import com.bkkbnjabar.sipenting.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit service interface for authentication-related API calls.
 */
interface AuthApiService {

    /**
     * Sends a POST request to the login endpoint.
     * @param loginRequest The user's credentials.
     * @return A Retrofit [Response] wrapping the [LoginResponse].
     */
    @POST("v1/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}
