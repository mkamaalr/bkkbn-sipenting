package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

/**
 * Implementation of the [CreatePregnantMotherUseCase].
 */
class CreatePregnantMotherUseCaseImpl @Inject constructor(
    private val repository: PregnantMotherRepository
) : CreatePregnantMotherUseCase {

    override suspend fun execute(data: PregnantMotherRegistrationData): Resource<Long> {
        // Business logic validation can be added here.
        // For example, ensuring NIK is not empty and is 16 digits long.
        if (data.name.isNullOrBlank() || data.nik.isNullOrBlank() || data.nik.length != 16) {
            return Resource.Error("Data tidak valid. Nama dan NIK (16 digit) wajib diisi.")
        }

        // Convert the form data to a database entity and insert it.
        val motherEntity = data.toEntity()
        return repository.insertPregnantMother(motherEntity)
    }
}
