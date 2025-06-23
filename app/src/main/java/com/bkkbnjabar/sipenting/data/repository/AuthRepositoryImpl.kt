package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.data.model.request.RefreshTokenRequest
import com.bkkbnjabar.sipenting.data.model.response.LoginResponse
import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toUserSession
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sharedPrefsManager: SharedPrefsManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Resource<UserSession> {
        return try {
            val response = authApiService.login(request)
            if (response.isSuccessful) {
                val loginResponse = response.body() // This is your LoginResponse DTO
                if (loginResponse != null) {
                    val userSession = loginResponse.toUserSession() // Convert DTO to Domain Model using AuthMapper
                    // --- INI PERBAIKAN KRUSIALNYA: Panggil saveUserSession() ---
                    // Peta AuthResponse ke UserSession untuk penyimpanan lokal
                    sharedPrefsManager.saveUserSession(userSession) // <<< Dipanggil di sini!
                    Resource.Success(userSession)
                } else {
                    Resource.Error("Login response body is empty.")
                }
            } else {
                // Login failed based on HTTP status code (e.g., 401 Unauthorized, 400 Bad Request)
                val errorMessage = response.errorBody()?.string() ?: "Login failed: Unknown error"
                Resource.Error(errorMessage)
            }
        } catch (e: IOException) {
            // Network-related errors (no internet, timeout)
            Resource.Error("Network error during login: ${e.localizedMessage}")
        } catch (e: Exception) {
            // Any other unexpected errors
            Resource.Error("An unexpected error occurred during login: ${e.localizedMessage}")
        }
    }

    override suspend fun validateToken(): Resource<Boolean> {
        return try {
            val accessToken = sharedPrefsManager.getAccessToken()
            if (accessToken.isNullOrEmpty()) {
                // No token saved locally, so it's not valid.
                // Tidak ada token yang tersimpan secara lokal, jadi tidak valid.
                return Resource.Success(false)
            }

            // Call API to validate the token on the server.
            // Panggil API untuk memvalidasi token di server.
            // Asumsi: If API responds with HTTP 200 OK, the token is considered valid.
            // If it responds with 401, 403, or any other error, the token is invalid.
            // Asumsi: Jika API merespons dengan HTTP 200 OK, token dianggap valid.
            // Jika merespons 401, 403, atau error lainnya, token tidak valid.
            val response = authApiService.validateToken("Bearer $accessToken")

            if (response.isSuccessful) {
                // API responded with 2xx OK. Token is valid.
                // API merespons 2xx OK. Token valid.
                Resource.Success(true)
            } else {
                // API responded with non-2xx (e.g., 401 Unauthorized, 403 Forbidden).
                // Token is considered invalid or expired.
                // Clear the local session as the token is no longer valid.
                // API merespons non-2xx (misalnya 401 Unauthorized, 403 Forbidden).
                // Token dianggap tidak valid atau kadaluarsa.
                // Hapus sesi lokal karena token sudah tidak berlaku.
                clearSession()
                Resource.Success(false)
            }
        } catch (e: IOException) {
            // Network issues (no internet, timeout, etc.).
            // Assume token cannot be validated at this time.
            // Masalah jaringan (tidak ada koneksi, timeout, dll).
            // Anggap token tidak dapat divalidasi saat ini.
            Resource.Error("Network error during token validation: ${e.localizedMessage}", data = false)
        } catch (e: Exception) {
            // Any other unexpected errors.
            // Kesalahan tak terduga lainnya.
            Resource.Error("An unexpected error occurred during token validation: ${e.localizedMessage}", data = false)
        }
    }

    override suspend fun clearSession() {
        // Hapus semua data sesi dari Shared Preferences
        sharedPrefsManager.clearAllSessionData()
        // Atau hanya yang spesifik seperti:
        // sharedPrefsManager.removeAccessToken()
        // sharedPrefsManager.removeRefreshToken()
        // sharedPrefsManager.removeUserSession()
    }

    /**
     * Mengambil access token saat ini yang tersimpan secara lokal.
     * Mengimplementasikan fungsi dari AuthRepository.
     */
    override suspend fun getAccessToken(): String? {
        return sharedPrefsManager.getAccessToken()
    }

    /**
     * Mencoba untuk me-refresh access token menggunakan refresh token (jika tersedia).
     * Mengimplementasikan fungsi dari AuthRepository.
     */
    override suspend fun refreshAccessToken(): Resource<String> {
        return try {
            val refreshToken = sharedPrefsManager.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                // Tidak ada refresh token, tidak bisa me-refresh.
                clearSession() // Hapus sesi karena tidak bisa mendapatkan token baru
                return Resource.Error("No refresh token available. Please log in again.", data = null)
            }

            // NEW: Buat RefreshTokenRequest
            val refreshRequest = RefreshTokenRequest(refreshToken = refreshToken)
            // NEW: Panggil API refresh token
            val response = authApiService.refreshToken(refreshRequest)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null && !loginResponse.accessToken.isNullOrEmpty()) {
                    // Simpan access token baru
                    sharedPrefsManager.saveAccessToken(loginResponse.accessToken)
                    // NEW: Jika API refresh token juga memberikan refresh token baru, simpan juga
                    // if (loginResponse.refreshToken != null) {
                    //     sharedPrefsManager.saveRefreshToken(loginResponse.refreshToken)
                    // }
                    Resource.Success(loginResponse.accessToken)
                } else {
                    // Respons kosong atau tidak ada access token baru
                    clearSession()
                    Resource.Error("Failed to get new access token from refresh response.", data = null)
                }
            } else {
                // Refresh token gagal di server (misalnya refresh token kadaluarsa atau tidak valid)
                val errorMessage = response.errorBody()?.string() ?: "Failed to refresh token: Unknown error"
                clearSession() // Hapus sesi karena refresh token juga tidak valid
                Resource.Error(errorMessage, data = null)
            }
        } catch (e: IOException) {
            Resource.Error("Network error during token refresh: ${e.localizedMessage}", data = null)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred during token refresh: ${e.localizedMessage}", data = null)
        }
    }
}