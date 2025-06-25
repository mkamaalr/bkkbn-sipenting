package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toDomain
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sharedPrefsManager: SharedPrefsManager
) : AuthRepository {

    override suspend fun login(loginRequest: LoginRequest): Flow<Resource<AuthResponse>> {
        return flow {
            // ====================== PERBAIKAN DI SINI ======================
            emit(Resource.Loading) // Menghapus tanda kurung ()
            // ===============================================================
            try {
                val response = authApiService.login(loginRequest)
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!.toDomain()
                    sharedPrefsManager.saveUserSession(authResponse.userSession)
                    emit(Resource.Success(authResponse))
                    Log.d("AuthRepoImpl", "Login berhasil, sesi pengguna disimpan dengan kelurahanId: ${authResponse.userSession.kelurahanId}")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login gagal, silakan coba lagi."
                    emit(Resource.Error(errorMsg))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("Tidak dapat terhubung ke server."))
            } catch (e: IOException) {
                emit(Resource.Error("Masalah jaringan. Periksa koneksi internet Anda."))
            } catch (e: Exception) {
                emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

    override fun logout() {
        sharedPrefsManager.clear()
    }

    override fun isLoggedIn(): Boolean {
        return sharedPrefsManager.getUserSession() != null
    }

    override fun getToken(): String? {
        return sharedPrefsManager.getUserSession()?.accessToken
    }

    override fun getKelurahanId(): Int? {
        return sharedPrefsManager.getUserSession()?.kelurahanId
    }

    override fun getUserSession(): UserSession? {
        return sharedPrefsManager.getUserSession()
    }
}
