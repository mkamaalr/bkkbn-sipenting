package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherWithLatestStatus
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreastfeedingMotherRepositoryImpl @Inject constructor(
    private val breastfeedingMotherDao: BreastfeedingMotherDao,
    private val visitsDao: BreastfeedingMotherVisitsDao
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
        // TODO: Implement logic to get pending mothers and visits from DAO and upload to API
    }

    override suspend fun syncFromServer() {
        // TODO: Implement logic to fetch data from API and insert/update local database
    }
}