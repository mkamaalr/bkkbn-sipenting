package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class CreateBreastfeedingMotherUseCaseImpl @Inject constructor(
    private val repository: BreastfeedingMotherRepository
) : CreateBreastfeedingMotherUseCase {
    override suspend fun execute(data: BreastfeedingMotherRegistrationData): Resource<Long> {
        // Business logic validation can be added here.
        // For example, ensuring NIK is not empty and is 16 digits long.
        if (data.name.isNullOrBlank() || data.nik.isNullOrBlank() || data.nik.length != 16) {
            return Resource.Error("Data tidak valid. Nama dan NIK (16 digit) wajib diisi.")
        }

        // Convert the form data to a database entity and insert it.
        val motherEntity = data.toEntity()
        return repository.insertBreastfeedingMother(motherEntity)
    }
}