package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.utils.Resource

interface CreatePregnantMotherVisitUseCase {
    suspend fun execute(visitData: PregnantMotherVisitData): Resource<Unit>
}
