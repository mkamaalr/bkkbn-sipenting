package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case untuk mendapatkan daftar Kelurahan dari database lokal (Room).
 * Data ini diasumsikan sudah di-preload di SplashScreen.
 */
class GetKelurahansUseCase @Inject constructor(
    private val lookupRepository: LookupRepository
) {
    /**
     * Mengeksekusi use case untuk mendapatkan semua data Kelurahan.
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(): Flow<Resource<List<Kelurahan>>> {
        return lookupRepository.getAllKelurahansFromRoom().map { listKelurahan ->
            Resource.Success(listKelurahan)
        }
    }

    /**
     * Mengeksekusi use case untuk mendapatkan daftar Kelurahan berdasarkan ID Kecamatan dari database lokal (Room).
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(kecamatanId: Int): Flow<Resource<List<Kelurahan>>> {
        return lookupRepository.getKelurahansByKecamatanFromRoom(kecamatanId).map { listKelurahan ->
            Resource.Success(listKelurahan)
        }
    }
}
