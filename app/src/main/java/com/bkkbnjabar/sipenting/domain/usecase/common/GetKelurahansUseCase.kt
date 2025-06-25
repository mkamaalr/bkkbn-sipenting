package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a list of villages (kelurahan) filtered by district ID.
 */
interface GetKelurahansUseCase {
    operator fun invoke(kecamatanId: Int): Flow<List<Kelurahan>>
}

class GetKelurahansUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetKelurahansUseCase {
    override operator fun invoke(kecamatanId: Int): Flow<List<Kelurahan>> {
        return repository.getKelurahansByKecamatanFromRoom(kecamatanId)
    }
}
