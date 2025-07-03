package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherWithLatestStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.remote.BreastfeedingMotherApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toBreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toBreastfeedingMotherVisitEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toUploadRequest
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreastfeedingMotherRepositoryImpl @Inject constructor(
    private val breastfeedingMotherDao: BreastfeedingMotherDao,
    private val visitsDao: BreastfeedingMotherVisitsDao,
    private val apiService: BreastfeedingMotherApiService
) : BreastfeedingMotherRepository {
    override suspend fun insertBreastfeedingMother(motherEntity: BreastfeedingMotherEntity): Resource<Long> {
        return try {
            val newRowId = breastfeedingMotherDao.insertBreastfeedingMother(motherEntity)
            Resource.Success(newRowId)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data ibu hamil ke database: ${e.message}")
        }
    }

    override suspend fun insertBreastfeedingMotherVisit(visitEntity: BreastfeedingMotherVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.insertVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data kunjungan ke database: ${e.message}")
        }
    }

    override fun getAllBreastfeedingMothers(): Flow<List<BreastfeedingMotherEntity>> {
        return breastfeedingMotherDao.getAllBreastfeedingMothers()
    }

    override fun getMotherById(localId: Int): Flow<BreastfeedingMotherEntity?> {
        return breastfeedingMotherDao.getBreastfeedingMotherById(localId)
    }

    override fun getVisitsForMother(motherId: Int): Flow<List<BreastfeedingMotherVisitsEntity>> {
        return visitsDao.getVisitsForMother(motherId)
    }

    override suspend fun updateBreastfeedingMotherVisit(visitEntity: BreastfeedingMotherVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.updateVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui data kunjungan di database: ${e.message}")
        }
    }

    override fun getVisitById(visitId: Int): Flow<BreastfeedingMotherVisitsEntity?> {
        return visitsDao.getVisitById(visitId)
    }

    override fun getAllMothersWithLatestStatus(): Flow<List<BreastfeedingMotherWithLatestStatus>> {
        return breastfeedingMotherDao.getAllMothersWithLatestStatus()
    }

    override suspend fun uploadPendingData() {
        val pendingMothers = breastfeedingMotherDao.getPendingBreastfeedingMother()
        Timber.d("Uploading ${pendingMothers.size} pending breastfeeding mothers...")
        for (mother in pendingMothers) {
            try {
                val request = mother.toUploadRequest()
                val response = if (mother.id == null) {
                    apiService.createBreastfeedingMother(request)
                } else {
                    apiService.updateBreastfeedingMother(mother.id, request)
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    breastfeedingMotherDao.updateBreastfeedingMother(mother.copy(id = serverId, syncStatus = SyncStatus.DONE))
                } else {
                    Timber.e("Failed to sync breastfeeding mother with localId ${mother.localId}: ${response.message()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception while syncing breastfeeding mother with localId: ${mother.localId}")
            }
        }

        val pendingVisits = visitsDao.getPendingVisits()
        Timber.d("Uploading ${pendingVisits.size} pending breastfeeding mother visits...")
        for (visit in pendingVisits) {
            try {
                val mother = breastfeedingMotherDao.getBreastfeedingMotherById(visit.breastfeedingMotherId).first()
                if (mother?.id == null) {
                    Timber.w("Skipping visit sync for localId ${visit.localVisitId}, parent mother not synced yet.")
                    continue
                }

                val request = visit.toUploadRequest(mother.id)
                val response = if (visit.id == null) {
                    apiService.createBreastfeedingMotherVisit(request)
                } else {
                    apiService.updateBreastfeedingMotherVisit(visit.id, request)
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    visitsDao.updateVisit(visit.copy(id = serverId, syncStatus = SyncStatus.DONE))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync breastfeeding mother visit with localId: ${visit.localVisitId}")
            }
        }
    }

    override suspend fun syncFromServer() {
        try {
            Timber.d("Syncing breastfeeding mothers from server...")
            val motherResponse = apiService.getAllBreastfeedingMothers()
            if (motherResponse.isSuccessful) {
                val motherEntities = motherResponse.body()?.data?.map { it.toBreastfeedingMotherEntity().copy(syncStatus = SyncStatus.DONE) }
                if (!motherEntities.isNullOrEmpty()) {
                    breastfeedingMotherDao.clearAndInsertAll(motherEntities)
                }
            }

            Timber.d("Syncing breastfeeding mother visits from server...")
            val visitResponse = apiService.getAllBreastfeedingMotherVisits()
            if (visitResponse.isSuccessful) {
                val visitEntities = visitResponse.body()?.data?.mapNotNull { visitDto ->
                    val mother = breastfeedingMotherDao.findByServerId(visitDto.parentId)
                    if (mother != null) {
                        visitDto.toBreastfeedingMotherVisitEntity().copy(
                            breastfeedingMotherId = mother.localId,
                            syncStatus = SyncStatus.DONE
                        )
                    } else {
                        null
                    }
                }
                if (!visitEntities.isNullOrEmpty()) {
                    visitsDao.clearAndInsertAll(visitEntities)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during breastfeeding mother syncFromServer")
            throw e
        }
    }
}