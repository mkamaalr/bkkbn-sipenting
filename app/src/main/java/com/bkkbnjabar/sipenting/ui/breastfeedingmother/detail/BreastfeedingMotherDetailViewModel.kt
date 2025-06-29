package com.bkkbnjabar.sipenting.ui.breastfeedingmother.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreastfeedingMotherDetailViewModel @Inject constructor(
    private val repository: BreastfeedingMotherRepository
) : ViewModel() {

    private val _motherDetails = MutableLiveData<BreastfeedingMotherEntity?>()
    val motherDetails: LiveData<BreastfeedingMotherEntity?> = _motherDetails

    private val _visitHistory = MutableLiveData<List<BreastfeedingMotherVisitsEntity>>()
    val visitHistory: LiveData<List<BreastfeedingMotherVisitsEntity>> = _visitHistory

    val interpretationComplication = MutableLiveData<InterpretationResult>()
    val interpretationKb = MutableLiveData<InterpretationResult>()
    val interpretationWater = MutableLiveData<InterpretationResult>()
    val interpretationBab = MutableLiveData<InterpretationResult>()
    val interpretationSmoke = MutableLiveData<InterpretationResult>()

    /**
     * Sets the mother's ID and fetches their details and visit history from the repository.
     */
    fun setMotherId(motherId: Int) {
        // --- LOG 1 ---
        Log.d("DetailDebug", "ViewModel: setMotherId called with ID: $motherId")

        viewModelScope.launch {
            repository.getMotherById(motherId).collect { motherEntity ->
                // --- LOG 2 ---
                Log.d("DetailDebug", "ViewModel: Fetched mother entity from repository: ${motherEntity?.name}")
                _motherDetails.postValue(motherEntity)
            }
        }
        viewModelScope.launch {
            repository.getVisitsForMother(motherId).collect { visits ->
                _visitHistory.postValue(visits)
                calculateAllInterpretations()
            }
        }
    }

    fun calculateAllInterpretations() {
        val latestVisit = _visitHistory.value?.maxByOrNull { it.visitDate } ?: return

        interpretationComplication.value = if (latestVisit.isPostpartumComplication == true) {
            InterpretationResult("Beresiko", R.color.risk_red)
        } else {
            InterpretationResult("Tidak Beresiko", R.color.no_risk_green)
        }

        interpretationKb.value = if (latestVisit.onContraception == true) {
            InterpretationResult("Beresiko", R.color.risk_red)
        } else {
            InterpretationResult("Tidak Beresiko", R.color.no_risk_green)
        }

        interpretationWater.value = when (latestVisit.mainSourceOfDrinkingWater?.firstOrNull()) {
            "Leding Meteran", "Air Isi Ulang", "Air Kemasan" -> InterpretationResult("Ideal", R.color.no_risk_green)
            else -> InterpretationResult("Beresiko", R.color.risk_red)
        }

        interpretationBab.value = when (latestVisit.defecationFacility?.firstOrNull()) {
            "Jamban Sendiri" -> InterpretationResult("Ideal", R.color.no_risk_green)
            else -> InterpretationResult("Beresiko", R.color.risk_red)
        }

        interpretationSmoke.value = if (latestVisit.isExposedToCigarettes == true) {
            InterpretationResult("Beresiko", R.color.risk_red)
        } else {
            InterpretationResult("Tidak Beresiko", R.color.no_risk_green)
        }
    }
}