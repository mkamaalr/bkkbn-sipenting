package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toPregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PregnantMotherRepositoryImpl @Inject constructor(
    private val pregnantMotherDao: PregnantMotherDao,
    private val pregnantMotherVisitsDao: PregnantMotherVisitsDao
) : PregnantMotherRepository {

    override suspend fun createPregnantMother(mother: PregnantMotherEntity): Resource<Long> { // FIXED: Mengembalikan Long ID
        return try {
            val id = pregnantMotherDao.insertPregnantMother(mother)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error("Failed to create pregnant mother: ${e.localizedMessage}")
        }
    }

    override fun getPregnantMothers(): Flow<Resource<List<PregnantMotherRegistrationData>>> = flow {
        emit(Resource.Loading())

        try {
            pregnantMotherDao.getAllPregnantMothers()
                .map { entities ->
                    entities.map { it.toPregnantMotherRegistrationData() }
                }
                .collect { mappedDataList ->
                    emit(Resource.Success(mappedDataList))
                }
        } catch (e: Exception) {
            emit(Resource.Error("Error loading pregnant mothers: ${e.localizedMessage}"))
        }
    }.catch { e ->
        emit(Resource.Error("Unexpected error in pregnant mothers flow: ${e.localizedMessage}"))
    }

    override suspend fun addPregnantMotherVisit(visit: PregnantMotherVisitsEntity): Resource<Unit> {
        return try {
            pregnantMotherVisitsDao.insertPregnantMotherVisit(visit)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to add pregnant mother visit: ${e.localizedMessage}")
        }
    }

    override fun getPregnantMotherVisits(motherId: Int): Flow<List<PregnantMotherVisitsEntity>> {
        return pregnantMotherVisitsDao.getVisitsForPregnantMother(motherId)
    }
}
