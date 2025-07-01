package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.ChildMotherDao
import com.bkkbnjabar.sipenting.data.local.entity.ChildMotherEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildMotherRepositoryImpl @Inject constructor(
    private val childMotherDao: ChildMotherDao
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
        // TODO: Implement logic to get pending mothers and visits from DAO and upload to API
    }

    override suspend fun syncFromServer() {
        // TODO: Implement logic to fetch data from API and insert/update local database
    }
}
