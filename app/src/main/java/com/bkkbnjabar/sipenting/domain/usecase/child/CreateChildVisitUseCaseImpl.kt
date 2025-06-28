package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

/**
 * Implementation of the [CreateChildVisitUseCase].
 */
class CreateChildVisitUseCaseImpl @Inject constructor(
    private val repository: ChildRepository
) : CreateChildVisitUseCase {

    override suspend fun execute(data: ChildVisitData): Resource<Unit> {
        // Validate that the visit is linked to a mother.
        if (data.pregnantMotherLocalId == null || data.pregnantMotherLocalId == 0) {
            return Resource.Error("Data kunjungan tidak terhubung dengan data ibu.")
        }

        // Convert the visit form data to a database entity and insert it.
        val visitEntity = data.toEntity()
        return repository.insertChildVisit(visitEntity)
    }
}
