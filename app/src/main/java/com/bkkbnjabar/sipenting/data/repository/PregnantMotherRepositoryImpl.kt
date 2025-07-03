package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherWithLatestStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toChildMotherEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toPregnantMotherEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toPregnantMotherVisitEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toUploadRequest
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PregnantMotherRepositoryImpl @Inject constructor(
    private val pregnantMotherDao: PregnantMotherDao,
    private val visitsDao: PregnantMotherVisitsDao,
    private val apiService: PregnantMotherApiService
) : PregnantMotherRepository {

    override suspend fun insertPregnantMother(motherEntity: PregnantMotherEntity): Resource<Long> {
        return try {
            val newRowId = pregnantMotherDao.insertPregnantMother(motherEntity)
            Resource.Success(newRowId)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data ibu hamil ke database: ${e.message}")
        }
    }

    override suspend fun insertPregnantMotherVisit(visitEntity: PregnantMotherVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.insertVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data kunjungan ke database: ${e.message}")
        }
    }

    override fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>> {
        return pregnantMotherDao.getAllPregnantMothers()
    }

    override fun getMotherById(localId: Int): Flow<PregnantMotherEntity?> {
        return pregnantMotherDao.getPregnantMotherById(localId)
    }

    override fun getVisitsForMother(motherId: Int): Flow<List<PregnantMotherVisitsEntity>> {
        return visitsDao.getVisitsForMother(motherId)
    }

    override suspend fun updatePregnantMotherVisit(visitEntity: PregnantMotherVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.updateVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui data kunjungan di database: ${e.message}")
        }
    }

    override fun getVisitById(visitId: Int): Flow<PregnantMotherVisitsEntity?> {
        return visitsDao.getVisitById(visitId)
    }

    override fun getAllMothersWithLatestStatus(): Flow<List<PregnantMotherWithLatestStatus>> {
        return pregnantMotherDao.getAllMothersWithLatestStatus()
    }

    override suspend fun uploadPendingData() {
        // Step 1: Upload pending mothers
        val pendingMothers = pregnantMotherDao.getPendingPregnantMother()
        Timber.d("Uploading ${pendingMothers.size} pending pregnant mothers...")
        for (mother in pendingMothers) {
            try {
                val request = mother.toUploadRequest()
                val response = if (mother.id == null) {
                    apiService.createPregnantMother(request)
                } else {
                    apiService.updatePregnantMother(mother.id, request)
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    pregnantMotherDao.updatePregnantMother(mother.copy(id = serverId, syncStatus = SyncStatus.DONE))
                } else {
                    Timber.e("Failed to sync pregnant mother with localId ${mother.localId}: ${response.message()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception while syncing pregnant mother with localId: ${mother.localId}")
            }
        }

        // Step 2: Upload pending visits for mothers that are already synced
        val pendingVisits = visitsDao.getPendingVisits()
        Timber.d("Uploading ${pendingVisits.size} pending pregnant mother visits...")
        for (visit in pendingVisits) {
            try {
                val mother = pregnantMotherDao.getPregnantMotherById(visit.pregnantMotherLocalId).first()
                if (mother?.id == null) {
                    Timber.w("Skipping visit sync for localId ${visit.localVisitId}, parent mother not synced yet.")
                    continue
                }

                val request = visit.toUploadRequest(mother.id)
                val response = if (visit.id == null) {
                    apiService.createPregnantMotherVisit(request)
                } else {
                    apiService.updatePregnantMotherVisit(visit.id, request)
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    visitsDao.updateVisit(visit.copy(id = serverId, syncStatus = SyncStatus.DONE))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync pregnant mother visit with localId: ${visit.localVisitId}")
            }
        }
    }

    override suspend fun syncFromServer() {
        try {
            // Sync Mothers
            Timber.d("Syncing pregnant mothers from server...")
            val motherResponse = apiService.getAllPregnantMothers()
            if (motherResponse.isSuccessful) {
                val motherEntities = motherResponse.body()?.data?.map { it.toPregnantMotherEntity().copy(syncStatus = SyncStatus.DONE) }
                if (!motherEntities.isNullOrEmpty()) {
                    pregnantMotherDao.clearAndInsertAll(motherEntities)
                }
            }

            // Sync Visits
            Timber.d("Syncing pregnant mother visits from server...")
            val visitResponse = apiService.getAllPregnantMotherVisits()
            if (visitResponse.isSuccessful) {
                val visitEntities = visitResponse.body()?.data?.mapNotNull { visitDto ->
                    val mother = pregnantMotherDao.findByServerId(visitDto.parentId)
                    if (mother != null) {
                        visitDto.toPregnantMotherVisitEntity().copy(
                            pregnantMotherLocalId = mother.localId,
                            syncStatus = SyncStatus.DONE
                        )
                    } else {
                        null // Can't save a visit if its mother isn't in the local DB
                    }
                }
                if (!visitEntities.isNullOrEmpty()) {
                    visitsDao.clearAndInsertAll(visitEntities)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during pregnant mother syncFromServer")
            throw e
        }
    }
}
