package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PregnantMotherRepository {
    // Mengembalikan Long sebagai ID yang baru dibuat
    suspend fun createPregnantMother(mother: PregnantMotherEntity): Resource<Long> // FIXED: Mengembalikan Long ID
    fun getPregnantMothers(): Flow<Resource<List<PregnantMotherRegistrationData>>>
    suspend fun addPregnantMotherVisit(visit: PregnantMotherVisitsEntity): Resource<Unit>
    fun getPregnantMotherVisits(motherId: Int): Flow<List<PregnantMotherVisitsEntity>>
}
