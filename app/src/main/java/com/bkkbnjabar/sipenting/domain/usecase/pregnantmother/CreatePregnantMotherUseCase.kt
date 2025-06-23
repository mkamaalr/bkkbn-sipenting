package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak untuk use case membuat data ibu hamil baru.
 * Bertanggung jawab untuk mengkoordinasikan operasi repository dan mengembalikan hasil yang relevan untuk UI.
 */
interface CreatePregnantMotherUseCase {
    suspend fun execute(data: PregnantMotherRegistrationData): Resource<Long> // FIXED: Mengembalikan Long ID
}