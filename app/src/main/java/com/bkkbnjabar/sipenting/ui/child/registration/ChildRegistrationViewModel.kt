package com.bkkbnjabar.sipenting.ui.child.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toDomain
import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildData
import com.bkkbnjabar.sipenting.data.model.child.ChildMotherData
import com.bkkbnjabar.sipenting.data.model.child.ChildRegistrationData
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.data.repository.ChildMotherRepository
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.LocationDetails
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.domain.usecase.child.CreateChildMotherUseCase
import com.bkkbnjabar.sipenting.domain.usecase.child.CreateChildUseCase
import com.bkkbnjabar.sipenting.domain.usecase.child.CreateChildVisitUseCase
import com.bkkbnjabar.sipenting.domain.usecase.child.UpdateChildVisitUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildRegistrationViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val lookupRepository: LookupRepository,
    private val childRepository: ChildRepository,
    private val childMotherRepository: ChildMotherRepository, // For loading mother data
    private val createChildUseCase: CreateChildUseCase,
    private val createChildMotherUseCase: CreateChildMotherUseCase, // ADDED
    private val createChildVisitUseCase: CreateChildVisitUseCase,
    private val updateChildVisitUseCase: UpdateChildVisitUseCase
) : ViewModel() {

    private val _currentChildMother = MutableLiveData<ChildMotherData>()
    val currentChildMother: LiveData<ChildMotherData> = _currentChildMother


    // State for the child's main data (name, NIK, address)
    private val _currentChild = MutableLiveData<ChildData>()
    val currentChild: LiveData<ChildData> = _currentChild

    // State for the child's visit form data
    private val _currentChildVisit = MutableLiveData<ChildVisitData>()
    val currentChildVisit: LiveData<ChildVisitData> = _currentChildVisit

    // State for the result of the save operation
    private val _saveResult = MutableLiveData<Resource<Unit>>(Resource.Idle)
    val saveResult: LiveData<Resource<Unit>> = _saveResult

    // State for the user's location details for pre-filling address
    private val _userLocationDetails = MutableLiveData<LocationDetails?>()
    val userLocationDetails: LiveData<LocationDetails?> = _userLocationDetails

    private val _referralStatusOptions = MutableLiveData<List<String>>()
    val referralStatusOptions: LiveData<List<String>> = _referralStatusOptions

    private val _socialAssistanceStatusOptions = MutableLiveData<List<String>>()
    val socialAssistanceStatusOptions: LiveData<List<String>> = _socialAssistanceStatusOptions

    private val _kkaResultOptions = MutableLiveData<List<String>>()
    val kkaResultOptions: LiveData<List<String>> = _kkaResultOptions

    // State for location dropdowns
    private val _rws = MutableLiveData<List<Rw>>()
    val rws: LiveData<List<Rw>> = _rws
    private val _rts = MutableLiveData<List<Rt>>()
    val rts: LiveData<List<Rt>> = _rts

    // --- LiveData for all lookup options needed in the form ---
    val childStatuses: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("child-statuses").asLiveData()
//    val kkaResults: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("kka-results").asLiveData()
    val contraceptionTypes: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("contraception-options").asLiveData()
    val contraceptionRejectionReasons: LiveData<List<LookupItem>> =
        lookupRepository.getLookupOptions("contraception-options-reject").asLiveData()
    val counselingTypes: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("counseling-types").asLiveData()
    val immunizations: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("immunization-options").asLiveData()
    val drinkingWaterSources: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("main-sources-of-drinking-water").asLiveData()
    val defecationFacilities: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("defecation-facilities").asLiveData()
    val socialAssistanceOptions: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("social-assistance-facilitation-options").asLiveData()
    val pregnantMotherStatuses: LiveData<List<LookupItem>> = lookupRepository.getLookupOptions("pregnant-mother-statuses").asLiveData()

    init {
        resetForm()
        loadStaticOptions()
    }

    fun loadVisitForEditing(visitId: Int) = viewModelScope.launch {
        val visitEntity = childRepository.getVisitById(visitId).first()
        if (visitEntity != null) {
            val childEntity = childRepository.getChildById(visitEntity.childId).first()
            if (childEntity != null) {
                val motherEntity = childMotherRepository.getMotherById(childEntity.motherId).first()
                if (motherEntity != null) {
                    _currentChildMother.value = motherEntity.toDomain()
                    _currentChild.value = childEntity.toDomain()
                    _currentChildVisit.value = visitEntity.toDomain()
                } else {
                    _saveResult.value = Resource.Error("Data ibu tidak ditemukan untuk anak ini.")
                }
            } else {
                _saveResult.value = Resource.Error("Data anak tidak ditemukan untuk kunjungan ini.")
            }
        } else {
            _saveResult.value = Resource.Error("Data kunjungan tidak ditemukan.")
        }
    }

    fun resetForm() {
        _currentChildMother.value = ChildMotherData(name = "", nik = "", dateOfBirth = "", fullAddress = "", provinsiName = "", kabupatenName = "", kecamatanName = "", kelurahanName = "", rtName = "", rwName = "", provinsiId = 0, kabupatenId = 0, kecamatanId = 0, kelurahanId = 0, rtId = 0, rwId = 0, phoneNumber = "")
        _currentChild.value = ChildData(localId = 0, motherId = 0, name = "", nik = "", dateOfBirth = "", fullAddress = "", provinsiName = "", kabupatenName = "", kecamatanName = "", kelurahanName = "", rtName = "", rwName = "", provinsiId = 0, kabupatenId = 0, kecamatanId = 0, kelurahanId = 0, rtId = 0, rwId = 0, phoneNumber = "")
        _currentChildVisit.value = ChildVisitData(childId = 0, onContraception = false)
        _saveResult.value = Resource.Idle
        loadUserLocationDetails()
    }

    private fun loadUserLocationDetails() = viewModelScope.launch {
        sharedPrefsManager.getUserSession()?.kelurahanId?.let {
            val details = lookupRepository.getLocationDetailsByKelurahanId(it)
            _userLocationDetails.postValue(details)
            if (_currentChild.value?.localId == 0) {
                prefillLocationData(details)
            }
        }
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
        _kkaResultOptions.value = listOf(
            "Perkembangan Anak Sesuai Usia",
            "Perkembangan Anak Tidak Sesuai Usia"
        )
    }

    private fun prefillLocationData(details: LocationDetails?) {
        details ?: return
        updateChildRegistrationData(
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

    fun startNewVisitForExistingChild(child: ChildEntity) {
        viewModelScope.launch {
            val mother = childMotherRepository.getMotherById(child.motherId).first()
            if (mother != null) {
                _currentChildMother.value = mother.toDomain()
                _currentChild.value = child.toDomain()
                _currentChildVisit.value = ChildVisitData(childId = child.localId, onContraception = false)
                _saveResult.value = Resource.Idle
            }
        }
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

    fun updateChildRegistrationData(
        name: String? = null, nik: String? = null, dateOfBirth: String? = null, phoneNumber: String? = null,
        provinsiName: String? = null, provinsiId: Int? = null, kabupatenName: String? = null, kabupatenId: Int? = null,
        kecamatanName: String? = null, kecamatanId: Int? = null, kelurahanName: String? = null, kelurahanId: Int? = null,
        rwName: String? = null, rwId: Int? = null, rtName: String? = null, rtId: Int? = null, fullAddress: String? = null
    ) {
        val currentDataMother = _currentChildMother.value ?: return
        _currentChildMother.value = currentDataMother.copy(
            name = name ?: currentDataMother.name, nik = nik ?: currentDataMother.nik, dateOfBirth = dateOfBirth ?: currentDataMother.dateOfBirth,
            phoneNumber = phoneNumber ?: currentDataMother.phoneNumber, provinsiName = provinsiName ?: currentDataMother.provinsiName,
            provinsiId = provinsiId ?: currentDataMother.provinsiId, kabupatenName = kabupatenName ?: currentDataMother.kabupatenName,
            kabupatenId = kabupatenId ?: currentDataMother.kabupatenId, kecamatanName = kecamatanName ?: currentDataMother.kecamatanName,
            kecamatanId = kecamatanId ?: currentDataMother.kecamatanId, kelurahanName = kelurahanName ?: currentDataMother.kelurahanName,
            kelurahanId = kelurahanId ?: currentDataMother.kelurahanId, rwName = rwName ?: currentDataMother.rwName, rwId = rwId ?: currentDataMother.rwId,
            rtName = rtName ?: currentDataMother.rtName, rtId = rtId ?: currentDataMother.rtId, fullAddress = fullAddress ?: currentDataMother.fullAddress
        )

        val currentData = _currentChild.value ?: return
        _currentChild.value = currentData.copy(
            name = name ?: currentData.name, nik = nik ?: currentData.nik, dateOfBirth = dateOfBirth ?: currentData.dateOfBirth,
            phoneNumber = phoneNumber ?: currentData.phoneNumber, provinsiName = provinsiName ?: currentData.provinsiName,
            provinsiId = provinsiId ?: currentData.provinsiId, kabupatenName = kabupatenName ?: currentData.kabupatenName,
            kabupatenId = kabupatenId ?: currentData.kabupatenId, kecamatanName = kecamatanName ?: currentData.kecamatanName,
            kecamatanId = kecamatanId ?: currentData.kecamatanId, kelurahanName = kelurahanName ?: currentData.kelurahanName,
            kelurahanId = kelurahanId ?: currentData.kelurahanId, rwName = rwName ?: currentData.rwName, rwId = rwId ?: currentData.rwId,
            rtName = rtName ?: currentData.rtName, rtId = rtId ?: currentData.rtId, fullAddress = fullAddress ?: currentData.fullAddress
        )

    }

    fun updateChildVisitData(
        visitDate: String? = null, pregnancyAgeWhenChildbirth: String? = null, weightBirth: Double? = null, heightBirth: Double? = null,
        isAsiExclusive: Boolean? = null, onContraception: Boolean? = null, contraceptionTypeId: Int? = null,
        contraceptionReasonForUse: String? = null, contraceptionRTypeId: Int? = null, measurementDate: String? = null,
        weightMeasurement: Double? = null, heightMeasurement: Double? = null, isOngoingAsi: Boolean? = null, isMpasi: Boolean? = null,
        isKkaFilled: Boolean? = null, kkaResult: String? = null, isExposedToCigarettes: Boolean? = null, isPosyanduMonth: Boolean? = null,
        isBkbMonth: Boolean? = null, isCounselingReceived: Boolean? = null, isReceivedMbg: Boolean? = null, headCircumference: Double? = null,
        counselingTypeId: Int? = null, immunizationsGiven: List<String>? = null, mainSourceOfDrinkingWater: List<String>? = null,
        mainSourceOfDrinkingWaterOther: String? = null, defecationFacility: List<String>? = null, defecationFacilityOther: String? = null,
        facilitatingReferralServiceStatus: String? = null, facilitatingSocialAssistanceStatus: String? = null,
        socialAssistanceFacilitationOptions: List<String>? = null, socialAssistanceFacilitationOptionsOther: String? = null,
        pregnantMotherStatusId: Int? = null, nextVisitDate: String? = null, tpkNotes: String? = null,
        imagePath1: String? = null, imagePath2: String? = null, latitude: Double? = null, longitude: Double? = null
    ) {
        val currentData = _currentChildVisit.value ?: return
        _currentChildVisit.value = currentData.copy(
            visitDate = visitDate ?: currentData.visitDate, pregnancyAgeWhenChildbirth = pregnancyAgeWhenChildbirth ?: currentData.pregnancyAgeWhenChildbirth,
            weightBirth = weightBirth ?: currentData.weightBirth, heightBirth = heightBirth ?: currentData.heightBirth,
            isAsiExclusive = isAsiExclusive ?: currentData.isAsiExclusive, onContraception = onContraception ?: currentData.onContraception,
            contraceptionTypeId = contraceptionTypeId ?: currentData.contraceptionTypeId, contraceptionReasonForUse = contraceptionReasonForUse ?: currentData.contraceptionReasonForUse,
            contraceptionRTypeId = contraceptionRTypeId ?: currentData.contraceptionRTypeId, measurementDate = measurementDate ?: currentData.measurementDate,
            weightMeasurement = weightMeasurement ?: currentData.weightMeasurement, heightMeasurement = heightMeasurement ?: currentData.heightMeasurement,
            isOngoingAsi = isOngoingAsi ?: currentData.isOngoingAsi, isMpasi = isMpasi ?: currentData.isMpasi,
            isKkaFilled = isKkaFilled ?: currentData.isKkaFilled, kkaResult = kkaResult ?: currentData.kkaResult,
            isExposedToCigarettes = isExposedToCigarettes ?: currentData.isExposedToCigarettes, isPosyanduMonth = isPosyanduMonth ?: currentData.isPosyanduMonth,
            isBkbMonth = isBkbMonth ?: currentData.isBkbMonth, isCounselingReceived = isCounselingReceived ?: currentData.isCounselingReceived,
            isReceivedMbg = isReceivedMbg ?: currentData.isReceivedMbg, headCircumference = headCircumference ?: currentData.headCircumference,
            counselingTypeId = counselingTypeId ?: currentData.counselingTypeId, immunizationsGiven = immunizationsGiven ?: currentData.immunizationsGiven,
            mainSourceOfDrinkingWater = mainSourceOfDrinkingWater ?: currentData.mainSourceOfDrinkingWater, mainSourceOfDrinkingWaterOther = mainSourceOfDrinkingWaterOther ?: currentData.mainSourceOfDrinkingWaterOther,
            defecationFacility = defecationFacility ?: currentData.defecationFacility, defecationFacilityOther = defecationFacilityOther ?: currentData.defecationFacilityOther,
            facilitatingReferralServiceStatus = facilitatingReferralServiceStatus ?: currentData.facilitatingReferralServiceStatus,
            facilitatingSocialAssistanceStatus = facilitatingSocialAssistanceStatus ?: currentData.facilitatingSocialAssistanceStatus,
            socialAssistanceFacilitationOptions = socialAssistanceFacilitationOptions ?: currentData.socialAssistanceFacilitationOptions,
            socialAssistanceFacilitationOptionsOther = socialAssistanceFacilitationOptionsOther ?: currentData.socialAssistanceFacilitationOptionsOther,
            pregnantMotherStatusId = pregnantMotherStatusId ?: currentData.pregnantMotherStatusId, nextVisitDate = nextVisitDate ?: currentData.nextVisitDate,
            tpkNotes = tpkNotes ?: currentData.tpkNotes, imagePath1 = imagePath1 ?: currentData.imagePath1,
            imagePath2 = imagePath2 ?: currentData.imagePath2, latitude = latitude ?: currentData.latitude, longitude = longitude ?: currentData.longitude
        )
    }

    fun saveAllData() = viewModelScope.launch {
        _saveResult.value = Resource.Loading
        val motherData = _currentChildMother.value
        val childData = _currentChild.value
        val visitData = _currentChildVisit.value

        if (motherData == null || childData == null || visitData == null) {
            _saveResult.value = Resource.Error("Data tidak lengkap")
            return@launch
        }

        if (visitData.localVisitId != 0) {
            _saveResult.value = updateChildVisitUseCase.execute(visitData)
            return@launch
        }

        if (childData.localId != 0) {
            _saveResult.value = createChildVisitUseCase.execute(visitData)
            return@launch
        }

        // --- CORRECTED LOGIC FOR NEW MOTHER AND CHILD ---
        when (val motherResult = createChildMotherUseCase.execute(motherData)) {
            is Resource.Success -> {
                val newMotherId = motherResult.data
                if (newMotherId == null) {
                    _saveResult.value = Resource.Error("Gagal mendapatkan ID Ibu setelah disimpan.")
                    return@launch
                }

                val finalChildData = childData.copy(motherId = newMotherId.toInt())

                when (val childResult = createChildUseCase.execute(finalChildData)) {
                    is Resource.Success -> {
                        val newChildId = childResult.data
                        if (newChildId == null) {
                            _saveResult.value = Resource.Error("Gagal mendapatkan ID Anak setelah disimpan.")
                            return@launch
                        }

                        val finalVisitData = visitData.copy(childId = newChildId.toInt())
                        _saveResult.value = createChildVisitUseCase.execute(finalVisitData)
                    }
                    is Resource.Error -> _saveResult.value = Resource.Error(childResult.message)
                    else -> {}
                }
            }
            is Resource.Error -> _saveResult.value = Resource.Error(motherResult.message)
            else -> {}
        }
    }

    fun updateChipGroupData(chipGroupId: Int, newSelection: List<String>) {
        when (chipGroupId) {
            R.id.chip_group_immunizations -> updateChildVisitData(immunizationsGiven = newSelection)
            R.id.chip_group_drinking_water -> updateChildVisitData(mainSourceOfDrinkingWater = newSelection)
            R.id.chip_group_defecation_facility -> updateChildVisitData(defecationFacility = newSelection)
            R.id.chip_group_social_assistance -> updateChildVisitData(socialAssistanceFacilitationOptions = newSelection)
        }
    }
}
