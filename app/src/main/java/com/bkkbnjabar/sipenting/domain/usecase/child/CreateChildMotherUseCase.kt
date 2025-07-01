package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.model.child.ChildMotherData
import com.bkkbnjabar.sipenting.utils.Resource

/**
 * Use case for creating a new child's mother record in the local database.
 */
interface CreateChildMotherUseCase {
    /**
     * Executes the use case.
     * @param motherData The mother's data to be saved.
     * @return A Resource containing the new localId of the mother, or an error.
     */
    suspend fun execute(motherData: ChildMotherData): Resource<Long?>
}