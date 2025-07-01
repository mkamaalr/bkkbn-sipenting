package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherWithLatestStatus
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BreastfeedingMotherRepository {
    suspend fun insertBreastfeedingMother(motherEntity: BreastfeedingMotherEntity): Resource<Long>
    suspend fun insertBreastfeedingMotherVisit(visitEntity: BreastfeedingMotherVisitsEntity): Resource<Unit>
    fun getAllBreastfeedingMothers(): Flow<List<BreastfeedingMotherEntity>>
    fun getMotherById(localId: Int): Flow<BreastfeedingMotherEntity?>
    fun getVisitsForMother(motherId: Int): Flow<List<BreastfeedingMotherVisitsEntity>>
    suspend fun updateBreastfeedingMotherVisit(visitEntity: BreastfeedingMotherVisitsEntity): Resource<Unit>
    fun getVisitById(visitId: Int): Flow<BreastfeedingMotherVisitsEntity?>
    fun getAllMothersWithLatestStatus(): Flow<List<BreastfeedingMotherWithLatestStatus>>
    suspend fun uploadPendingData()
    suspend fun syncFromServer()
}