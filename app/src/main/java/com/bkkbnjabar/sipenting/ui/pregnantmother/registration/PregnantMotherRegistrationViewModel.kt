package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.*
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherUseCase
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherVisitUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PregnantMotherRegistrationViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val lookupRepository: LookupRepository,
    private val createPregnantMotherUseCase: CreatePregnantMotherUseCase,
    private val createPregnantMotherVisitUseCase: CreatePregnantMotherVisitUseCase
) : ViewModel() {

    // State utama untuk data formulir
    private val _currentPregnantMother = MutableLiveData(PregnantMotherRegistrationData())
    val currentPregnantMother: LiveData<PregnantMotherRegistrationData> = _currentPregnantMother

    private val _currentPregnantMotherVisit = MutableLiveData(PregnantMotherVisitData())
    val currentPregnantMotherVisit: LiveData<PregnantMotherVisitData> = _currentPregnantMotherVisit

    // State untuk hasil penyimpanan
    private val _saveResult = MutableLiveData<Resource<Unit>>(Resource.Idle)
    val saveResult: LiveData<Resource<Unit>> = _saveResult

    // State untuk detail lokasi pengguna yang login
    private val _userLocationDetails = MutableLiveData<LocationDetails?>()
    val userLocationDetails: LiveData<LocationDetails?> = _userLocationDetails

    private val _referralStatusOptions = MutableLiveData<List<String>>()
    val referralStatusOptions: LiveData<List<String>> = _referralStatusOptions

    private val _socialAssistanceStatusOptions = MutableLiveData<List<String>>()
    val socialAssistanceStatusOptions: LiveData<List<String>> = _socialAssistanceStatusOptions

    // State untuk daftar pilihan dropdown Halaman 1
    private val _rws = MutableLiveData<List<Rw>>()
    val rws: LiveData<List<Rw>> = _rws
    private val _rts = MutableLiveData<List<Rt>>()
    val rts: LiveData<List<Rt>> = _rts

    // LiveData untuk semua dropdown di Halaman 2
    val counselingTypes: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("counseling-types").asLiveData()
    val deliveryPlaces: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("delivery-places").asLiveData()
    val birthAssistants: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("birth-assistants").asLiveData()
    val contraceptionOptions: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("contraception-options").asLiveData()
    val givenBirthStatuses: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("given-birth-statuses").asLiveData()
    val pregnantMotherStatuses: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("pregnant-mother-statuses").asLiveData()
    val diseaseHistories: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("disease-histories").asLiveData()
    val mainSourcesOfDrinkingWater: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("main-sources-of-drinking-water").asLiveData()
    val defecationFacilities: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("defecation-facilities").asLiveData()
    val socialAssistanceOptions: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("social-assistance-facilitation-options").asLiveData()

    init {
        resetForm()
        loadStaticOptions()
    }

    fun resetForm() {
        _currentPregnantMother.value = PregnantMotherRegistrationData()
        _currentPregnantMotherVisit.value = PregnantMotherVisitData()
        _saveResult.value = Resource.Idle
        _userLocationDetails.value = null
        loadUserLocationDetails()
    }

    private fun loadStaticOptions() {
        _referralStatusOptions.value = listOf(
            "Ya, Sedang Proses",
            "Ya, Sudah Mendapatkan Pelayanan Rujukan",
            "Tidak"
        )
        _socialAssistanceStatusOptions.value = listOf(
            "Ya, Sedang Proses",
            "Ya, Sudah Mendapatkan Bantuan Sosial",
            "Tidak"
        )
    }

    private fun loadUserLocationDetails() {
        viewModelScope.launch {
            val kelurahanId = sharedPrefsManager.getUserSession()?.kelurahanId
            if (kelurahanId != null) {
                val details = lookupRepository.getLocationDetailsByKelurahanId(kelurahanId)
                _userLocationDetails.postValue(details)
                if (_currentPregnantMother.value?.name.isNullOrEmpty()) {
                    prefillFormData(details)
                }
            }
        }
    }

    private fun prefillFormData(details: LocationDetails?) {
        details ?: return
        updatePregnantMotherData(
            provinsiId = details.provinsiId?.toIntOrNull(),
            provinsiName = details.provinsiName,
            kabupatenId = details.kabupatenId?.toIntOrNull(),
            kabupatenName = details.kabupatenName,
            kecamatanId = details.kecamatanId?.toIntOrNull(),
            kecamatanName = details.kecamatanName,
            kelurahanId = details.kelurahanId?.toIntOrNull(),
            kelurahanName = details.kelurahanName
        )
        details.kelurahanId?.toIntOrNull()?.let { getRWS(it) }
    }

    fun updatePregnantMotherData(
        name: String? = null,
        nik: String? = null,
        dateOfBirth: String? = null,
        phoneNumber: String? = null,
        provinsiName: String? = null,
        provinsiId: Int? = null,
        kabupatenName: String? = null,
        kabupatenId: Int? = null,
        kecamatanName: String? = null,
        kecamatanId: Int? = null,
        kelurahanName: String? = null,
        kelurahanId: Int? = null,
        rwName: String? = null,
        rwId: Int? = null,
        rtName: String? = null,
        rtId: Int? = null,
        fullAddress: String? = null
    ) {
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()
        val updatedData = currentData.copy(
            name = name ?: currentData.name,
            nik = nik ?: currentData.nik,
            dateOfBirth = dateOfBirth ?: currentData.dateOfBirth,
            phoneNumber = phoneNumber ?: currentData.phoneNumber,
            provinsiName = provinsiName ?: currentData.provinsiName,
            provinsiId = provinsiId ?: currentData.provinsiId,
            kabupatenName = kabupatenName ?: currentData.kabupatenName,
            kabupatenId = kabupatenId ?: currentData.kabupatenId,
            kecamatanName = kecamatanName ?: currentData.kecamatanName,
            kecamatanId = kecamatanId ?: currentData.kecamatanId,
            kelurahanName = kelurahanName ?: currentData.kelurahanName,
            kelurahanId = kelurahanId ?: currentData.kelurahanId,
            rwName = rwName ?: currentData.rwName,
            rwId = rwId ?: currentData.rwId,
            rtName = rtName ?: currentData.rtName,
            rtId = rtId ?: currentData.rtId,
            fullAddress = fullAddress ?: currentData.fullAddress
        )
        _currentPregnantMother.value = updatedData
    }

    fun updatePregnantMotherVisitData(
        visitDate: String? = null,
        childNumber: Int? = null,
        dateOfBirthLastChild: String? = null,
        pregnancyWeekAge: Int? = null,
        weightTrimester1: Double? = null,
        currentHeight: Double? = null,
        currentWeight: Double? = null,
        isHbChecked: Boolean? = null,
        hemoglobinLevel: Double? = null,
        hemoglobinLevelReason: String? = null, // ADDED
        upperArmCircumference: Double? = null,
        isTwin: Boolean? = null,
        numberOfTwins: Int? = null,
        isEstimatedFetalWeightChecked: Boolean? = null,
        tbj: Double? = null, // ADDED
        isExposedToCigarettes: Boolean? = null,
        isCounselingReceived: Boolean? = null,
        counselingTypeId: Int? = null,
        isIronTablesReceived: Boolean? = null,
        isIronTablesTaken: Boolean? = null,
        facilitatingReferralServiceStatus: String? = null,
        facilitatingSocialAssistanceStatus: String? = null,
        nextVisitDate: String? = null,
        tpkNotes: String? = null,
        isAlive: Boolean? = null,
        isGivenBirth: Boolean? = null,
        givenBirthStatusId: Int? = null,
        pregnantMotherStatusId: Int? = null,
        deliveryPlaceId: Int? = null,
        birthAssistantId: Int? = null,
        contraceptionOptionId: Int? = null,
        diseaseHistory: List<String>? = null,
        mainSourceOfDrinkingWater: List<String>? = null,
        defecationFacility: List<String>? = null,
        socialAssistanceFacilitationOptions: List<String>? = null,
        imagePath1: String? = null,
        imagePath2: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        isReceivedMbg: Boolean? = null,
        isTfuMeasured: Boolean? = null,
        tfu: Double? = null
    ) {
        val currentData = _currentPregnantMotherVisit.value ?: PregnantMotherVisitData()
        val updatedData = currentData.copy(
            visitDate = visitDate ?: currentData.visitDate,
            childNumber = childNumber ?: currentData.childNumber,
            dateOfBirthLastChild = dateOfBirthLastChild ?: currentData.dateOfBirthLastChild,
            pregnancyWeekAge = pregnancyWeekAge ?: currentData.pregnancyWeekAge,
            weightTrimester1 = weightTrimester1 ?: currentData.weightTrimester1,
            currentHeight = currentHeight ?: currentData.currentHeight,
            currentWeight = currentWeight ?: currentData.currentWeight,
            isHbChecked = isHbChecked ?: currentData.isHbChecked,
            hemoglobinLevel = hemoglobinLevel ?: currentData.hemoglobinLevel,
            hemoglobinLevelReason = hemoglobinLevelReason ?: currentData.hemoglobinLevelReason, // ADDED
            upperArmCircumference = upperArmCircumference ?: currentData.upperArmCircumference,
            isTwin = isTwin ?: currentData.isTwin,
            numberOfTwins = numberOfTwins ?: currentData.numberOfTwins,
            isEstimatedFetalWeightChecked = isEstimatedFetalWeightChecked ?: currentData.isEstimatedFetalWeightChecked,
            tbj = tbj ?: currentData.tbj, // ADDED
            isExposedToCigarettes = isExposedToCigarettes ?: currentData.isExposedToCigarettes,
            isCounselingReceived = isCounselingReceived ?: currentData.isCounselingReceived,
            counselingTypeId = counselingTypeId ?: currentData.counselingTypeId,
            isIronTablesReceived = isIronTablesReceived ?: currentData.isIronTablesReceived,
            isIronTablesTaken = isIronTablesTaken ?: currentData.isIronTablesTaken,
            facilitatingReferralServiceStatus = facilitatingReferralServiceStatus ?: currentData.facilitatingReferralServiceStatus,
            facilitatingSocialAssistanceStatus = facilitatingSocialAssistanceStatus ?: currentData.facilitatingSocialAssistanceStatus,
            nextVisitDate = nextVisitDate ?: currentData.nextVisitDate,
            tpkNotes = tpkNotes ?: currentData.tpkNotes,
            isAlive = isAlive ?: currentData.isAlive,
            isGivenBirth = isGivenBirth ?: currentData.isGivenBirth,
            givenBirthStatusId = givenBirthStatusId ?: currentData.givenBirthStatusId,
            pregnantMotherStatusId = pregnantMotherStatusId ?: currentData.pregnantMotherStatusId,
            deliveryPlaceId = deliveryPlaceId ?: currentData.deliveryPlaceId,
            birthAssistantId = birthAssistantId ?: currentData.birthAssistantId,
            contraceptionOptionId = contraceptionOptionId ?: currentData.contraceptionOptionId,
            diseaseHistory = diseaseHistory ?: currentData.diseaseHistory,
            mainSourceOfDrinkingWater = mainSourceOfDrinkingWater ?: currentData.mainSourceOfDrinkingWater,
            defecationFacility = defecationFacility ?: currentData.defecationFacility,
            socialAssistanceFacilitationOptions = socialAssistanceFacilitationOptions ?: currentData.socialAssistanceFacilitationOptions,
            imagePath1 = imagePath1 ?: currentData.imagePath1,
            imagePath2 = imagePath2 ?: currentData.imagePath2,
            latitude = latitude ?: currentData.latitude,
            longitude = longitude ?: currentData.longitude,
            isReceivedMbg = isReceivedMbg ?: currentData.isReceivedMbg,
            isTfuMeasured = isTfuMeasured ?: currentData.isTfuMeasured,
            tfu = tfu ?: currentData.tfu
        )
        _currentPregnantMotherVisit.value = updatedData
    }

    fun getRWS(kelurahanId: Int) = viewModelScope.launch {
        lookupRepository.getRWSByKelurahanFromRoom(kelurahanId).collectLatest {
            _rws.postValue(it)
        }
    }

    fun getRTS(rwId: Int) = viewModelScope.launch {
        lookupRepository.getRTSByRwFromRoom(rwId).collectLatest {
            _rts.postValue(it)
        }
    }

    fun startNewVisitForExistingMother(mother: PregnantMotherEntity) {
        // Populate the mother's data so the form has context
        _currentPregnantMother.value = mother.toRegistrationData()

        // Clear only the visit data, but crucially link it to the mother
        _currentPregnantMotherVisit.value = PregnantMotherVisitData(pregnantMotherLocalId = mother.localId)
        _saveResult.value = Resource.Idle
    }

    fun saveAllData() = viewModelScope.launch {
        _saveResult.value = Resource.Loading
        val motherData = _currentPregnantMother.value
        val visitData = _currentPregnantMotherVisit.value

        if (motherData == null || visitData == null) {
            _saveResult.value = Resource.Error("Data tidak lengkap")
            return@launch
        }

        // Check if the mother already exists in the database.
        if (motherData.localId != null && motherData.localId > 0) {
            // Mother exists, just save the visit data.
            val visitResult = createPregnantMotherVisitUseCase.execute(visitData)
            _saveResult.value = visitResult
        } else {
            // Mother is new. Save the mother first, then use the new ID to save the visit.
            when (val motherResult = createPregnantMotherUseCase.execute(motherData)) {
                is Resource.Success -> {
                    val newMotherId = motherResult.data
                    if (newMotherId != null) {
                        val finalVisitData = visitData.copy(pregnantMotherLocalId = newMotherId.toInt())
                        val visitResult = createPregnantMotherVisitUseCase.execute(finalVisitData)
                        _saveResult.value = visitResult
                    } else {
                        _saveResult.value = Resource.Error("Gagal mendapatkan ID Ibu setelah disimpan.")
                    }
                }
                is Resource.Error -> {
                    _saveResult.value = Resource.Error(motherResult.message)
                }
                else -> { /* No-op for Loading/Idle */ }
            }
        }
    }
}