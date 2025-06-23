package com.bkkbnjabar.sipenting.domain.repository

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.data.model.response.LoginResponse
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.utils.Resource

interface AuthRepository {
    /**
     * Attempts to log in a user with the provided credentials.
     * Mencoba untuk masuk pengguna dengan kredensial yang disediakan.
     *
     * @param request The LoginRequest containing the username and password.
     * LoginRequest yang berisi username dan password.
     * @return A Resource object indicating the success, error, or loading state of the login attempt,
     * along with the UserSession data if successful.
     * Objek Resource yang menunjukkan status sukses, error, atau loading dari upaya login,
     * bersama dengan data UserSession jika berhasil.
     */
    suspend fun login(request: LoginRequest): Resource<UserSession>

    /**
     * Validates the currently stored authentication token.
     * Memvalidasi token autentikasi yang saat ini tersimpan.
     *
     * This might involve checking local storage for a token, or making a network call to verify
     * the token's validity with the server.
     * Ini mungkin melibatkan pemeriksaan penyimpanan lokal untuk token, atau melakukan panggilan
     * jaringan untuk memverifikasi validitas token dengan server.
     *
     * @return A Resource object with `true` if the token is valid, `false` otherwise,
     * or an error/loading state.
     * Objek Resource dengan `true` jika token valid, `false` jika tidak,
     * atau status error/loading.
     */
    suspend fun validateToken(): Resource<Boolean>

    /**
     * Clears the user's session data (e.g., tokens, user info) from local storage.
     * Membersihkan data sesi pengguna (misalnya, token, info pengguna) dari penyimpanan lokal.
     *
     * This is typically called during logout.
     * Ini biasanya dipanggil saat logout.
     */
    suspend fun clearSession()

    /**
     * Retrieves the current access token stored locally.
     * Mengambil access token saat ini yang tersimpan secara lokal.
     *
     * This is primarily used by interceptors or authenticators to attach the token
     * to outgoing API requests or to check its existence.
     * Ini terutama digunakan oleh interceptor atau authenticator untuk melampirkan token
     * ke permintaan API keluar atau untuk memeriksa keberadaannya.
     *
     * @return The access token string, or null if not found.
     * String access token, atau null jika tidak ditemukan.
     */
    suspend fun getAccessToken(): String?

    /**
     * Attempts to refresh the access token using the refresh token (if available).
     * Mencoba untuk me-refresh access token menggunakan refresh token (jika tersedia).
     *
     * This operation typically involves making a network request to a refresh token endpoint.
     * Operasi ini biasanya melibatkan pembuatan permintaan jaringan ke endpoint refresh token.
     *
     * @return A Resource object indicating the success or error of the refresh operation.
     * If successful, it should contain the new access token.
     * Objek Resource yang menunjukkan status sukses atau error dari operasi refresh.
     * Jika berhasil, seharusnya berisi access token baru.
     */
    suspend fun refreshAccessToken(): Resource<String>
}
