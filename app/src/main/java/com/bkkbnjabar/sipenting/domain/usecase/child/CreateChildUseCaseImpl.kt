package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildData
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class CreateChildUseCaseImpl @Inject constructor(
    private val repository: ChildRepository
) : CreateChildUseCase {

    override suspend fun execute(childData: ChildData): Resource<Long?> {
        // Step 1: Validate the input data.
        if (childData.name.isBlank() || childData.nik.isBlank()) {
            return Resource.Error("Nama dan NIK Anak tidak boleh kosong.")
        }
        if (childData.nik.length != 16) {
            return Resource.Error("NIK Anak harus 16 digit.")
        }
        if (childData.motherId == 0) {
            return Resource.Error("Data Ibu tidak terhubung, gagal menyimpan anak.")
        }

        // Step 2: Convert the data model to a database entity.
        val childEntity = childData.toEntity()

        // Step 3: Insert the entity into the database via the repository.
        return repository.insertChild(childEntity)
    }
}
