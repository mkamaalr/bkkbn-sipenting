package com.bkkbnjabar.sipenting.utils

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val authRepositoryProvider: Provider<AuthRepository>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Hanya coba lagi jika kita mendapatkan 401 dan ada token untuk disegarkan
        if (response.request.header("Authorization") == null || response.code != 401) {
            return null // Bukan 401 tidak sah atau tidak ada token dalam permintaan asli
        }

        // Hindari pengulangan tak terbatas jika refresh token API juga mengembalikan 401.
        // Periksa apakah token yang digunakan dalam permintaan yang gagal masih sama dengan token yang disimpan.
        // Jika berbeda, berarti thread lain sudah berhasil menyegarkan token.
        val currentTokenInPrefs = sharedPrefsManager.getAuthToken()
        if (currentTokenInPrefs == null || response.request.header("Authorization") != "Bearer $currentTokenInPrefs") {
            return null // Token sudah diubah oleh thread lain atau tidak ada token
        }

        // Dapatkan instance AuthRepository melalui Provider
        val authRepository = authRepositoryProvider.get() // Sekarang kita mendapatkan instance AuthRepository

        // Segarkan token secara sinkron
        val newToken = runBlocking {
            authRepository.refreshToken() // Panggil metode pada instance yang didapat
        }

        return if (newToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            // Penyegaran token gagal, pengguna perlu autentikasi ulang
            sharedPrefsManager.clearAuthData() // Pastikan data autentikasi dihapus
            null
        }
    }
}