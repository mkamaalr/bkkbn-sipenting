package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class UpdatePregnantMotherVisitUseCaseImpl @Inject constructor(
    private val repository: PregnantMotherRepository
) : UpdatePregnantMotherVisitUseCase {
    override suspend fun execute(data: PregnantMotherVisitData): Resource<Unit> {
        if (data.localVisitId == null || data.localVisitId == 0) {
            return Resource.Error("ID Kunjungan tidak valid untuk diperbarui.")
        }
        val visitEntity = data.toEntity()
        return repository.updatePregnantMotherVisit(visitEntity)
    }
}