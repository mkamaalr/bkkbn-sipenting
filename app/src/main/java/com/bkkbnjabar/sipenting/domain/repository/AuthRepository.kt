package com.bkkbnjabar.sipenting.domain.repository

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk operasi data terkait autentikasi.
 * Ini bertindak sebagai kontrak untuk lapisan data.
 * ViewModel akan berinteraksi dengan interface ini, bukan implementasinya.
 */
interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Flow<Resource<AuthResponse>>
    fun logout()
    fun isLoggedIn(): Boolean
    fun getToken(): String?
    fun getKelurahanId(): Int?
    fun getUserSession(): UserSession? // <-- FUNGSI BARU
}
