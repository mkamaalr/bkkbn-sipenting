package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {
    /**
     * Executes the login operation.
     * Mengesekusi operasi login.
     *
     * Delegates the login request to the AuthRepository and returns the result as a Resource.
     * Mendelegasikan permintaan login ke AuthRepository dan mengembalikan hasilnya sebagai Resource.
     *
     * @param request The LoginRequest containing the user's credentials.
     * Permintaan Login yang berisi kredensial pengguna.
     * @return A Resource object indicating the outcome of the login attempt.
     * Objek Resource yang menunjukkan hasil dari upaya login.
     */
    override suspend fun execute(request: LoginRequest): Resource<UserSession> {
        return authRepository.login(request)
    }
}