package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a list of districts (kecamatan) filtered by regency ID.
 */
interface GetKecamatansUseCase {
    operator fun invoke(kabupatenId: Int): Flow<List<Kecamatan>>
}

class GetKecamatansUseCaseImpl @Inject constructor(
    private val repository: LookupRepository
) : GetKecamatansUseCase {
    override operator fun invoke(kabupatenId: Int): Flow<List<Kecamatan>> {
        return repository.getKecamatansByKabupatenFromRoom(kabupatenId)
    }
}
