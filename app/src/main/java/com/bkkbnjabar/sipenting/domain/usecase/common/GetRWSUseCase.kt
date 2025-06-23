package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case untuk mendapatkan daftar RW dari database lokal (Room).
 * Data ini diasumsikan sudah di-preload di SplashScreen.
 */
class GetRWSUseCase @Inject constructor(
    private val lookupRepository: LookupRepository
) {
    /**
     * Mengeksekusi use case untuk mendapatkan semua data RW.
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(): Flow<Resource<List<Rw>>> {
        return lookupRepository.getAllRWSFromRoom().map { listRw ->
            Resource.Success(listRw)
        }
    }

    /**
     * Mengeksekusi use case untuk mendapatkan daftar RW berdasarkan ID Kelurahan dari database lokal (Room).
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(kelurahanId: Int): Flow<Resource<List<Rw>>> {
        return lookupRepository.getRWSByKelurahanFromRoom(kelurahanId).map { listRw ->
            Resource.Success(listRw)
        }
    }
}
