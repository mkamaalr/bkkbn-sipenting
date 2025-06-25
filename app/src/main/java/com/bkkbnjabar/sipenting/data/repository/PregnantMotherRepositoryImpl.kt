package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PregnantMotherRepositoryImpl @Inject constructor(
    private val pregnantMotherDao: PregnantMotherDao,
    private val visitsDao: PregnantMotherVisitsDao
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
}
