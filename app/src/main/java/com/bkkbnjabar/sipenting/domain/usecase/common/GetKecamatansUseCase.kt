package com.bkkbnjabar.sipenting.domain.usecase.common

import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Import map operator
import javax.inject.Inject

/**
 * Use case untuk mendapatkan daftar Kecamatan dari database lokal (Room).
 * Data ini diasumsikan sudah di-preload di SplashScreen.
 */
class GetKecamatansUseCase @Inject constructor(
    private val lookupRepository: LookupRepository
) {
    /**
     * Mengeksekusi use case untuk mendapatkan semua data Kecamatan.
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(): Flow<Resource<List<Kecamatan>>> {
        return lookupRepository.getAllKecamatansFromRoom().map { listKecamatan ->
            Resource.Success(listKecamatan)
        }
        // Anda juga bisa menambahkan Resource.Loading() sebelum map jika diperlukan
        // untuk mengindikasikan bahwa data sedang dimuat dari Room (walaupun cepat).
        // return flow {
        //     emit(Resource.Loading())
        //     lookupRepository.getAllKecamatansFromRoom().collect { listKecamatan ->
        //         emit(Resource.Success(listKecamatan))
        //     }
        // }
    }

    /**
     * Mengeksekusi use case untuk mendapatkan daftar Kecamatan berdasarkan ID Kabupaten dari database lokal (Room).
     * Mengembalikan Flow of Resource yang menunjukkan status loading, sukses, atau error.
     */
    operator fun invoke(kabupatenId: Int): Flow<Resource<List<Kecamatan>>> {
        return lookupRepository.getKecamatansByKabupatenFromRoom(kabupatenId).map { listKecamatan ->
            Resource.Success(listKecamatan)
        }
    }

    // Memanggil API tanpa parameter karena API mengembalikan semua kecamatan
    suspend fun execute(): Resource<List<Kecamatan>> {
        return lookupRepository.getKecamatansFromApi()
    }
}
