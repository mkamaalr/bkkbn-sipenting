package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Rw
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a list of community units (RW) filtered by village ID.
 */
interface GetRWSUseCase {
    operator fun invoke(kelurahanId: Int): Flow<List<Rw>>
}

class GetRWSUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetRWSUseCase {
    override operator fun invoke(kelurahanId: Int): Flow<List<Rw>> {
        return repository.getRWSByKelurahanFromRoom(kelurahanId)
    }
}
