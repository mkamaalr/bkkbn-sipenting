package com.bkkbnjabar.sipenting.ui.child.detail

import androidx.lifecycle.*
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChildDetailViewModel @Inject constructor(
    private val repository: ChildRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val childId = savedStateHandle.getLiveData<Int>("childId")

     val childDetails: LiveData<ChildEntity?> = childId.switchMap { id ->
        repository.getChildById(id).asLiveData()
    }

    private val _visitHistory: LiveData<List<ChildVisitsEntity>> = childId.switchMap { id ->
        repository.getVisitsForChild(id).asLiveData()
    }

    // --- LiveData for each interpretation result ---
    val interpretationAge = MutableLiveData<InterpretationResult>()
    val interpretationWeight = MutableLiveData<InterpretationResult>()
    val interpretationHeight = MutableLiveData<InterpretationResult>()
    val interpretationHeadCircumference = MutableLiveData<InterpretationResult>()
    val interpretationAsi = MutableLiveData<InterpretationResult>()
    val interpretationMpasi = MutableLiveData<InterpretationResult>()
    val interpretationPosyandu = MutableLiveData<InterpretationResult>()

    // --- MediatorLiveData to combine sources and trigger calculations ---
    val detailDataMediator = MediatorLiveData<Pair<ChildEntity?, List<ChildVisitsEntity>>>().apply {
        addSource(childDetails) { child ->
            value = Pair(child, _visitHistory.value)
        }
        addSource(_visitHistory) { visits ->
            value = Pair(childDetails.value, visits)
        }
    }

    /**
     * Calculates all interpretations. This is called from the Fragment when the mediator observes a change.
     */
    fun calculateAllInterpretations() {
        val child = detailDataMediator.value?.first
        val latestVisit = detailDataMediator.value?.second?.maxByOrNull { it.createdAt ?: 0L }

        if (child == null) return

        val ageInMonths = calculateAgeInMonths(child.dateOfBirth)
        interpretationAge.value = interpretChildAge(child.dateOfBirth)

        if (latestVisit == null) {
            val noData = InterpretationResult("Data Kunjungan Kosong", R.color.neutral_gray)
            interpretationWeight.value = noData
            interpretationHeight.value = noData
            interpretationHeadCircumference.value = noData
            interpretationAsi.value = noData
            interpretationMpasi.value = noData
            interpretationPosyandu.value = noData
            return
        }

        interpretationWeight.value = interpretWeight(latestVisit.weightMeasurement, ageInMonths)
        interpretationHeight.value = interpretHeight(latestVisit.heightMeasurement, ageInMonths)
        interpretationHeadCircumference.value = interpretHeadCircumference(latestVisit.headCircumference, ageInMonths)
        interpretationAsi.value = interpretAsiStatus(latestVisit.isAsiExclusive)
        interpretationMpasi.value = interpretMpasiStatus(latestVisit.isMpasi)
        interpretationPosyandu.value = interpretPosyanduStatus(latestVisit.isPosyanduMonth)
    }

    private fun calculateAgeInMonths(dobString: String?): Int {
        if (dobString.isNullOrBlank()) return 0
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dobString, formatter)
            Period.between(birthDate, LocalDate.now()).toTotalMonths().toInt()
        } catch (e: Exception) { 0 }
    }

    private fun interpretChildAge(dobString: String?): InterpretationResult {
        if (dobString.isNullOrBlank()) return InterpretationResult("Format Tgl Lahir Salah", R.color.risk_red)
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dobString, formatter)
            val period = Period.between(birthDate, LocalDate.now())
            val years = period.years
            val months = period.months
            val text = if (years > 0) "$years Tahun, $months Bulan" else "$months Bulan"
            InterpretationResult(text, R.color.no_risk_green)
        } catch (e: Exception) {
            InterpretationResult("Format Tgl Lahir Salah", R.color.risk_red)
        }
    }

    private fun interpretWeight(weight: Double?, ageInMonths: Int): InterpretationResult {
        if (weight == null) return InterpretationResult("-", R.color.neutral_gray)
        // This is example logic. A real implementation would use a WHO growth chart table.
        val idealLowerBound = ageInMonths * 0.5 + 4
        val idealUpperBound = ageInMonths * 0.5 + 6
        return when {
            weight < idealLowerBound -> InterpretationResult("$weight kg (Kurang)", R.color.risk_red)
            weight > idealUpperBound -> InterpretationResult("$weight kg (Lebih)", R.color.status_yellow_text)
            else -> InterpretationResult("$weight kg (Ideal)", R.color.no_risk_green)
        }
    }

    private fun interpretHeight(height: Double?, ageInMonths: Int): InterpretationResult {
        if (height == null) return InterpretationResult("-", R.color.neutral_gray)
        // This is example logic.
        val idealLowerBound = ageInMonths * 0.6 + 60
        return when {
            height < idealLowerBound -> InterpretationResult("$height cm (Pendek)", R.color.risk_red)
            else -> InterpretationResult("$height cm (Normal)", R.color.no_risk_green)
        }
    }

    private fun interpretHeadCircumference(circumference: Double?, ageInMonths: Int): InterpretationResult {
        if (circumference == null) return InterpretationResult("-", R.color.neutral_gray)
        return InterpretationResult("$circumference cm", R.color.no_risk_green)
    }

    private fun interpretAsiStatus(isExclusive: Boolean?): InterpretationResult {
        if (isExclusive == null) return InterpretationResult("-", R.color.neutral_gray)
        return if (isExclusive) InterpretationResult("Ya (Ideal)", R.color.no_risk_green)
        else InterpretationResult("Tidak (Beresiko)", R.color.risk_red)
    }

    private fun interpretMpasiStatus(isMpasi: Boolean?): InterpretationResult {
        if (isMpasi == null) return InterpretationResult("-", R.color.neutral_gray)
        return InterpretationResult(if (isMpasi) "Ya" else "Tidak", R.color.no_risk_green)
    }

    private fun interpretPosyanduStatus(isPosyandu: Boolean?): InterpretationResult {
        if (isPosyandu == null) return InterpretationResult("-", R.color.neutral_gray)
        return InterpretationResult(if (isPosyandu) "Ya" else "Tidak", R.color.no_risk_green)
    }
}
