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

    override suspend fun execute(visitData: ChildVisitData): Resource<Unit> {
        if (visitData.visitDate.isNullOrBlank()) {
            return Resource.Error("Tanggal kunjungan tidak boleh kosong.")
        }
        if (visitData.childId == 0) {
            return Resource.Error("Data anak tidak terhubung, gagal menyimpan kunjungan.")
        }

        return try {
            repository.insertVisit(visitData.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data kunjungan: ${e.message}")
        }
    }
}
