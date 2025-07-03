package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.model.child.ChildData
import com.bkkbnjabar.sipenting.data.model.child.ChildRegistrationData
import com.bkkbnjabar.sipenting.utils.Resource

/**
 * Use case for creating a new child record in the local database.
 */
interface CreateChildUseCase {
    /**
     * Executes the use case.
     * @param childData The child's data to be saved.
     * @return A Resource containing the new localId of the child, or an error.
     */
    suspend fun execute(childData: ChildData): Resource<Long?>

}