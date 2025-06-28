package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.utils.Resource

interface UpdateBreastfeedingMotherVisitUseCase {
    suspend fun execute(data: BreastfeedingMotherVisitData): Resource<Unit>
}