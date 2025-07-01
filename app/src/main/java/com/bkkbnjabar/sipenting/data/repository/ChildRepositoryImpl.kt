package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildWithLatestStatus
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepositoryImpl @Inject constructor(
    private val childDao: ChildDao,
    private val visitsDao: ChildVisitsDao
) : ChildRepository {

    override suspend fun insertChild(motherEntity: ChildEntity): Resource<Long> {
        return try {
            val newRowId = childDao.insertChild(motherEntity)
            Resource.Success(newRowId)
        } catch (e: Exception) {
            Log.e("YourTag", "Gagal menyimpan data ibu hamil ke database: ${e.message}", e)
            Resource.Error("Gagal menyimpan data ibu hamil ke database: ${e.message}")
        }
    }

    override suspend fun insertChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.insertVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data kunjungan ke database: ${e.message}")
        }
    }

    override fun getAllChilds(): Flow<List<ChildEntity>> {
        return childDao.getAllChildren()
    }

    override fun getChildById(localId: Int): Flow<ChildEntity?> {
        return childDao.getChildById(localId)
    }

    override fun getVisitsForChild(child: Int): Flow<List<ChildVisitsEntity>> {
        return visitsDao.getVisitsForChild(child)
    }

    override suspend fun updateChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit> {
        return try {
            visitsDao.updateVisit(visitEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui data kunjungan di database: ${e.message}")
        }
    }

    override fun getVisitById(visitId: Int): Flow<ChildVisitsEntity?> {
        return visitsDao.getVisitById(visitId)
    }

    override fun getAllChildsWithLatestStatus(): Flow<List<ChildWithLatestStatus>> {
        return childDao.getAllChildsWithLatestStatus()
    }

    override suspend fun uploadPendingData() {
        // TODO: Implement logic to get pending mothers and visits from DAO and upload to API
    }

    override suspend fun syncFromServer() {
        // TODO: Implement logic to fetch data from API and insert/update local database
    }
}
