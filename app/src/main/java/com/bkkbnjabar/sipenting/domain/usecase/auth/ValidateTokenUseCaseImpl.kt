package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ValidateTokenUseCase.
 * Delegates the token validation to the AuthRepository.
 */
class ValidateTokenUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : ValidateTokenUseCase {
    /**
     * Memeriksa apakah pengguna saat ini masuk (memiliki token yang valid).
     *
     * @return Resource.Success(true) jika token ada, Resource.Success(false) jika tidak ada.
     * Resource.Error jika ada masalah saat memeriksa status login.
     */
    override suspend fun execute(): Resource<Boolean> {
        return authRepository.validateToken()
    }
}
