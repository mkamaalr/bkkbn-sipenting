package com.bkkbnjabar.sipenting.ui.child

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * ViewModel untuk ChildListFragment.
 */
@HiltViewModel
class ChildListViewModel @Inject constructor(
    private val repository: ChildRepository,
    private val lookupRepository: LookupRepository
) : ViewModel() {

    /**
     * Menyediakan daftar semua ibu hamil dari database lokal sebagai LiveData.
     */
    val allChilds: LiveData<List<ChildEntity>> = repository.getAllChilds().asLiveData()

    // Define a new data class for the final UI state
    data class ChildUI(
        val localId: Int,
        val name: String,
        val nik: String,
        val syncStatus: com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus,
        val statusName: String, // The final status name to display
        val details: String // A combined details string
    )

    // Combine the flow of mothers with the flow of lookup items to create the final UI list
    val allChildsForUI = repository.getAllChildsWithLatestStatus()
        .combine(lookupRepository.getLookupOptions("pregnant-mother-statuses")) { mothersWithStatus, statuses ->
            mothersWithStatus.map { motherData ->
                val statusName = statuses.find { it.id == motherData.pregnantMotherStatusId }?.name ?: "Belum Ada Kunjungan"
                val details = "Kunjungan Berikutnya: ${motherData.nextVisitDate ?: '-'}"

                ChildUI(
                    localId = motherData.child.localId,
                    name = motherData.child.name,
                    nik = motherData.child.nik,
                    syncStatus = motherData.child.syncStatus,
                    statusName = statusName,
                    details = details
                )
            }
        }.asLiveData()
}
