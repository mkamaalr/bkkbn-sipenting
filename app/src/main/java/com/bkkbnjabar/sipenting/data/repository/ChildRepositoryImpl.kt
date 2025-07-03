package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildWithLatestStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.remote.ChildApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toChildEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toChildVisitEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toEntity
import com.bkkbnjabar.sipenting.data.remote.mapper.toUploadRequest
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepositoryImpl @Inject constructor(
    private val childDao: ChildDao,
    private val childMotherDao: ChildMotherDao,
    private val visitsDao: ChildVisitsDao,
    private val apiService: ChildApiService
) : ChildRepository {

    override suspend fun insertChild(child: ChildEntity): Resource<Long?> {
        return try {
            val newRowId = childDao.insertChild(child)
            if (newRowId > 0) {
                Resource.Success(newRowId)
            } else {
                Resource.Error("Gagal menyimpan data anak ke database.")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data anak: ${e.message}")
        }
    }


    override suspend fun insertVisit(visit: ChildVisitsEntity): Resource<Unit> {
        visitsDao.insertVisit(visit)
        return Resource.Success(Unit)
    }
    override suspend fun updateVisit(visit: ChildVisitsEntity): Resource<Unit> {
        visitsDao.updateVisit(visit.copy(syncStatus = SyncStatus.PENDING))
        return Resource.Success(Unit)
    }

    override suspend fun insertChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit> {
        visitsDao.insertVisit(visitEntity)
        return Resource.Success(Unit)
    }

    override fun getAllChilds(): Flow<List<ChildEntity>> {
        return childDao.getAllChildren()
    }


    override fun getChildById(localId: Int): Flow<ChildEntity?> = childDao.getChildById(localId)
    override fun getVisitsForChild(childId: Int): Flow<List<ChildVisitsEntity>> = visitsDao.getVisitsForChild(childId)
    override suspend fun updateChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit> {
        visitsDao.updateVisit(visitEntity.copy(syncStatus = SyncStatus.PENDING))
        return Resource.Success(Unit)
    }

    override fun getVisitById(localVisitId: Int): Flow<ChildVisitsEntity?> = visitsDao.getVisitById(localVisitId)
    override fun getAllChildsWithLatestStatus(): Flow<List<ChildWithLatestStatus>> {
        return childDao.getAllChildsWithLatestStatus()
    }

    override suspend fun uploadPendingData() {
        val pendingChildren = childDao.getPendingChildren()
        Timber.d("Uploading ${pendingChildren.size} pending children...")
        for (child in pendingChildren) {
            try {
                val mother = childMotherDao.getMotherById(child.motherId).first()
                if (mother?.id == null) {
                    Timber.w("Skipping child sync for localId ${child.localId}, mother not synced yet.")
                    continue
                }

                val request = child.toUploadRequest(mother.id)
                val response = if (child.id == null) apiService.createChild(request)
                else apiService.updateChild(child.id, request)

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    childDao.updateChild(child.copy(id = serverId, syncStatus = SyncStatus.DONE))
                }
            } catch (e: Exception) { Timber.e(e, "Failed to sync child with localId: ${child.localId}") }
        }

        val pendingVisits = visitsDao.getPendingVisits()
        Timber.d("Uploading ${pendingVisits.size} pending visits...")
        for (visit in pendingVisits) {
            try {
                val child = childDao.getChildById(visit.childId).first()
                if (child?.id == null) {
                    Timber.w("Skipping visit sync for localId ${visit.localVisitId}, child not synced yet.")
                    continue
                }

                val request = visit.toUploadRequest(child.id)
                val response = if (visit.id == null) apiService.createChildVisit(request)
                else apiService.updateChildVisit(visit.id, request)

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverId = response.body()!!.data!!.id
                    visitsDao.updateVisit(visit.copy(id = serverId, syncStatus = SyncStatus.DONE))
                }
            } catch(e: Exception) { Timber.e(e, "Failed to sync visit with localId: ${visit.localVisitId}") }
        }
    }

    override suspend fun syncFromServer() {
        try {
            // Sync Children
            val childResponse = apiService.getAllChildren()
            if (childResponse.isSuccessful) {
                val childEntities = childResponse.body()?.data?.mapNotNull { childDto ->
                    val mother = childMotherDao.findByServerId(childDto.motherId ?: "")
                    if (mother != null) {
                        // This line will now work correctly
                        childDto.toChildEntity().copy(
                            motherId = mother.localId,
                            syncStatus = SyncStatus.DONE
                        )
                    } else { null }
                }
                if (!childEntities.isNullOrEmpty()) {
                    childDao.clearAndInsertAll(childEntities)
                }
            }
            // Sync Visits
            val visitResponse = apiService.getAllChildVisits()
            if (visitResponse.isSuccessful) {
                val visitEntities = visitResponse.body()?.data?.mapNotNull { visitDto ->
                    val child = childDao.findByServerId(visitDto.parentId)
                    if (child != null) {
                        visitDto.toChildVisitEntity().copy(
                            childId = child.localId,
                            syncStatus = SyncStatus.DONE
                        )
                    } else { null }
                }
                if (!visitEntities.isNullOrEmpty()) {
                    visitsDao.clearAndInsertAll(visitEntities)
                }
            }
        } catch (e: Exception) { throw e }
    }
}
