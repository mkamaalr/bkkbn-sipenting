package com.bkkbnjabar.sipenting.ui.breastfeedingmother.registration

import androidx.lifecycle.*
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toData
import com.bkkbnjabar.sipenting.data.local.mapper.toRegistrationData
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.LocationDetails
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother.CreateBreastfeedingMotherUseCase
import com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother.CreateBreastfeedingMotherVisitUseCase
import com.bkkbnjabar.sipenting.domain.usecase.breastfeedingmother.UpdateBreastfeedingMotherVisitUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreastfeedingMotherRegistrationViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val lookupRepository: LookupRepository,
    private val createBreastFeedingMotherUseCase: CreateBreastfeedingMotherUseCase,
    private val createBreastFeedingMotherVisitUseCase: CreateBreastfeedingMotherVisitUseCase,
    private val updateBreastFeedingMotherVisitUseCase: UpdateBreastfeedingMotherVisitUseCase, // ADDED
    private val repository: BreastfeedingMotherRepository, // ADDED

) : ViewModel() {

    // State utama untuk data formulir
    private val _currentBreastFeedingMother = MutableLiveData(BreastfeedingMotherRegistrationData())
    val currentBreastFeedingMother: LiveData<BreastfeedingMotherRegistrationData> = _currentBreastFeedingMother

    private val _currentBreastFeedingMotherVisit = MutableLiveData(BreastfeedingMotherVisitData())
    val currentBreastFeedingMotherVisit: LiveData<BreastfeedingMotherVisitData> = _currentBreastFeedingMotherVisit

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

    fun loadVisitForEditing(visitId: Int) = viewModelScope.launch {
        // Use .first() to get the first result from the Flow and stop listening.
        val visitEntity = repository.getVisitById(visitId).first()

        if (visitEntity != null) {
            // Also use .first() to get the associated mother data.
            val motherEntity = repository.getMotherById(visitEntity.breastfeedingMotherId).first()

            if (motherEntity != null) {
                // Now that we have all data, update the LiveData once.
                _currentBreastFeedingMother.value = motherEntity.toRegistrationData()
                _currentBreastFeedingMotherVisit.value = visitEntity.toData()
            } else {
                _saveResult.value = Resource.Error("Data ibu tidak ditemukan untuk kunjungan ini.")
            }
        } else {
            _saveResult.value = Resource.Error("Data kunjungan tidak ditemukan.")
        }
    }

    fun resetForm() {
        _currentBreastFeedingMother.value = BreastfeedingMotherRegistrationData()
        _currentBreastFeedingMotherVisit.value = BreastfeedingMotherVisitData()
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
                if (_currentBreastFeedingMother.value?.name.isNullOrEmpty()) {
                    prefillFormData(details)
                }
            }
        }
    }

    private fun prefillFormData(details: LocationDetails?) {
        details ?: return
        updateBreastfeedingMotherData(
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

    fun updateBreastfeedingMotherData(
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
        val currentData = _currentBreastFeedingMother.value ?: BreastfeedingMotherRegistrationData()
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
        _currentBreastFeedingMother.value = updatedData
    }

    fun updateBreastfeedingMotherVisitData(
        visitDate: String? = null,
        currentHeight: Double? = null,
        currentWeight: Double? = null,
        isTwin: Boolean? = null,
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
        breastfeedingMotherStatusId: Int? = null,
        deliveryPlaceId: Int? = null,
        birthAssistantId: Int? = null,
        contraceptionOptionId: Int? = null,
        mainSourceOfDrinkingWater: List<String>? = null,
        mainSourceOfDrinkingWaterOther: String? = null,
        defecationFacility: List<String>? = null,
        defecationFacilityOther: String? = null,
        socialAssistanceFacilitationOptions: List<String>? = null,
        socialAssistanceFacilitationOptionsOther: String? = null,
        imagePath1: String? = null,
        imagePath2: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        isReceivedMbg: Boolean? = null,
        isAsiExclusive: Boolean? = null,
    ) {
        val currentData = _currentBreastFeedingMotherVisit.value ?: BreastfeedingMotherVisitData()
        val updatedData = currentData.copy(
            visitDate = visitDate ?: currentData.visitDate,
            currentHeight = currentHeight ?: currentData.currentHeight,
            currentWeight = currentWeight ?: currentData.currentWeight,
            isTwin = isTwin ?: currentData.isTwin,
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
            deliveryPlaceId = deliveryPlaceId ?: currentData.deliveryPlaceId,
            birthAssistantId = birthAssistantId ?: currentData.birthAssistantId,
            contraceptionOptionId = contraceptionOptionId ?: currentData.contraceptionOptionId,
            mainSourceOfDrinkingWater = mainSourceOfDrinkingWater ?: currentData.mainSourceOfDrinkingWater,
            mainSourceOfDrinkingWaterOther = mainSourceOfDrinkingWaterOther ?: currentData.mainSourceOfDrinkingWaterOther,
            defecationFacility = defecationFacility ?: currentData.defecationFacility,
            defecationFacilityOther = defecationFacilityOther ?: currentData.defecationFacilityOther,
            socialAssistanceFacilitationOptions = socialAssistanceFacilitationOptions ?: currentData.socialAssistanceFacilitationOptions,
            socialAssistanceFacilitationOptionsOther = socialAssistanceFacilitationOptionsOther ?: currentData.socialAssistanceFacilitationOptionsOther,
            imagePath1 = imagePath1 ?: currentData.imagePath1,
            imagePath2 = imagePath2 ?: currentData.imagePath2,
            latitude = latitude ?: currentData.latitude,
            longitude = longitude ?: currentData.longitude,
            isReceivedMbg = isReceivedMbg ?: currentData.isReceivedMbg,
            breastfeedingMotherStatusId = breastfeedingMotherStatusId ?: currentData.breastfeedingMotherStatusId,
            isAsiExclusive = isAsiExclusive ?: currentData.isAsiExclusive,
        )
        _currentBreastFeedingMotherVisit.value = updatedData
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

    fun startNewVisitForExistingMother(mother: BreastfeedingMotherEntity) {
        // Populate the mother's data so the form has context
        _currentBreastFeedingMother.value = mother.toRegistrationData()

        // Clear only the visit data, but crucially link it to the mother
        _currentBreastFeedingMotherVisit.value = BreastfeedingMotherVisitData(breastfeedingMotherId = mother.localId)
        _saveResult.value = Resource.Idle
    }

    fun saveAllData() = viewModelScope.launch {
        _saveResult.value = Resource.Loading
        val motherData = _currentBreastFeedingMother.value
        val visitData = _currentBreastFeedingMotherVisit.value

        if (motherData == null || visitData == null) {
            _saveResult.value = Resource.Error("Data tidak lengkap")
            return@launch
        }

        // Check if the visit already has a local ID. If so, it's an update.
        if (visitData.localVisitId != null && visitData.localVisitId > 0) {
            val result = updateBreastFeedingMotherVisitUseCase.execute(visitData)
            _saveResult.value = result
        } else if (motherData.localId != null && motherData.localId > 0) {
            // This is a new visit for an existing mother
            val visitResult = createBreastFeedingMotherVisitUseCase.execute(visitData)
            _saveResult.value = visitResult
        } else {
            // This is a new mother and their first visit
            when (val motherResult = createBreastFeedingMotherUseCase.execute(motherData)) {
                is Resource.Success -> {
                    val newMotherId = motherResult.data
                    if (newMotherId != null) {
                        val finalVisitData = visitData.copy(breastfeedingMotherId = newMotherId.toInt())
                        val visitResult = createBreastFeedingMotherVisitUseCase.execute(finalVisitData)
                        _saveResult.value = visitResult
                    } else {
                        _saveResult.value = Resource.Error("Gagal mendapatkan ID Ibu setelah disimpan.")
                    }
                }
                is Resource.Error -> _saveResult.value = Resource.Error(motherResult.message)
                else -> {}
            }
        }
    }

    fun updateChipGroupData(chipGroupId: Int, newSelection: List<String>) {
        when (chipGroupId) {
            R.id.chip_group_drinking_water -> updateBreastfeedingMotherVisitData(mainSourceOfDrinkingWater = newSelection)
            R.id.chip_group_defecation_facility -> updateBreastfeedingMotherVisitData(defecationFacility = newSelection)
            R.id.chip_group_social_assistance -> updateBreastfeedingMotherVisitData(socialAssistanceFacilitationOptions = newSelection)
        }
    }
}