package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.auth.AuthResponse
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("v1/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("v1/logout")
    suspend fun logout(): Response<Unit> // Asumsi logout tidak mengembalikan body

    @POST("v1/refresh-token") // Opsional: jika API Anda memiliki endpoint penyegaran token
    suspend fun refreshToken(): Response<AuthResponse>
}