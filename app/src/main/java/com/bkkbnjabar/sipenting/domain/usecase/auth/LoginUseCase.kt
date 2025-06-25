package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Login use case.
 * Defines the contract for executing the login business logic.
 */
interface LoginUseCase {
    /**
     * Executes the login process.
     * @param loginRequest The user's login credentials.
     * @return A Flow emitting the resource state of the authentication process.
     */
    suspend fun execute(loginRequest: LoginRequest): Flow<Resource<AuthResponse>>
}