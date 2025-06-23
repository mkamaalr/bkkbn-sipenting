package com.bkkbnjabar.sipenting.domain.usecase.auth

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

interface ValidateTokenUseCase {
    suspend fun execute(): Resource<Boolean>
}