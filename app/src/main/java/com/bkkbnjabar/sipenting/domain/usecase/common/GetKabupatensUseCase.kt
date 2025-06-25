package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to get a list of regencies (kabupaten) filtered by province ID.
 */
interface GetKabupatensUseCase {
    operator fun invoke(provinsiId: Int): Flow<List<Kabupaten>>
}

class GetKabupatensUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetKabupatensUseCase {
    override operator fun invoke(provinsiId: Int): Flow<List<Kabupaten>> {
        // Gets all regencies and then filters them in the domain layer.
        return repository.getAllKabupatensFromRoom().map { list ->
            list.filter { it.provinsiId == provinsiId }
        }
    }
}
