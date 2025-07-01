package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildMotherData
import com.bkkbnjabar.sipenting.data.repository.ChildMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class CreateChildMotherUseCaseImpl @Inject constructor(
    private val repository: ChildMotherRepository
) : CreateChildMotherUseCase {

    override suspend fun execute(motherData: ChildMotherData): Resource<Long?> {
        // Step 1: Validate the input data.
        if (motherData.name.isBlank() || motherData.nik.isBlank()) {
            return Resource.Error("Nama dan NIK Ibu tidak boleh kosong.")
        }
        if (motherData.nik.length != 16) {
            return Resource.Error("NIK Ibu harus 16 digit.")
        }

        // Step 2: Convert the data model to a database entity.
        val motherEntity = motherData.toEntity()

        // Step 3: Insert the entity into the database via the repository.
        return try {
            val newRowId = repository.insertMother(motherEntity)
            if (newRowId > 0) {
                Resource.Success(newRowId)
            } else {
                Resource.Error("Gagal menyimpan data ibu ke database.")
            }
        } catch (e: Exception) {
            // Return an error resource with the exception message.
            Resource.Error("Gagal menyimpan data ibu ke database: ${e.message}")
        }
    }
}