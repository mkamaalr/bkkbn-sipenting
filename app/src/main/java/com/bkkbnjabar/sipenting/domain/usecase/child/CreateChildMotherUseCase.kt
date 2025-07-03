package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildMotherData
import com.bkkbnjabar.sipenting.data.repository.ChildMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

interface CreateChildMotherUseCase {
    suspend fun execute(motherData: ChildMotherData): Resource<Long?>
}

class CreateChildMotherUseCaseImpl @Inject constructor(
    private val repository: ChildMotherRepository
) : CreateChildMotherUseCase {

    override suspend fun execute(motherData: ChildMotherData): Resource<Long?> {
        if (motherData.name.isBlank() || motherData.nik.isBlank()) {
            return Resource.Error("Nama dan NIK Ibu tidak boleh kosong.")
        }
        if (motherData.nik.length != 16) {
            return Resource.Error("NIK Ibu harus 16 digit.")
        }

        return try {
            val newRowId = repository.insertMother(motherData.toEntity())
            if (newRowId > 0) {
                Resource.Success(newRowId)
            } else {
                Resource.Error("Gagal menyimpan data ibu ke database.")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal menyimpan data ibu ke database: ${e.message}")
        }
    }
}
