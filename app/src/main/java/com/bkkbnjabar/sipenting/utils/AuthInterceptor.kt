package com.bkkbnjabar.sipenting.utils

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An OkHttp Interceptor that adds the Authorization header with a Bearer token
 * to every outgoing API request.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPrefsManager.getUserSession()?.accessToken
        val originalRequest = chain.request()

        // If a token exists, add it to the request header.
        val requestBuilder = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
        } else {
            originalRequest.newBuilder()
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
