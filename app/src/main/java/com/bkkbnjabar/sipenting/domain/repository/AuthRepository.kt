package com.bkkbnjabar.sipenting.domain.repository

import com.bkkbnjabar.sipenting.data.model.auth.AuthResponse
import com.bkkbnjabar.sipenting.utils.Resource

interface AuthRepository {
    suspend fun login(username: String?, email: String?, password: String): Resource<AuthResponse>
    suspend fun logout(): Resource<Unit>
    suspend fun refreshToken(): String? // Harus mengembalikan token baru atau null jika penyegaran gagal
    suspend fun validateToken(): Boolean // Memeriksa apakah token yang tersimpan valid melalui API
}