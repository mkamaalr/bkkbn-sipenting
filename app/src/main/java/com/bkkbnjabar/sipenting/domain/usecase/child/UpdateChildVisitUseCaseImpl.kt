package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class UpdateChildVisitUseCaseImpl @Inject constructor(
    private val repository: ChildRepository
) : UpdateChildVisitUseCase {
    override suspend fun execute(data: ChildVisitData): Resource<Unit> {
        if (data.localVisitId == null || data.localVisitId == 0) {
            return Resource.Error("ID Kunjungan tidak valid untuk diperbarui.")
        }
        val visitEntity = data.toEntity()
        return repository.updateVisit(visitEntity)
    }
}