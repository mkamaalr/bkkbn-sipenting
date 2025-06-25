package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of the LoginUseCase.
 * This class contains the specific business logic for user login.
 */
class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {

    /**
     * Validates input and calls the repository to perform login.
     */
    override suspend fun execute(loginRequest: LoginRequest): Flow<Resource<AuthResponse>> {
        // ====================== PERBAIKAN UTAMA ADA DI SINI ======================
        //
        // Menggunakan .isNullOrBlank() yang aman untuk tipe data String? (nullable)
        // untuk mencegah NullPointerException.
        //
        if (loginRequest.email.isNullOrBlank() || loginRequest.password.isNullOrBlank()) {
            // Mengembalikan error dalam bentuk Flow agar bisa ditangkap oleh ViewModel.
            return flow {
                emit(Resource.Error("email dan password tidak boleh kosong."))
            }
        }
        // =========================================================================

        return authRepository.login(loginRequest)
    }
}
