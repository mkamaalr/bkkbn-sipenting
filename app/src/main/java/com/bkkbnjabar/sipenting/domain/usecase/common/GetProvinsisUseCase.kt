package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the list of all provinces.
 */
interface GetProvinsisUseCase {
    operator fun invoke(): Flow<List<Provinsi>>
}

class GetProvinsisUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetProvinsisUseCase {
    override operator fun invoke(): Flow<List<Provinsi>> {
        return repository.getAllProvinsisFromRoom()
    }
}
