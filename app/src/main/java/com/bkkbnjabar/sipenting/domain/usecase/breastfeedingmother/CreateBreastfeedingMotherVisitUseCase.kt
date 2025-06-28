package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.utils.Resource

/**
 * Interface for the use case that creates a new pregnant mother visit record.
 */
interface CreateBreastfeedingMotherVisitUseCase {
    /**
     * Executes the use case to save a new visit's data.
     * @param data The visit data from the form.
     * @return A Resource indicating the result of the operation.
     */
    suspend fun execute(data: BreastfeedingMotherVisitData): Resource<Unit>
}
