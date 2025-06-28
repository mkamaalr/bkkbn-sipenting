package com.bkkbnjabar.sipenting.ui.pregnantmother.detail

import androidx.lifecycle.*
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class PregnantMotherDetailViewModel @Inject constructor(
    private val repository: PregnantMotherRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val motherId = savedStateHandle.getLiveData<Int>("motherId")

    val motherDetails: LiveData<PregnantMotherEntity?> = motherId.switchMap { id ->
        repository.getMotherById(id).asLiveData()
    }

    val visitHistory: LiveData<List<PregnantMotherVisitsEntity>> = motherId.switchMap { id ->
        repository.getVisitsForMother(id).asLiveData()
    }

    // LiveData for each interpretation field
    private val _interpretationAge = MutableLiveData<InterpretationResult>()
    val interpretationAge: LiveData<InterpretationResult> = _interpretationAge

    private val _interpretationChildCount = MutableLiveData<InterpretationResult>()
    val interpretationChildCount: LiveData<InterpretationResult> = _interpretationChildCount

    private val _interpretationTfu = MutableLiveData<InterpretationResult>()
    val interpretationTfu: LiveData<InterpretationResult> = _interpretationTfu

    private val _interpretationImt = MutableLiveData<InterpretationResult>()
    val interpretationImt: LiveData<InterpretationResult> = _interpretationImt

    private val _interpretationDisease = MutableLiveData<InterpretationResult>()
    val interpretationDisease: LiveData<InterpretationResult> = _interpretationDisease

    private val _interpretationHb = MutableLiveData<InterpretationResult>()
    val interpretationHb: LiveData<InterpretationResult> = _interpretationHb

    private val _interpretationLila = MutableLiveData<InterpretationResult>()
    val interpretationLila: LiveData<InterpretationResult> = _interpretationLila

    private val _interpretationTbj = MutableLiveData<InterpretationResult>()
    val interpretationTbj: LiveData<InterpretationResult> = _interpretationTbj

    private val _interpretationWater = MutableLiveData<InterpretationResult>()
    val interpretationWater: LiveData<InterpretationResult> = _interpretationWater

    private val _interpretationBab = MutableLiveData<InterpretationResult>()
    val interpretationBab: LiveData<InterpretationResult> = _interpretationBab

    private val _interpretationSmoke = MutableLiveData<InterpretationResult>()
    val interpretationSmoke: LiveData<InterpretationResult> = _interpretationSmoke

    // ADDED: LiveData for raw values
    private val _pregnancyWeekAgeText = MutableLiveData<String>()
    val pregnancyWeekAgeText: LiveData<String> = _pregnancyWeekAgeText

    private val _tbjValueText = MutableLiveData<String>()
    val tbjValueText: LiveData<String> = _tbjValueText

    private val _nextVisitDateText = MutableLiveData<String>()
    val nextVisitDateText: LiveData<String> = _nextVisitDateText


    fun calculateAllInterpretations() {
        val mother = motherDetails.value
        // Find the latest visit by the highest 'createdAt' timestamp
        val latestVisit = visitHistory.value?.maxByOrNull { it.createdAt }

        if (mother == null) {
            return
        }

        _interpretationAge.value = interpretAge(mother.dateOfBirth)

        if (latestVisit == null) {
            val noData = InterpretationResult("Data Kunjungan Kosong", R.color.status_yellow_text)
            val noDataString = "-"
            _interpretationChildCount.value = noData
            _interpretationTfu.value = noData
            _interpretationImt.value = noData
            _interpretationDisease.value = noData
            _interpretationHb.value = noData
            _interpretationLila.value = noData
            _interpretationTbj.value = noData
            _interpretationWater.value = noData
            _interpretationBab.value = noData
            _interpretationSmoke.value = noData
            _pregnancyWeekAgeText.value = noDataString
            _tbjValueText.value = noDataString
            _nextVisitDateText.value = "" // Hide if no visits
            return
        }

        // Set raw values
        _pregnancyWeekAgeText.value = "${latestVisit.pregnancyWeekAge ?: '-'} Minggu"
        _tbjValueText.value = "${latestVisit.tbj ?: '-'} gram"

        // Calculate all other interpretations based on the latest visit
        _interpretationChildCount.value = interpretChildCount(latestVisit.childNumber)
        _interpretationTfu.value = interpretTfu(latestVisit.tfu, latestVisit.pregnancyWeekAge)
        _interpretationImt.value = interpretImt(latestVisit.currentWeight, latestVisit.currentHeight)
        _interpretationDisease.value = interpretDiseaseHistory(latestVisit.diseaseHistory)
        _interpretationHb.value = interpretHb(latestVisit.hemoglobinLevel)
        _interpretationLila.value = interpretLila(latestVisit.upperArmCircumference)
        _interpretationTbj.value = interpretTbj(latestVisit.tbj, latestVisit.pregnancyWeekAge)
        _interpretationWater.value = interpretWater(latestVisit.mainSourceOfDrinkingWater)
        _interpretationBab.value = interpretBab(latestVisit.defecationFacility)
        _interpretationSmoke.value = interpretSmoke(latestVisit.isExposedToCigarettes)
        _nextVisitDateText.value = formatNextVisitDate(latestVisit.nextVisitDate)
    }

    private fun formatNextVisitDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) {
            return "Kunjungan Berikutnya: Belum ditentukan"
        }
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
            val date = inputFormat.parse(dateString)
            "Kunjungan Berikutnya: ${outputFormat.format(date)}"
        } catch (e: Exception) {
            "Kunjungan Berikutnya: ${dateString}" // Fallback to original string on error
        }
    }

    private fun interpretAge(dobString: String): InterpretationResult {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dobString, formatter)
            val age = Period.between(birthDate, LocalDate.now()).years

            val text = "$age Tahun"
            when {
                age < 20 -> InterpretationResult("$text (Terlalu Muda)", R.color.status_red_text)
                age > 35 -> InterpretationResult("$text (Beresiko)", R.color.status_red_text)
                else -> InterpretationResult("$text (Ideal)", R.color.status_green_text)
            }
        } catch (e: Exception) {
            InterpretationResult("Format Tgl Lahir Salah", R.color.status_red_text)
        }
    }

    private fun interpretChildCount(count: Int?): InterpretationResult {
        if (count == null) return InterpretationResult("-", R.color.status_black_text)
        return if (count > 2) {
            InterpretationResult("$count (Beresiko)", R.color.status_red_text)
        } else {
            InterpretationResult("$count (Ideal)", R.color.status_green_text)
        }
    }

    private fun interpretTfu(tfu: Double?, week: Int?): InterpretationResult {
        if (tfu == null || week == null) return InterpretationResult("-", R.color.status_black_text)

        val ideal = when (week) {
            in 20..21 -> tfu in 17.0..23.0
            in 22..28 -> tfu in 24.0..25.0
            in 29..30 -> tfu == 29.5
            in 37..40 -> tfu in 33.0..38.0
            else -> false
        }

        return if (ideal) {
            InterpretationResult("$tfu cm (Ideal)", R.color.status_green_text)
        } else {
            InterpretationResult("$tfu cm (Beresiko)", R.color.status_red_text)
        }
    }

    private fun interpretImt(weight: Double?, height: Double?): InterpretationResult {
        if (weight == null || height == null || height == 0.0) return InterpretationResult("-", R.color.status_black_text)
        val heightInMeters = height / 100.0
        val imt = weight / (heightInMeters.pow(2))
        val imtFormatted = String.format("%.2f", imt)

        return when {
            imt < 18.5 -> InterpretationResult("$imtFormatted (Kurus)", R.color.status_red_text)
            imt < 25.0 -> InterpretationResult("$imtFormatted (Normal)", R.color.status_green_text)
            imt < 30.0 -> InterpretationResult("$imtFormatted (Gemuk)", R.color.status_red_text)
            else -> InterpretationResult("$imtFormatted (Obesitas)", R.color.status_red_text)
        }
    }

    private fun interpretDiseaseHistory(history: List<String>?): InterpretationResult {
        return if (history.isNullOrEmpty() || (history.size == 1 && history.contains("Tidak Ada"))) {
            InterpretationResult("Tidak Ada", R.color.status_green_text)
        } else {
            InterpretationResult("Beresiko", R.color.status_red_text)
        }
    }

    private fun interpretHb(hb: Double?): InterpretationResult {
        if (hb == null) return InterpretationResult("-", R.color.status_black_text)
        val rujukan = "Melakukan Rujukan Ke Faskes"
        return when {
            hb >= 11.0 -> InterpretationResult("$hb g/dl (Normal)", R.color.status_green_text)
            hb >= 10.0 -> InterpretationResult("$hb g/dl (Anemia Ringan)", R.color.status_yellow_text)
            hb >= 7.0 -> InterpretationResult("$hb g/dl (Anemia Sedang)", R.color.status_red_text, rujukan)
            else -> InterpretationResult("$hb g/dl (Anemia Berat)", R.color.status_red_text, rujukan)
        }
    }

    private fun interpretLila(lila: Double?): InterpretationResult {
        if (lila == null) return InterpretationResult("-", R.color.status_black_text)
        return if (lila < 23.5) {
            InterpretationResult("$lila cm (Beresiko KEK)", R.color.status_red_text)
        } else {
            InterpretationResult("$lila cm (Normal)", R.color.status_green_text)
        }
    }

    private fun interpretTbj(tbj: Double?, week: Int?): InterpretationResult {
        if (tbj == null || week == null) return InterpretationResult("Beresiko", R.color.status_red_text)

        val idealWeight = getIdealTbjForWeek(week)
        if (idealWeight == 0.0) return InterpretationResult("Beresiko (Usia Hamil Diluar Jangkauan)", R.color.status_red_text)

        val tolerance = 0.15 // 15% tolerance
        val minWeight = idealWeight * (1 - tolerance)
        val maxWeight = idealWeight * (1 + tolerance)

        return if (tbj in minWeight..maxWeight) {
            InterpretationResult("Ideal", R.color.status_green_text)
        } else {
            InterpretationResult("Beresiko", R.color.status_red_text)
        }
    }

    private fun getIdealTbjForWeek(week: Int): Double = when (week) {
        8 -> 1.0; 9 -> 2.0; 10 -> 4.0; 11 -> 7.0; 12 -> 14.0; 13 -> 23.0
        14 -> 43.0; 15 -> 70.0; 16 -> 100.0; 17 -> 140.0; 18 -> 190.0
        19 -> 240.0; 20 -> 300.0; 21 -> 360.0; 22 -> 430.0; 23 -> 501.0
        24 -> 600.0; 25 -> 660.0; 26 -> 760.0; 27 -> 875.0; 28 -> 1005.0
        29 -> 1153.0; 30 -> 1319.0; 31 -> 1502.0; 32 -> 1702.0; 33 -> 1918.0
        34 -> 2146.0; 35 -> 2383.0; 36 -> 2622.0; 37 -> 2859.0; 38 -> 3083.0
        39 -> 3288.0; 40 -> 3462.0; 41 -> 3597.0; 42 -> 3685.0
        else -> 0.0
    }

    private fun interpretWater(sources: List<String>?): InterpretationResult {
        if (sources.isNullOrEmpty()) return InterpretationResult("-", R.color.status_black_text)
        val tidakLayakSources = listOf(
            "Sumur tak terlindung", "Mata air tak terlindung",
            "Air Permukaan (sungai/danau/waduk/kolam/irigasi)", "Air Hujan", "Lainnya"
        )
        val isNotLayak = sources.any { it in tidakLayakSources }
        return if (isNotLayak) {
            InterpretationResult("Tidak Layak", R.color.status_red_text)
        } else {
            InterpretationResult("Layak", R.color.status_green_text)
        }
    }

    private fun interpretBab(facilities: List<String>?): InterpretationResult {
        if (facilities.isNullOrEmpty()) return InterpretationResult("-", R.color.status_black_text)
        val layakFacilities = listOf(
            "Jamban milik sendiri dengan leher angsa dan tangki septik/IPAL",
            "Jamban pada MCK komunal dengan leher angsa dan tangki septik/IPAL"
        )
        val isLayak = facilities.any { it in layakFacilities }
        return if (isLayak) {
            InterpretationResult("Layak", R.color.status_green_text)
        } else {
            InterpretationResult("Tidak Layak", R.color.status_red_text)
        }
    }

    private fun interpretSmoke(isExposed: Boolean?): InterpretationResult {
        if (isExposed == null) return InterpretationResult("-", R.color.status_black_text)
        return if (isExposed) {
            InterpretationResult("Beresiko", R.color.status_red_text)
        } else {
            InterpretationResult("Tidak Beresiko", R.color.status_green_text)
        }
    }
}