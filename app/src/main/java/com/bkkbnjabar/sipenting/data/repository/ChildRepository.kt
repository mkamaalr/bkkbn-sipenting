package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildWithLatestStatus
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChildRepository {
    suspend fun insertChild(childEntity: ChildEntity): Resource<Long>
    suspend fun insertChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit>
    fun getAllChilds(): Flow<List<ChildEntity>>
    fun getChildById(localId: Int): Flow<ChildEntity?>
    fun getVisitsForChild(childId: Int): Flow<List<ChildVisitsEntity>>
    suspend fun updateChildVisit(visitEntity: ChildVisitsEntity): Resource<Unit>
    fun getVisitById(visitId: Int): Flow<ChildVisitsEntity?>
    fun getAllChildsWithLatestStatus(): Flow<List<ChildWithLatestStatus>>
    suspend fun uploadPendingData()
    suspend fun syncFromServer()
}
