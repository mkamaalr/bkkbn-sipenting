package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.mapper.toPregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toPregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.flow // Penting: Import flow builder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PregnantMotherRepositoryImpl @Inject constructor(
    private val pregnantMotherDao: PregnantMotherDao
) : PregnantMotherRepository {

    override suspend fun createPregnantMother(data: PregnantMotherRegistrationData): Resource<String> {
        return try {
            val entity = data.toPregnantMotherEntity()
            Log.d("PMR_REPO", "createPregnantMother: Processing data with localId: ${entity.localId}")

            val resultId = if (entity.localId == null || entity.localId == 0) {
                val newId = pregnantMotherDao.insertPregnantMother(entity)
                Log.d("PMR_REPO", "createPregnantMother: Inserted new record with localId: $newId")
                newId
            } else {
                pregnantMotherDao.updatePregnantMother(entity)
                Log.d("PMR_REPO", "createPregnantMother: Updated existing record with localId: ${entity.localId}")
                entity.localId.toLong()
            }
            Resource.Success("Data ibu hamil berhasil disimpan dengan ID: $resultId")
        } catch (e: Exception) {
            Log.e("PMR_REPO", "Error saving/updating pregnant mother: ${e.message}", e)
            Resource.Error("Gagal menyimpan/memperbarui data ibu hamil: ${e.localizedMessage}")
        }
    }

    override suspend fun updatePregnantMother(
        localId: Int,
        data: PregnantMotherRegistrationData
    ): Resource<String> {
        TODO("Not yet implemented")
    }

    // --- FIX DI SINI ---
    override fun getAllPregnantMothers(): Flow<Resource<List<PregnantMotherRegistrationData>>> = flow {
        // 1. Memancarkan status Loading terlebih dahulu
        emit(Resource.Loading())
        Log.d("PMR_REPO", "getAllPregnantMothers: Emitting Loading state.")

        try {
            // 2. Mengambil data dari DAO dan memetakannya
            pregnantMotherDao.getAllPregnantMothers()
                .map { entities ->
                    // Konversi entities ke data model
                    entities.map { it.toPregnantMotherRegistrationData() }
                }
                .collect { data ->
                    // 3. Memancarkan data yang berhasil dalam Resource.Success
                    emit(Resource.Success(data))
                    Log.d("PMR_REPO", "getAllPregnantMothers: Emitting Success state with ${data.size} items.")
                }
        } catch (e: Exception) {
            // 4. Menangkap error dan memancarkan Resource.Error
            emit(Resource.Error("Gagal memuat daftar ibu hamil: ${e.localizedMessage}"))
            Log.e("PMR_REPO", "Error getting all pregnant mothers: ${e.message}", e)
        }
    }

    override suspend fun uploadPendingPregnantMothers(): Resource<String> {
        TODO("Not yet implemented")
    }
    // --- FIX BERAKHIR DI SINI ---


    override suspend fun getPregnantMotherById(localId: Int): Resource<PregnantMotherRegistrationData> {
        return try {
            val entity = pregnantMotherDao.getPregnantMotherById(localId)
            if (entity != null) {
                Resource.Success(entity.toPregnantMotherRegistrationData())
            } else {
                Resource.Error("Data ibu hamil dengan ID $localId tidak ditemukan.")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal mengambil data ibu hamil: ${e.localizedMessage}")
        }
    }

    override suspend fun deletePregnantMother(localId: Int): Resource<String> {
        return try {
            pregnantMotherDao.deletePregnantMother(localId)
            Resource.Success("Data ibu hamil berhasil dihapus.")
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus data ibu hamil: ${e.localizedMessage}")
        }
    }
}
