package com.bkkbnjabar.sipenting.domain.repository

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PregnantMotherRepository {
    // Fungsi untuk membuat data Ibu Hamil baru, baik secara lokal maupun ke server.
    // Mengembalikan Resource<String> untuk indikasi sukses/gagal dan pesan.
    suspend fun createPregnantMother(data: PregnantMotherRegistrationData): Resource<String>

    // Fungsi untuk memperbarui data Ibu Hamil yang sudah ada.
    suspend fun updatePregnantMother(localId: Int, data: PregnantMotherRegistrationData): Resource<String>

    // Fungsi untuk menghapus data Ibu Hamil.
    suspend fun deletePregnantMother(localId: Int): Resource<String>

    // Fungsi untuk mendapatkan semua data Ibu Hamil.
    // Mengembalikan Flow untuk observasi perubahan data secara real-time.
    // Data yang dikembalikan adalah PregnantMotherRegistrationData,
    // yang akan menjadi model gabungan dari Entity dan mungkin respons API jika diperlukan.
    fun getAllPregnantMothers(): Flow<Resource<List<PregnantMotherRegistrationData>>>

    // Fungsi untuk mengunggah data Ibu Hamil yang tertunda ke server.
    suspend fun uploadPendingPregnantMothers(): Resource<String>

    // Fungsi untuk mendapatkan detail Ibu Hamil berdasarkan ID lokal (misalnya untuk edit)
    suspend fun getPregnantMotherById(localId: Int): Resource<PregnantMotherRegistrationData>
}
