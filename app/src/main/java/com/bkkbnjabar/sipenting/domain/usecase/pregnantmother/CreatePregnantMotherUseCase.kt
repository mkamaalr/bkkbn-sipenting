package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import javax.inject.Inject

class CreatePregnantMotherUseCase @Inject constructor( // Pastikan @Inject di sini
    private val pregnantMotherRepository: PregnantMotherRepository
) {
    suspend fun execute(data: PregnantMotherRegistrationData): Resource<String> {
        // Mengasumsikan repository memiliki metode untuk membuat ibu hamil
        return pregnantMotherRepository.createPregnantMother(data)
    }
}
