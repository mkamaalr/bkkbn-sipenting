package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherWithLatestStatus
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PregnantMotherRepository {
    suspend fun insertPregnantMother(motherEntity: PregnantMotherEntity): Resource<Long>
    suspend fun insertPregnantMotherVisit(visitEntity: PregnantMotherVisitsEntity): Resource<Unit>
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>
    fun getMotherById(localId: Int): Flow<PregnantMotherEntity?>
    fun getVisitsForMother(motherId: Int): Flow<List<PregnantMotherVisitsEntity>>
    suspend fun updatePregnantMotherVisit(visitEntity: PregnantMotherVisitsEntity): Resource<Unit>
    fun getVisitById(visitId: Int): Flow<PregnantMotherVisitsEntity?>
    fun getAllMothersWithLatestStatus(): Flow<List<PregnantMotherWithLatestStatus>>
    suspend fun uploadPendingData()
    suspend fun syncFromServer()
}
