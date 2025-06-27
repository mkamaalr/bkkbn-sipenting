package com.bkkbnjabar.sipenting.ui.pregnantmother.detail

import androidx.lifecycle.*
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PregnantMotherDetailViewModel @Inject constructor(
    private val repository: PregnantMotherRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Mengambil motherId dari argumen navigasi dengan aman
    private val motherId = savedStateHandle.getLiveData<Int>("motherId")

    // LiveData untuk detail ibu, akan otomatis update jika motherId berubah
    val motherDetails: LiveData<PregnantMotherEntity?> = motherId.switchMap { id ->
        repository.getMotherById(id).asLiveData()
    }

    // LiveData untuk riwayat kunjungan, juga otomatis update
    val visitHistory: LiveData<List<PregnantMotherVisitsEntity>> = motherId.switchMap { id ->
        repository.getVisitsForMother(id).asLiveData()
    }

    // LiveData untuk interpretasi data (placeholder untuk rumus Anda nanti)
    private val _interpretationText = MutableLiveData<String>()
    val interpretationText: LiveData<String> = _interpretationText

    fun calculateInterpretation() {
        val mother = motherDetails.value
        val firstVisit = visitHistory.value?.minByOrNull { it.createdAt }

        if (mother == null || firstVisit == null) {
            _interpretationText.value = "Data tidak lengkap untuk interpretasi."
            return
        }

        // TODO: Masukkan rumus Anda di sini.
        // Contoh sederhana:
        if ((firstVisit.upperArmCircumference ?: 0.0) < 23.5) {
            _interpretationText.value = "Status Gizi: Risiko KEK (Kurang Energi Kronis)"
        } else {
            _interpretationText.value = "Status Gizi: Normal"
        }
    }
}
