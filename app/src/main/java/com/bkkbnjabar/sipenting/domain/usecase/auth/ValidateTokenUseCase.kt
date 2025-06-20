package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(): Boolean {
        return authRepository.validateToken()
    }
}