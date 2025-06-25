package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PregnantMotherRepository {
    suspend fun insertPregnantMother(motherEntity: PregnantMotherEntity): Resource<Long>
    suspend fun insertPregnantMotherVisit(visitEntity: PregnantMotherVisitsEntity): Resource<Unit>
    fun getAllPregnantMothers(): Flow<List<PregnantMotherEntity>>
}
