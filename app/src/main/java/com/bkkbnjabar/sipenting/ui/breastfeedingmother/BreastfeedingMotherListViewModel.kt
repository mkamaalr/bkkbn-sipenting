package com.bkkbnjabar.sipenting.ui.breastfeedingmother

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BreastfeedingMotherListViewModel @Inject constructor(
    private val repository: BreastfeedingMotherRepository,
    private val lookupRepository: LookupRepository
) : ViewModel() {

    // Data class for the final UI state
    data class BreastfeedingMotherUI(
        val localId: Int,
        val name: String,
        val nik: String,
        val details: String
    )

    val allBreastfeedingMothersForUI = repository.getAllMothersWithLatestStatus()
        .map { mothers ->
            mothers.map { motherData ->
                val asiStatus = if (motherData.isAsiExclusive == true) "Eksklusif" else "Tidak Eksklusif"
                val details = "Kunjungan Terakhir: ${motherData.latestVisitDate ?: '-'}, Status ASI: $asiStatus"

                BreastfeedingMotherUI(
                    localId = motherData.mother.localId,
                    name = motherData.mother.name,
                    nik = motherData.mother.nik,
                    details = details
                )
            }
        }.asLiveData()
}