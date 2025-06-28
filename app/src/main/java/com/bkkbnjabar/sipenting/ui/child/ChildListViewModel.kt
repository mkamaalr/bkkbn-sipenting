package com.bkkbnjabar.sipenting.ui.child

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * ViewModel untuk PregnantMotherListFragment.
 */
@HiltViewModel
class ChildListViewModel @Inject constructor(
    private val repository: PregnantMotherRepository,
    private val lookupRepository: LookupRepository
) : ViewModel() {

    /**
     * Menyediakan daftar semua ibu hamil dari database lokal sebagai LiveData.
     */
    val allPregnantMothers: LiveData<List<PregnantMotherEntity>> = repository.getAllPregnantMothers().asLiveData()

    // Define a new data class for the final UI state
    data class PregnantMotherUI(
        val localId: Int,
        val name: String,
        val nik: String,
        val syncStatus: com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus,
        val statusName: String, // The final status name to display
        val details: String // A combined details string
    )

    // Combine the flow of mothers with the flow of lookup items to create the final UI list
    val allPregnantMothersForUI = repository.getAllMothersWithLatestStatus()
        .combine(lookupRepository.getLookupOptions("pregnant-mother-statuses")) { mothersWithStatus, statuses ->
            mothersWithStatus.map { motherData ->
                val statusName = statuses.find { it.id == motherData.pregnantMotherStatusId }?.name ?: "Belum Ada Kunjungan"
                val details = "Usia Kehamilan: ${motherData.pregnancyWeekAge ?: '-'} mg, Kunjungan Berikutnya: ${motherData.nextVisitDate ?: '-'}"

                PregnantMotherUI(
                    localId = motherData.mother.localId,
                    name = motherData.mother.name,
                    nik = motherData.mother.nik,
                    syncStatus = motherData.mother.syncStatus,
                    statusName = statusName,
                    details = details
                )
            }
        }.asLiveData()
}
