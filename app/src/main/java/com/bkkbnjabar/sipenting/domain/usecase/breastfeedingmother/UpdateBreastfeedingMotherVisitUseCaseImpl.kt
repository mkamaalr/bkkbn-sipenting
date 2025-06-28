package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class UpdateBreastfeedingMotherVisitUseCaseImpl @Inject constructor(
    private val repository: BreastfeedingMotherRepository
) : UpdateBreastfeedingMotherVisitUseCase {
    override suspend fun execute(data: BreastfeedingMotherVisitData): Resource<Unit> {
        if (data.localVisitId == null || data.localVisitId == 0) {
            return Resource.Error("ID Kunjungan tidak valid untuk diperbarui.")
        }
        val visitEntity = data.toEntity()
        return repository.updateBreastfeedingMotherVisit(visitEntity)
    }
}