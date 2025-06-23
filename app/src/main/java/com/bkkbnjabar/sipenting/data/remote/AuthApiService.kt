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

interface AuthApiService {

    /**
     * Sends a POST request to the login endpoint.
     * Mengirim permintaan POST ke endpoint login.
     *
     * @param request The LoginRequest object containing user credentials.
     * Objek LoginRequest yang berisi kredensial pengguna.
     * @return A Response containing the LoginResponse DTO.
     * Respons yang berisi LoginResponse DTO.
     */
    @POST("v1/login") // Sesuaikan dengan endpoint login API Anda
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Sends a GET request to validate the current authentication token.
     * Mengirim permintaan GET untuk memvalidasi token autentikasi saat ini.
     *
     * This endpoint is typically used to verify if the token is still active and valid on the server.
     * Endpoint ini biasanya digunakan untuk memverifikasi apakah token masih aktif dan valid di server.
     *
     * @param token The Authorization header (e.g., "Bearer YOUR_ACCESS_TOKEN").
     * Header Authorization (misalnya, "Bearer YOUR_ACCESS_TOKEN").
     * @return A Response containing TokenValidationResponse DTO.
     * Respons yang berisi TokenValidationResponse DTO.
     */
    @GET("v1/user") // Sesuaikan dengan endpoint validasi token API Anda
    suspend fun validateToken(@Header("Authorization") token: String): Response<TokenValidationResponse>
    // Note: Anda perlu membuat data class TokenValidationResponse
    // yang sesuai dengan format respons dari endpoint validateToken.
    // Contoh di bawah ini.
    @POST("api/auth/refresh-token") // <-- SESUAIKAN DENGAN ENDPOINT LARAVEL ANDA!
    @Headers("No-Authentication: true") // Penting agar AuthInterceptor tidak menambah token lagi
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>
}

/**
 * Data class to represent the response from the token validation API endpoint.
 * Data class untuk merepresentasikan respons dari endpoint API validasi token.
 *
 * Customize this based on your actual API response.
 * Sesuaikan ini berdasarkan respons API Anda yang sebenarnya.
 *
 * Example:
 * data class TokenValidationResponse(
 * @Json(name = "is_valid") val isValid: Boolean,
 * @Json(name = "message") val message: String?
 * )
 */
// Contoh TokenValidationResponse - Sesuaikan dengan API Anda!
// Anda mungkin perlu membuat file ini di data/remote/model/auth/TokenValidationResponse.kt
data class TokenValidationResponse(
    // Asumsi API mengembalikan true/false untuk validitas
    // @Json(name = "is_valid") val isValid: Boolean,
    // Atau jika hanya mengembalikan status HTTP 200 OK untuk valid
    // dan 401/403 untuk tidak valid, maka Anda mungkin tidak perlu field ini
    // Cukup periksa response.isSuccessful di AuthRepositoryImpl
    val message: String? = null // Mungkin ada pesan dari server
)