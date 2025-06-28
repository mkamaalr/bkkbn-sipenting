package com.bkkbnjabar.sipenting.domain.usecase.child

import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.utils.Resource

interface UpdateChildVisitUseCase {
    suspend fun execute(data: ChildVisitData): Resource<Unit>
}