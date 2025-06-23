package com.bkkbnjabar.sipenting.utils

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.runBlocking // Import runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor( // <<< PASTIKAN ADA @Inject constructor DI SINI
    private val authRepositoryProvider: Provider<AuthRepository>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Logika ini sangat penting untuk mencegah loop tak terbatas
        // jika otentikasi itu sendiri gagal (misalnya, refresh token juga tidak valid).
        // Pastikan kita tidak mencoba me-refresh token untuk permintaan refresh token itu sendiri
        // atau untuk permintaan yang secara eksplisit ditandai tanpa otentikasi.
        if (response.request.header("No-Authentication") != null || isRefreshRequest(response.request)) {
            return null // Jangan coba merefresh jika request adalah pengecualian
        }

        // Dapatkan instansi AuthRepository secara lazy
        // Ini aman karena kita menggunakan Provider, menghindari circular dependency
        val authRepository = authRepositoryProvider.get()

        // Menggunakan synchronized untuk memastikan hanya satu thread yang mencoba merefresh token
        // pada satu waktu. Ini mencegah beberapa permintaan refresh token yang tidak perlu
        // jika banyak permintaan 401 terjadi secara bersamaan.
        synchronized(this) {
            // Dapatkan token akses terbaru setelah mungkin sudah di-refresh oleh thread lain
            val latestAccessToken = runBlocking { authRepository.getAccessToken() } // Asumsi ada fungsi ini di AuthRepository
            val originalRequestAuthHeader = response.request.header("Authorization")

            // Jika token sudah di-refresh oleh thread lain
            if (latestAccessToken != null && "Bearer $latestAccessToken" != originalRequestAuthHeader) {
                // Coba ulang permintaan asli dengan token yang baru di-refresh
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestAccessToken")
                    .build()
            }

            // Jika token belum berubah (atau null), artinya thread ini harus mencoba me-refresh
            // Panggil fungsi refreshAccessToken di AuthRepository.
            // Gunakan runBlocking karena authenticate() bukanlah fungsi suspend.
            val refreshResult = runBlocking {
                authRepository.refreshAccessToken() // Asumsi fungsi ini ada dan mengembalikan Resource<String> (token baru)
            }

            return when (refreshResult) {
                is Resource.Success -> {
                    val newAccessToken = refreshResult.data // Ambil token baru dari hasil sukses
                    if (!newAccessToken.isNullOrEmpty()) { // Pastikan token baru tidak null/kosong
                        // Bangun request baru dengan token yang baru
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        // Tidak ada token baru setelah refresh berhasil, ada yang salah
                        // Log out pengguna
                        runBlocking { authRepository.clearSession() }
                        null // Gagal otentikasi, tidak bisa melanjutkan request
                    }
                }
                is Resource.Error -> {
                    // Refresh token gagal (misalnya refresh token juga kadaluarsa atau tidak valid).
                    // Log out pengguna agar bisa login ulang.
                    runBlocking { authRepository.clearSession() }
                    null // Gagal otentikasi, tidak bisa melanjutkan request
                }
                Resource.Idle, is Resource.Loading -> {
                    // Kondisi ini seharusnya tidak terjadi dalam Authenticator
                    // karena kita memanggil fungsi suspend secara langsung.
                    // Jika terjadi, ini menandakan masalah dalam implementasi refresh token atau flow.
                    null
                }
            }
        }
    }

    /**
     * Helper function to check if the current request is the refresh token request itself.
     * This prevents infinite loops if the refresh endpoint itself returns 401.
     *
     * Fungsi pembantu untuk memeriksa apakah permintaan saat ini adalah permintaan refresh token itu sendiri.
     * Ini mencegah loop tak terbatas jika endpoint refresh itu sendiri mengembalikan 401.
     */
    private fun isRefreshRequest(request: Request): Boolean {
        // Sesuaikan URL ini dengan endpoint refresh token Anda yang sebenarnya
        // Misalnya, jika endpoint refresh adalah /api/auth/refresh-token
        return request.url.encodedPath.contains("api/auth/refresh-token")
    }
}