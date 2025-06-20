package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.model.Rt
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case untuk mendapatkan daftar RT dari database lokal (Room).
 * Data ini diasumsikan sudah di-preload di SplashScreen.
 */
class GetRTSUseCase @Inject constructor(
    private val lookupRepository: LookupRepository
) {
    /**
     * Mengeksekusi use case untuk mendapatkan semua data RT.
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(): Flow<Resource<List<Rt>>> {
        return lookupRepository.getAllRTSFromRoom().map { listRt ->
            Resource.Success(listRt)
        }
    }

    /**
     * Mengeksekusi use case untuk mendapatkan daftar RT berdasarkan ID RW dari database lokal (Room).
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(rwId: Int): Flow<Resource<List<Rt>>> {
        return lookupRepository.getRTSByRwFromRoom(rwId).map { listRt ->
            Resource.Success(listRt)
        }
    }
}
