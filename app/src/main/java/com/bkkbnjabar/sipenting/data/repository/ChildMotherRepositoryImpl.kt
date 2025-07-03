package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.ChildMotherDao
import com.bkkbnjabar.sipenting.data.local.entity.ChildMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.remote.ChildApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toChildMotherEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toUploadRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildMotherRepositoryImpl @Inject constructor(
    private val childMotherDao: ChildMotherDao,
    private val apiService: ChildApiService
) : ChildMotherRepository {

    override suspend fun insertMother(mother: ChildMotherEntity): Long {
        return childMotherDao.insertMother(mother)
    }

    override fun getMotherById(localId: Int): Flow<ChildMotherEntity?> {
        return childMotherDao.getMotherById(localId)
    }

    override fun getAllMothers(): Flow<List<ChildMotherEntity>> {
        return childMotherDao.getAllMothers()
    }

    override suspend fun uploadPendingData() {
        val pendingMothers = childMotherDao.getPendingMothers()
        pendingMothers.forEach { mother ->
            try {
                val request = mother.toUploadRequest()
                val response = if (mother.id == null) {
                    // It's a new record, POST it
                    apiService.createMother(request)
                } else {
                    // It's an existing record, PUT to update
                    apiService.updateMother(mother.id, request)
                }

                if (response.isSuccessful && response.body() != null) {
                    val serverId = response.body()?.data?.id
                    // Update the local record with the server ID and set status to DONE
                    childMotherDao.updateMother(mother.copy(id = serverId, syncStatus = SyncStatus.DONE))
                }
            } catch (e: Exception) {
                // Log the error but continue trying to sync other records
            }
        }
    }

    override suspend fun syncFromServer() {
        try {
            val response = apiService.getAllMothers()
            if (response.isSuccessful) {
                val entities = response.body()?.data?.map { it.toChildMotherEntity().copy(syncStatus = SyncStatus.DONE) }
                if (!entities.isNullOrEmpty()) {
                    // This transaction ensures the operation is atomic:
                    // it first clears all old data, then inserts all new data.
                    childMotherDao.clearAndInsertAll(entities)
                }
            }
        } catch (e: Exception) {
            // Re-throw the exception so the SyncRepository knows this step failed
            throw e
        }
    }
}
