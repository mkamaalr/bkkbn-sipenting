package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.mapper.toPregnantMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow // PENTING: Import flow builder
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class CreatePregnantMotherUseCaseImpl @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository
) : CreatePregnantMotherUseCase {
    override suspend fun execute(data: PregnantMotherRegistrationData): Resource<Long> { // FIXED: Mengembalikan Long ID
        val entity = data.toPregnantMotherEntity()
        return pregnantMotherRepository.createPregnantMother(entity)
    }
}
