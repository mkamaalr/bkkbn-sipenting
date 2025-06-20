package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.data.model.auth.AuthResponse
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(username: String?, email: String?, password: String): Resource<AuthResponse> {
        return authRepository.login(username, email, password)
    }
}