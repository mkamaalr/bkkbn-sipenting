package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadResponse // Pastikan ini diimpor
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PregnantMotherRepositoryImpl @Inject constructor(
    private val pregnantMotherDao: PregnantMotherDao, // DAO untuk operasi Room
    private val pregnantMotherApiService: PregnantMotherApiService // API Service untuk operasi server
) : PregnantMotherRepository {

    /**
     * Menyimpan data ibu hamil yang baru ke database lokal (Room) dengan status PENDING_UPLOAD.
     * Ini memastikan data tersedia secara offline segera.
     */
    override suspend fun createPregnantMother(data: PregnantMotherRegistrationData): Resource<String> {
        return try {
            // Konversi domain model ke Room Entity baru. localId akan di-auto-generate.
            val entity = data.toNewEntity()
            val localId = pregnantMotherDao.insert(entity)
            Resource.Success("Pendaftaran ibu hamil berhasil disimpan secara lokal (ID: $localId). Menunggu upload.")
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan pendaftaran ibu hamil secara lokal: ${e.localizedMessage}")
        }
    }

    /**
     * Memperbarui data ibu hamil yang sudah ada di database lokal (Room).
     * Jika data sudah diunggah sebelumnya, statusnya akan diubah menjadi PENDING_UPLOAD jika ada perubahan.
     */
    override suspend fun updatePregnantMother(localId: Int, data: PregnantMotherRegistrationData): Resource<String> {
        return try {
            val existingEntity = pregnantMotherDao.getPregnantMotherById(localId)
            if (existingEntity == null) {
                return Resource.Error("Data ibu hamil dengan ID lokal $localId tidak ditemukan untuk diperbarui.")
            }

            // Perbarui entitas yang sudah ada dengan data baru dari domain model
            val updatedEntity = existingEntity.copy(
                name = data.name,
                nik = data.nik,
                dateOfBirth = data.dateOfBirth,
                phoneNumber = data.phoneNumber,
                husbandName = data.husbandName,
                fullAddress = data.fullAddress,
                registrationDate = data.registrationDate, // Pastikan ini juga diupdate

                provinsiName = data.provinsiName,
                provinsiId = data.provinsiId,
                kabupatenName = data.kabupatenName,
                kabupatenId = data.kabupatenId,
                kecamatanName = data.kecamatanName,
                kecamatanId = data.kecamatanId,
                kelurahanName = data.kelurahanName,
                kelurahanId = data.kelurahanId,
                rwName = data.rwName,
                rwId = data.rwId,
                rtName = data.rtName,
                rtId = data.rtId,

                updatedAt = System.currentTimeMillis(),
                // Ubah status menjadi PENDING_UPLOAD jika data berubah dan sebelumnya sudah UPLOADED
                syncStatus = if (existingEntity.syncStatus == SyncStatus.UPLOADED) SyncStatus.PENDING_UPLOAD else existingEntity.syncStatus
            )
            pregnantMotherDao.update(updatedEntity)
            Resource.Success("Data ibu hamil (ID: $localId) berhasil diperbarui secara lokal.")
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui pendaftaran ibu hamil secara lokal: ${e.localizedMessage}")
        }
    }

    /**
     * Menghapus data ibu hamil dari database lokal (Room).
     */
    override suspend fun deletePregnantMother(localId: Int): Resource<String> {
        return try {
            pregnantMotherDao.deletePregnantMother(localId)
            Resource.Success("Data ibu hamil (ID: $localId) berhasil dihapus secara lokal.")
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus data ibu hamil lokal: ${e.localizedMessage}")
        }
    }

    /**
     * Mendapatkan semua data ibu hamil dari database lokal (Room) sebagai Flow.
     * Data yang dikembalikan adalah model domain (PregnantMotherRegistrationData)
     * sehingga UI tidak perlu tahu tentang Room Entity.
     */
    override fun getAllPregnantMothers(): Flow<Resource<List<PregnantMotherRegistrationData>>> {
        return pregnantMotherDao.getAllPregnantMothers().map { entities ->
            val domainList = entities.map { it.toDomainModel() } // Konversi Room Entity ke domain model
            Resource.Success(domainList)
        }
    }

    /**
     * Mengunggah data ibu hamil yang berstatus PENDING_UPLOAD atau ERROR_UPLOAD ke server.
     * Setelah berhasil diunggah, status di database lokal akan diperbarui menjadi UPLOADED.
     */
    override suspend fun uploadPendingPregnantMothers(): Resource<String> {
        val pendingUploads = pregnantMotherDao.getPregnantMothersBySyncStatus(SyncStatus.PENDING_UPLOAD) +
                pregnantMotherDao.getPregnantMothersBySyncStatus(SyncStatus.ERROR_UPLOAD)

        if (pendingUploads.isEmpty()) {
            return Resource.Success("Tidak ada data ibu hamil yang tertunda untuk diunggah.")
        }

        var successCount = 0
        var errorCount = 0
        val errorMessages = mutableListOf<String>()

        for (entity in pendingUploads) {
            try {
                // Konversi Room Entity ke DTO yang sesuai untuk permintaan API upload
                val uploadRequest = entity.toUploadRequest()
                val response = pregnantMotherApiService.uploadPregnantMother(uploadRequest)

                if (response.isSuccessful) {
                    val serverResponse = response.body()
                    // PERBAIKAN: Pastikan serverId diambil dengan benar dan cocok tipenya (Int?)
                    val serverId = serverResponse?.id?.toIntOrNull() // Menggunakan toIntOrNull()
                    val updatedEntity = entity.copy(syncStatus = SyncStatus.UPLOADED, serverId = serverId)
                    pregnantMotherDao.update(updatedEntity) // Perbarui status di lokal
                    successCount++
                } else {
                    val errorMessage = "Gagal mengunggah data ID lokal ${entity.localId}: ${response.message() ?: response.errorBody()?.string() ?: "Unknown error"}"
                    val updatedEntity = entity.copy(syncStatus = SyncStatus.ERROR_UPLOAD)
                    pregnantMotherDao.update(updatedEntity)
                    errorCount++
                    errorMessages.add(errorMessage)
                }
            } catch (e: IOException) {
                val errorMessage = "Gagal mengunggah data ID lokal ${entity.localId} (Jaringan): ${e.localizedMessage}"
                val updatedEntity = entity.copy(syncStatus = SyncStatus.ERROR_UPLOAD)
                pregnantMotherDao.update(updatedEntity)
                errorCount++
                errorMessages.add(errorMessage)
            } catch (e: HttpException) {
                val errorMessage = "Gagal mengunggah data ID lokal ${entity.localId} (Server): ${e.localizedMessage}"
                val updatedEntity = entity.copy(syncStatus = SyncStatus.ERROR_UPLOAD)
                pregnantMotherDao.update(updatedEntity)
                errorCount++
                errorMessages.add(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Gagal mengunggah data ID lokal ${entity.localId} (Umum): ${e.localizedMessage}"
                val updatedEntity = entity.copy(syncStatus = SyncStatus.ERROR_UPLOAD)
                pregnantMotherDao.update(updatedEntity)
                errorCount++
                errorMessages.add(errorMessage)
            }
        }

        return if (errorCount == 0) {
            Resource.Success("$successCount data ibu hamil berhasil diunggah.")
        } else {
            Resource.Error("$successCount berhasil diunggah, $errorCount gagal. Detail: ${errorMessages.joinToString("; ")}")
        }
    }

    /**
     * Mendapatkan detail ibu hamil berdasarkan ID lokal dari database Room.
     */
    override suspend fun getPregnantMotherById(localId: Int): Resource<PregnantMotherRegistrationData> {
        return try {
            val entity = pregnantMotherDao.getPregnantMotherById(localId)
            if (entity != null) {
                Resource.Success(entity.toDomainModel())
            } else {
                Resource.Error("Data ibu hamil dengan ID lokal $localId tidak ditemukan.")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal mendapatkan data ibu hamil lokal: ${e.localizedMessage}")
        }
    }

    // --- Fungsi Pemetaan (Mapper Functions) ---
    // Fungsi ekstensi untuk mengonversi PregnantMotherRegistrationData (domain model) ke PregnantMotherEntity (Room entity)
    // untuk entitas BARU (dengan localId otomatis)
    private fun PregnantMotherRegistrationData.toNewEntity(): PregnantMotherEntity {
        return PregnantMotherEntity(
            // localId akan di-auto-generate oleh Room
            // serverId akan null saat pertama disimpan
            // syncStatus sudah default PENDING_UPLOAD
            name = this.name,
            nik = this.nik,
            dateOfBirth = this.dateOfBirth,
            phoneNumber = this.phoneNumber,
            husbandName = this.husbandName,
            fullAddress = this.fullAddress,
            registrationDate = this.registrationDate,

            provinsiName = this.provinsiName,
            provinsiId = this.provinsiId,
            kabupatenName = this.kabupatenName,
            kabupatenId = this.kabupatenId,
            kecamatanName = this.kecamatanName,
            kecamatanId = this.kecamatanId,
            kelurahanName = this.kelurahanName,
            kelurahanId = this.kelurahanId,
            rwName = this.rwName,
            rwId = this.rwId,
            rtName = this.rtName,
            rtId = this.rtId,

            syncStatus = SyncStatus.PENDING_UPLOAD, // Selalu PENDING_UPLOAD saat membuat baru
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    // Fungsi ekstensi untuk mengonversi PregnantMotherEntity (Room entity) ke PregnantMotherRegistrationData (domain model)
    private fun PregnantMotherEntity.toDomainModel(): PregnantMotherRegistrationData {
        return PregnantMotherRegistrationData(
            localId = this.localId.toLong(), // Map localId
            remoteId = this.serverId?.toString(), // Map serverId ke remoteId
            syncStatus = this.syncStatus, // Map syncStatus

            name = this.name,
            nik = this.nik,
            dateOfBirth = this.dateOfBirth,
            phoneNumber = this.phoneNumber,
            husbandName = this.husbandName,
            fullAddress = this.fullAddress,
            registrationDate = this.registrationDate,

            provinsiName = this.provinsiName,
            provinsiId = this.provinsiId,
            kabupatenName = this.kabupatenName,
            kabupatenId = this.kabupatenId,
            kecamatanName = this.kecamatanName,
            kecamatanId = this.kecamatanId,
            kelurahanName = this.kelurahanName,
            kelurahanId = this.kelurahanId,
            rwName = this.rwName,
            rwId = this.rwId,
            rtName = this.rtName,
            rtId = this.rtId
        )
    }

    // Fungsi ekstensi untuk mengonversi PregnantMotherEntity (Room entity) ke PregnantMotherUploadRequest (DTO untuk API)
    private fun PregnantMotherEntity.toUploadRequest(): PregnantMotherUploadRequest {
        return PregnantMotherUploadRequest(
            name = this.name ?: "", // Handle nullability jika model domain mengizinkan null
            nik = this.nik ?: "",
            dateOfBirth = this.dateOfBirth ?: "",
            phoneNumber = this.phoneNumber ?: "",
            kecamatanId = this.kecamatanId,
            kelurahanId = this.kelurahanId,
            rwId = this.rwId,
            rtId = this.rtId,
            fullAddress = this.fullAddress ?: "",
            registrationDate = this.registrationDate ?: "",
            husbandName = this.husbandName ?: ""
            // Tambahkan bidang lain yang diperlukan oleh API Anda
        )
    }
}
