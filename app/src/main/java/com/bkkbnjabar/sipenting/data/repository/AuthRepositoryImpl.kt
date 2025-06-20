package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.model.auth.AuthResponse
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val sharedPrefsManager: SharedPrefsManager
) : AuthRepository {

    override suspend fun login(username: String?, email: String?, password: String): Resource<AuthResponse> {
        return try {
            val request = LoginRequest(username, email, password)
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    sharedPrefsManager.saveAuthToken(authResponse.accessToken)
                    sharedPrefsManager.saveUserId(authResponse.user.id.toString())
                    Resource.Success(authResponse)
                } ?: Resource.Error("Terjadi kesalahan tidak dikenal")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    // Coba parsing body error jika itu adalah struktur error yang diketahui
                    // Asumsi AuthResponse Anda juga dapat digunakan untuk pesan error, atau buat ErrorResponseDto spesifik
                    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                    val adapter = moshi.adapter(AuthResponse::class.java) // Sesuaikan jika Anda memiliki ErrorResponseDto umum
                    adapter.fromJson(errorBody ?: "")?.message ?: "Login gagal: ${response.message()}"
                } catch (e: Exception) {
                    "Login gagal: ${response.message()} (Gagal parsing respons)"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: IOException) {
            Resource.Error("Tidak dapat mencapai server. Periksa koneksi internet Anda.")
        } catch (e: HttpException) {
            Resource.Error("Terjadi kesalahan tak terduga: ${e.message()}")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            val response = api.logout()
            if (response.isSuccessful) {
                sharedPrefsManager.clearAuthData()
                Resource.Success(Unit)
            } else {
                Resource.Error("Logout gagal: ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Tidak dapat mencapai server. Periksa koneksi internet Anda.")
        } catch (e: HttpException) {
            Resource.Error("Terjadi kesalahan tak terduga: ${e.message()}")
        }
    }

    override suspend fun refreshToken(): String? {
        return try {
            val response = api.refreshToken()
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    sharedPrefsManager.saveAuthToken(authResponse.accessToken)
                    authResponse.accessToken
                }
            } else {
                sharedPrefsManager.clearAuthData() // Hapus token lama jika penyegaran gagal
                null
            }
        } catch (e: Exception) {
            sharedPrefsManager.clearAuthData()
            null
        }
    }

    override suspend fun validateToken(): Boolean {
        val token = sharedPrefsManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            return false
        }
        // Dalam aplikasi nyata, Anda mungkin memiliki endpoint API khusus seperti /api/v1/user atau /api/v1/validate-token
        // Untuk kesederhanaan, kita akan mengasumsikan permintaan dummy yang berhasil (misalnya, ke /user atau endpoint yang diautentikasi)
        // menunjukkan token yang valid atau dapat disegarkan. Solusi yang lebih kuat akan memanggil endpoint validasi khusus.
        return try {
            // Ini adalah placeholder. Anda idealnya harus memanggil endpoint yang diautentikasi yang mengembalikan informasi pengguna,
            // atau endpoint validasi token tertentu yang disediakan oleh API Laravel Anda.
            // Jika panggilan berhasil, berarti token valid/disegarkan. Jika gagal (misalnya, 401), berarti tidak valid.
            // TODO: Check this one, turning off logout
//            val response = api.logout() // Menggunakan logout hanya sebagai panggilan terautentikasi dummy. GANTI INI.
//            response.isSuccessful // Jika ini berhasil, berarti token valid (atau disegarkan oleh Authenticator)
            return true
        } catch (e: Exception) {
            sharedPrefsManager.clearAuthData() // Hapus token jika validasi gagal
            false
        }
    }
}