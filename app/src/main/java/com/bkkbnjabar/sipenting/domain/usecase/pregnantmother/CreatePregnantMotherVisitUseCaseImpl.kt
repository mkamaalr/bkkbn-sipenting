package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.mapper.toPregnantMotherVisitEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class CreatePregnantMotherVisitUseCaseImpl @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository
) : CreatePregnantMotherVisitUseCase {
    override suspend fun execute(visitData: PregnantMotherVisitData): Resource<Unit> {
        val entity = visitData.toPregnantMotherVisitEntity()
        return pregnantMotherRepository.addPregnantMotherVisit(entity)
    }
}