package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Rt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a list of neighborhood units (RT) filtered by community unit ID.
 */
interface GetRTSUseCase {
    operator fun invoke(rwId: Int): Flow<List<Rt>>
}

class GetRTSUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetRTSUseCase {
    override operator fun invoke(rwId: Int): Flow<List<Rt>> {
        return repository.getRTSByRwFromRoom(rwId)
    }
}
