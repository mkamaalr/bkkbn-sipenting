package com.bkkbnjabar.sipenting.utils

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor kustom untuk menambahkan token otentikasi (Bearer Token) ke setiap permintaan API.
 * Mengambil token dari SharedPrefsManager.
 */
@Singleton
class AuthInterceptor @Inject constructor( // <<< PASTIKAN ADA @Inject constructor DI SINI
    private val sharedPrefsManager: SharedPrefsManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = sharedPrefsManager.getAccessToken() // Menggunakan getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!accessToken.isNullOrEmpty()) { // Cek null/empty saja, tidak ada "No-Authentication"
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val modifiedRequest = requestBuilder.build()
        return chain.proceed(modifiedRequest)
    }
}
