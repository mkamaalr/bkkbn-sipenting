package com.bkkbnjabar.sipenting.domain.usecase.auth

/**
 * Interface for the ValidateToken use case.
 * Defines the contract for checking if a user session is currently valid.
 */
interface ValidateTokenUseCase {
    /**
     * Executes the token validation check.
     * @return True if the user is considered logged in, false otherwise.
     */
    fun execute(): Boolean
}
