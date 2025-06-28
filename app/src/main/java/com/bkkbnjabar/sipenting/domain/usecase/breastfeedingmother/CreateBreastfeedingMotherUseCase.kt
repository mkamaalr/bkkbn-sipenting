package com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother

import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource

interface CreateBreastfeedingMotherUseCase {
    /**
     * Executes the use case to save a new pregnant mother's data.
     * @param data The registration data from the form.
     * @return A Resource containing the new row ID (Long) from the database if successful.
     */
    suspend fun execute(data: BreastfeedingMotherRegistrationData): Resource<Long>
}