package com.bkkbnjabar.sipenting.domain.usecase.pregnantmother

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource

/**
 * Interface for the use case that creates a new pregnant mother record.
 */
interface CreatePregnantMotherUseCase {
    /**
     * Executes the use case to save a new pregnant mother's data.
     * @param data The registration data from the form.
     * @return A Resource containing the new row ID (Long) from the database if successful.
     */
    suspend fun execute(data: PregnantMotherRegistrationData): Resource<Long>
}