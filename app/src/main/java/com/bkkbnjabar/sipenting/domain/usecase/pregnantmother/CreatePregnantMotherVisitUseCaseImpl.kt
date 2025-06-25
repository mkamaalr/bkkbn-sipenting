package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

/**
 * Implementation of the [CreatePregnantMotherVisitUseCase].
 */
class CreatePregnantMotherVisitUseCaseImpl @Inject constructor(
    private val repository: PregnantMotherRepository
) : CreatePregnantMotherVisitUseCase {

    override suspend fun execute(data: PregnantMotherVisitData): Resource<Unit> {
        // Validate that the visit is linked to a mother.
        if (data.pregnantMotherLocalId == null || data.pregnantMotherLocalId == 0) {
            return Resource.Error("Data kunjungan tidak terhubung dengan data ibu.")
        }

        // Convert the visit form data to a database entity and insert it.
        val visitEntity = data.toEntity()
        return repository.insertPregnantMotherVisit(visitEntity)
    }
}
