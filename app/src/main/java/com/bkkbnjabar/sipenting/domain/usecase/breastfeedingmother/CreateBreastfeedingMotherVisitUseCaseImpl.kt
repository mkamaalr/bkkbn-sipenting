package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

/**
 * Implementation of the [CreateBreastfeedingMotherVisitUseCase].
 */
class CreateBreastfeedingMotherVisitUseCaseImpl @Inject constructor(
    private val repository: BreastfeedingMotherRepository
) : CreateBreastfeedingMotherVisitUseCase {

    override suspend fun execute(data: BreastfeedingMotherVisitData): Resource<Unit> {
        // Validate that the visit is linked to a mother.
        if (data.breastfeedingMotherId == null || data.breastfeedingMotherId == 0) {
            return Resource.Error("Data kunjungan tidak terhubung dengan data ibu.")
        }

        // Convert the visit form data to a database entity and insert it.
        val visitEntity = data.toEntity()
        return repository.insertBreastfeedingMotherVisit(visitEntity)
    }
}
