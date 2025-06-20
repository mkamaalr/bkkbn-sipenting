package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case untuk mendapatkan daftar Kabupaten dari database lokal (Room).
 * Data ini diasumsikan sudah di-preload di SplashScreen.
 */
class GetKabupatensUseCase @Inject constructor(
    private val lookupRepository: LookupRepository
) {
    /**
     * Mengeksekusi use case untuk mendapatkan semua data Kabupaten.
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(): Flow<Resource<List<Kabupaten>>> {
        return lookupRepository.getAllKabupatensFromRoom().map { listKabupaten ->
            Resource.Success(listKabupaten)
        }
    }
}
