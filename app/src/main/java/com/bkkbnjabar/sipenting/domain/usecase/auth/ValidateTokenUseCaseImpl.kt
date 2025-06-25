package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Implementation of the ValidateTokenUseCase.
 * This class contains the logic to check for an active user session.
 */
class ValidateTokenUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : ValidateTokenUseCase {

    /**
     * Checks the session status via the repository.
     */
    override fun execute(): Boolean {
        // The business logic is simply to delegate the check to the repository.
        return authRepository.isLoggedIn()
    }
}
