package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData // Perlu diimpor untuk StateFlow.asLiveData()
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.LocationDetails
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherUseCase
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherVisitUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PregnantMotherRegistrationViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val lookupRepository: LookupRepository, // Digunakan untuk memuat data lokasi
    private val createPregnantMotherUseCase: CreatePregnantMotherUseCase, // Digunakan untuk menyimpan data ibu hamil
    private val createPregnantMotherVisitUseCase: CreatePregnantMotherVisitUseCase // FIXED: Injeksikan use case untuk menyimpan kunjungan
) : ViewModel() {

    // --- Data Pendaftaran Ibu Hamil Saat Ini (Form Data) ---
    // MutableLiveData untuk menyimpan dan memperbarui data ibu hamil di antara navigasi fragment
    private val _currentPregnantMother = MutableLiveData<PregnantMotherRegistrationData>()
    val currentPregnantMother: LiveData<PregnantMotherRegistrationData> = _currentPregnantMother

    // FIXED: MutableLiveData untuk menyimpan dan memperbarui data kunjungan ibu hamil
    private val _currentPregnantMotherVisit = MutableLiveData<PregnantMotherVisitData>()
    val currentPregnantMotherVisit: LiveData<PregnantMotherVisitData> = _currentPregnantMotherVisit

    // LiveData untuk hasil proses pendaftaran (Success, Error, Loading, Idle)
    private val _registrationResult = MutableLiveData<Resource<Unit>>() // Ini untuk hasil simpan data ibu hamil dari Fragment 1
    val registrationResult: LiveData<Resource<Unit>> = _registrationResult

    // FIXED: LiveData baru untuk hasil proses penyimpanan (Success, Error, Loading, Idle)
    private val _saveResult = MutableLiveData<Resource<Unit>>() // Untuk status penyimpanan data ibu hamil (dipicu dari Fragment 2)
    val saveResult: LiveData<Resource<Unit>> = _saveResult

    // FIXED: LiveData baru untuk hasil proses penyimpanan kunjungan (Success, Error, Loading, Idle)
    private val _saveVisitResult = MutableLiveData<Resource<Unit>>() // Untuk status penyimpanan data kunjungan (dipicu dari Fragment 2)
    val saveVisitResult: LiveData<Resource<Unit>> = _saveVisitResult


    // --- Data Lokasi (untuk AutoCompleteTextViews) ---
    // Menggunakan StateFlow internal dan mengeksposnya sebagai LiveData agar Fragment bisa observe
    private val _provinsis = MutableStateFlow<Resource<List<Provinsi>>>(Resource.Idle)
    val provinsis: LiveData<Resource<List<Provinsi>>> = _provinsis.asLiveData(viewModelScope.coroutineContext)

    private val _kabupatens = MutableStateFlow<Resource<List<Kabupaten>>>(Resource.Idle)
    val kabupatens: LiveData<Resource<List<Kabupaten>>> = _kabupatens.asLiveData(viewModelScope.coroutineContext)

    private val _kecamatans = MutableStateFlow<Resource<List<Kecamatan>>>(Resource.Idle)
    val kecamatans: LiveData<Resource<List<Kecamatan>>> = _kecamatans.asLiveData(viewModelScope.coroutineContext)

    private val _kelurahans = MutableStateFlow<Resource<List<Kelurahan>>>(Resource.Idle)
    val kelurahans: LiveData<Resource<List<Kelurahan>>> = _kelurahans.asLiveData(viewModelScope.coroutineContext)

    private val _rws = MutableStateFlow<Resource<List<Rw>>>(Resource.Idle)
    val rws: LiveData<Resource<List<Rw>>> = _rws.asLiveData(viewModelScope.coroutineContext)

    private val _rts = MutableStateFlow<Resource<List<Rt>>>(Resource.Idle)
    val rts: LiveData<Resource<List<Rt>>> = _rts.asLiveData(viewModelScope.coroutineContext)

    // FIXED: LiveData untuk opsi-opsi dynamic checkbox/dropdown di Fragment 2
    private val _diseaseHistoryOptions = MutableLiveData<List<String>>()
    val diseaseHistoryOptions: LiveData<List<String>> = _diseaseHistoryOptions

    private val _mainSourceOfDrinkingWaterOptions = MutableLiveData<List<String>>()
    val mainSourceOfDrinkingWaterOptions: LiveData<List<String>> = _mainSourceOfDrinkingWaterOptions

    private val _defecationFacilityOptions = MutableLiveData<List<String>>()
    val defecationFacilityOptions: LiveData<List<String>> = _defecationFacilityOptions

    private val _socialAssistanceFacilitationOptions = MutableLiveData<List<String>>()
    val socialAssistanceFacilitationOptions: LiveData<List<String>> = _socialAssistanceFacilitationOptions

    private val _initialUserData = MutableLiveData<UserSession?>()
    val initialUserData: LiveData<UserSession?> = _initialUserData

    // NEW: LiveData untuk detail lokasi pengguna yang akan digunakan untuk pre-fill
    private val _userLocationDetails = MutableLiveData<LocationDetails?>()
    val userLocationDetails: LiveData<LocationDetails?> = _userLocationDetails

    init {
        // Inisialisasi data ibu hamil kosong saat ViewModel pertama kali dibuat
        _currentPregnantMother.value = PregnantMotherRegistrationData()
        // FIXED: Inisialisasi data kunjungan kosong
        _currentPregnantMotherVisit.value = PregnantMotherVisitData()
        // Muat data provinsi saat ViewModel pertama kali dibuat
        getProvinsis()
        // Inisialisasi status pendaftaran sebagai Idle
        _registrationResult.value = Resource.Idle
        // FIXED: Inisialisasi status penyimpanan ke Idle
        _saveResult.value = Resource.Idle
        _saveVisitResult.value = Resource.Idle
        _initialUserData.value = sharedPrefsManager.getUserSession()

        // FIXED: Memuat semua data opsi dynamic checkbox/dropdown
        getDiseaseHistoryOptions()
        getMainSourceOfDrinkingWaterOptions()
        getDefecationFacilityOptions()
        getSocialAssistanceFacilitationOptions()
        loadUserLocationDetails() // <<< Panggil untuk memuat detail lokasi
    }

    // --- Fungsi Update Data Form ---
    /**
     * Memperbarui properti data ibu hamil saat ini.
     * Menggunakan copy() untuk membuat instance data class yang baru dengan perubahan.
     */
    fun updatePregnantMotherData(
        localId: Int? = null,
        registrationDate: String? = null,
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
        husbandName: String? = null,
        fullAddress: String? = null,
        syncStatus: SyncStatus? = null, // Gunakan SyncStatus dari domain.model
        createdAt: String? = null
    ) {
        // Dapatkan data saat ini, jika null, inisialisasi dengan objek kosong
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()

        // Buat instance baru dengan perubahan
        val updatedData = currentData.copy(
            localId = localId ?: currentData.localId,
            registrationDate = registrationDate ?: currentData.registrationDate,
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
            husbandName = husbandName ?: currentData.husbandName,
            fullAddress = fullAddress ?: currentData.fullAddress,
            syncStatus = syncStatus ?: currentData.syncStatus,
            createdAt = createdAt ?: currentData.createdAt
        )
        _currentPregnantMother.value = updatedData
    }

    // FIXED: Fungsi untuk memperbarui data kunjungan ibu hamil
    fun updatePregnantMotherVisitData(
        localVisitId: Int? = null,
        pregnantMotherLocalId: Int? = null,
        visitDate: String? = null,
        childNumber: Int? = null,
        dateOfBirthLastChild: String? = null,
        pregnancyWeekAge: Int? = null,
        weightTrimester1: Double? = null,
        currentHeight: Double? = null,
        currentWeight: Double? = null,
        isHbChecked: Boolean? = null,
        hemoglobinLevel: Double? = null,
        upperArmCircumference: Double? = null,
        isTwin: Boolean? = null,
        numberOfTwins: Int? = null,
        isEstimatedFetalWeightChecked: Boolean? = null,
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
        diseaseHistory: List<String>? = null,
        mainSourceOfDrinkingWater: List<String>? = null,
        defecationFacility: List<String>? = null,
        socialAssistanceFacilitationOptions: List<String>? = null,
        syncStatus: SyncStatus? = null,
        createdAt: String? = null
    ) {
        val currentData = _currentPregnantMotherVisit.value ?: PregnantMotherVisitData()
        val updatedData = currentData.copy(
            localVisitId = localVisitId ?: currentData.localVisitId,
            pregnantMotherLocalId = pregnantMotherLocalId ?: currentData.pregnantMotherLocalId,
            visitDate = visitDate ?: currentData.visitDate,
            childNumber = childNumber ?: currentData.childNumber,
            dateOfBirthLastChild = dateOfBirthLastChild ?: currentData.dateOfBirthLastChild,
            pregnancyWeekAge = pregnancyWeekAge ?: currentData.pregnancyWeekAge,
            weightTrimester1 = weightTrimester1 ?: currentData.weightTrimester1,
            currentHeight = currentHeight ?: currentData.currentHeight,
            currentWeight = currentWeight ?: currentData.currentWeight,
            isHbChecked = isHbChecked ?: currentData.isHbChecked,
            hemoglobinLevel = hemoglobinLevel ?: currentData.hemoglobinLevel,
            upperArmCircumference = upperArmCircumference ?: currentData.upperArmCircumference,
            isTwin = isTwin ?: currentData.isTwin,
            numberOfTwins = numberOfTwins ?: currentData.numberOfTwins,
            isEstimatedFetalWeightChecked = isEstimatedFetalWeightChecked ?: currentData.isEstimatedFetalWeightChecked,
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
            diseaseHistory = diseaseHistory ?: currentData.diseaseHistory,
            mainSourceOfDrinkingWater = mainSourceOfDrinkingWater ?: currentData.mainSourceOfDrinkingWater,
            defecationFacility = defecationFacility ?: currentData.defecationFacility,
            socialAssistanceFacilitationOptions = socialAssistanceFacilitationOptions ?: currentData.socialAssistanceFacilitationOptions,
            syncStatus = syncStatus ?: currentData.syncStatus,
            createdAt = createdAt ?: currentData.createdAt
        )
        _currentPregnantMotherVisit.value = updatedData
    }

    // --- Fungsi Pemuatan Data Lokasi (dari LookupRepository) ---

    // Memuat daftar provinsi dari repository
    fun getProvinsis() {
        viewModelScope.launch {
            lookupRepository.getAllProvinsisFromRoom() // Mengembalikan Flow<List<Provinsi>>
                .onStart { _provinsis.value = Resource.Loading() } // Emit Loading
                .map { list -> Resource.Success(list) } // Map data ke Resource.Success
                .catch { e -> _provinsis.value = Resource.Error(e.localizedMessage ?: "Unknown error") } // Tangkap error
                .collect { resource -> _provinsis.value = resource } // Kumpulkan hasil ke StateFlow
        }
    }

    // Memuat daftar kabupaten berdasarkan ID provinsi yang dipilih
    fun getKabupatens(provinsiId: Int) {
        viewModelScope.launch {
            lookupRepository.getAllKabupatensFromRoom() // Mengembalikan Flow<List<Kabupaten>>
                .onStart { _kabupatens.value = Resource.Loading() }
                .map { list ->
                    // Filter berdasarkan provinsiId yang dipilih
                    Resource.Success(list.filter { it.provinsiId == provinsiId })
                }
                .catch { e -> _kabupatens.value = Resource.Error(e.localizedMessage ?: "Unknown error") }
                .collect { resource -> _kabupatens.value = resource }
        }
    }

    // Memuat daftar kecamatan berdasarkan ID kabupaten yang dipilih
    fun getKecamatans(kabupatenId: Int) {
        viewModelScope.launch {
            lookupRepository.getKecamatansByKabupatenFromRoom(kabupatenId) // Mengembalikan Flow<List<Kecamatan>>
                .onStart { _kecamatans.value = Resource.Loading() }
                .map { list -> Resource.Success(list) }
                .catch { e -> _kecamatans.value = Resource.Error(e.localizedMessage ?: "Unknown error") }
                .collect { resource -> _kecamatans.value = resource }
        }
    }

    // Memuat daftar kelurahan berdasarkan ID kecamatan yang dipilih
    fun getKelurahans(kecamatanId: Int) {
        viewModelScope.launch {
            lookupRepository.getKelurahansByKecamatanFromRoom(kecamatanId) // Mengembalikan Flow<List<Kelurahan>>
                .onStart { _kelurahans.value = Resource.Loading() }
                .map { list -> Resource.Success(list) }
                .catch { e -> _kelurahans.value = Resource.Error(e.localizedMessage ?: "Unknown error") }
                .collect { resource -> _kelurahans.value = resource }
        }
    }

    // Memuat daftar RW berdasarkan ID kelurahan yang dipilih
    fun getRWS(kelurahanId: Int) {
        viewModelScope.launch {
            lookupRepository.getRWSByKelurahanFromRoom(kelurahanId) // Mengembalikan Flow<List<Rw>>
                .onStart { _rws.value = Resource.Loading() }
                .map { list -> Resource.Success(list) }
                .catch { e -> _rws.value = Resource.Error(e.localizedMessage ?: "Unknown error") }
                .collect { resource -> _rws.value = resource }
        }
    }

    // Memuat daftar RT berdasarkan ID RW yang dipilih
    fun getRTS(rwId: Int) {
        viewModelScope.launch {
            lookupRepository.getRTSByRwFromRoom(rwId) // Mengembalikan Flow<List<Rt>>
                .onStart { _rts.value = Resource.Loading() }
                .map { list -> Resource.Success(list) }
                .catch { e -> _rts.value = Resource.Error(e.localizedMessage ?: "Unknown error") }
                .collect { resource -> _rts.value = resource }
        }
    }

    // FIXED: Fungsi untuk memuat opsi dynamic checkbox/dropdown dari LookupRepository
    fun getDiseaseHistoryOptions() {
        viewModelScope.launch {
            lookupRepository.getDiseaseHistoryOptions()
                .collect { options -> _diseaseHistoryOptions.postValue(options) }
        }
    }

    fun getMainSourceOfDrinkingWaterOptions() {
        viewModelScope.launch {
            lookupRepository.getMainSourceOfDrinkingWaterOptions()
                .collect { options -> _mainSourceOfDrinkingWaterOptions.postValue(options) }
        }
    }

    fun getDefecationFacilityOptions() {
        viewModelScope.launch {
            lookupRepository.getDefecationFacilityOptions()
                .collect { options -> _defecationFacilityOptions.postValue(options) }
        }
    }

    fun getSocialAssistanceFacilitationOptions() {
        viewModelScope.launch {
            lookupRepository.getSocialAssistanceFacilitationOptions()
                .collect { options -> _socialAssistanceFacilitationOptions.postValue(options) }
        }
    }

    // --- Fungsi Pendaftaran Ibu Hamil (melalui Use Case) ---
    // Fungsi ini dipanggil dari Fragment 1, hanya untuk menyimpan data ibu hamil.
    fun registerPregnantMother() {
        val data = _currentPregnantMother.value
        if (data == null) {
            _registrationResult.value = Resource.Error("Data ibu hamil tidak lengkap.")
            return
        }

        _registrationResult.value = Resource.Loading()
        viewModelScope.launch {
            // Note: createPregnantMotherUseCase.execute mengembalikan Resource<Long> (ID ibu hamil yang baru)
            // FIXED: Memetakan Resource<Long> ke Resource<Unit>
            val result = createPregnantMotherUseCase.execute(data)
            _registrationResult.postValue(result.mapToUnit())
        }
    }

    // FIXED: Fungsi untuk menyimpan Ibu Hamil dan Kunjungan (dipanggil dari Fragment 2)
    fun savePregnantMother() {
        _saveResult.value = Resource.Loading() // Mulai proses penyimpanan ibu hamil

        viewModelScope.launch {
            val motherData = _currentPregnantMother.value
            val visitData = _currentPregnantMotherVisit.value

            if (motherData == null) {
                _saveResult.postValue(Resource.Error("Data ibu hamil tidak ditemukan."))
                return@launch
            }
            if (visitData == null) {
                _saveResult.postValue(Resource.Error("Data kunjungan ibu hamil tidak ditemukan."))
                return@launch
            }

            // Langkah 1: Simpan data Ibu Hamil
            when (val motherSaveResult = createPregnantMotherUseCase.execute(motherData)) {
                is Resource.Success -> {
                    val motherLocalId = motherSaveResult.data?.toInt() // Dapatkan ID lokal ibu yang baru disimpan
                    if (motherLocalId != null) {
                        // Langkah 2: Perbarui data kunjungan dengan ID ibu hamil lokal
                        val updatedVisitData = visitData.copy(pregnantMotherLocalId = motherLocalId)
                        _currentPregnantMotherVisit.value = updatedVisitData // Perbarui ViewModel LiveData kunjungan

                        _saveResult.postValue(Resource.Success(Unit)) // Ibu hamil berhasil disimpan (untuk Fragment 2)

                        // Langkah 3: Simpan data Kunjungan
                        _saveVisitResult.value = Resource.Loading() // Mulai loading untuk penyimpanan kunjungan
                        when (val visitSaveResult = createPregnantMotherVisitUseCase.execute(updatedVisitData)) {
                            is Resource.Success -> {
                                _saveVisitResult.postValue(Resource.Success(Unit)) // Kunjungan berhasil disimpan
                                // Reset data form setelah sukses simpan
                                resetAllFormData()
                            }
                            is Resource.Error -> {
                                _saveVisitResult.postValue(Resource.Error(visitSaveResult.message ?: "Gagal menyimpan data kunjungan."))
                            }
                            is Resource.Loading -> { /* Not handled here */ }
                            Resource.Idle -> { /* Not handled here */ }
                        }
                    } else {
                        _saveResult.postValue(Resource.Error("Gagal mendapatkan ID lokal ibu hamil setelah disimpan."))
                    }
                }
                is Resource.Error -> {
                    _saveResult.postValue(Resource.Error(motherSaveResult.message ?: "Gagal menyimpan data ibu hamil."))
                }
                is Resource.Loading -> { /* Not handled here */ }
                Resource.Idle -> { /* Not handled here */ }
            }
        }
    }

    // --- Fungsi Reset Status ---
    /**
     * Mereset status hasil pendaftaran dari Fragment 1 ke Idle.
     */
    fun resetRegistrationResult() {
        _registrationResult.value = Resource.Idle
    }

    // FIXED: Mereset status hasil penyimpanan dari Fragment 2 ke Idle
    fun resetSaveResult() {
        _saveResult.value = Resource.Idle
    }

    // FIXED: Mereset status hasil penyimpanan kunjungan dari Fragment 2 ke Idle
    fun resetSaveVisitResult() {
        _saveVisitResult.value = Resource.Idle
    }

    // FIXED: Mereset semua data form di ViewModel (untuk memulai pendaftaran baru)
    fun resetAllFormData() {
        _currentPregnantMother.value = PregnantMotherRegistrationData()
        _currentPregnantMotherVisit.value = PregnantMotherVisitData()
        resetRegistrationResult()
        resetSaveResult()
        resetSaveVisitResult()
    }

    // NEW: Fungsi untuk memuat detail lokasi berdasarkan kelurahanId pengguna
    private fun loadUserLocationDetails() {
        viewModelScope.launch {
            val kelurahanId = sharedPrefsManager.getUserSession()?.kelurahanId
            if (kelurahanId != null) {
                val locationDetails = lookupRepository.getLocationDetailsByKelurahanId(kelurahanId)
                _userLocationDetails.postValue(locationDetails)
            } else {
                _userLocationDetails.postValue(null) // Tidak ada kelurahanId atau kelurahan tidak ditemukan
            }
        }
    }
}

// Extension function untuk mengkonversi StateFlow ke LiveData
// Ini diperlukan karena Anda menggunakan .asLiveData() di ViewModel.
// Jika sudah ada di proyek Anda (misalnya dari library lifecycle-livedata-ktx), Anda bisa menghapus ini.
//fun <T> StateFlow<T>.asLiveData(coroutineContext: kotlinx.coroutines.CoroutineContext) =
//    androidx.lifecycle.asLiveData(coroutineContext)

// Helper extension function to map Resource<Long> to Resource<Unit>
// FIXED: Ditambahkan untuk menangani pemetaan Resource<Long> ke Resource<Unit>
fun Resource<Long>.mapToUnit(): Resource<Unit> {
    return when (this) {
        is Resource.Success -> Resource.Success(Unit)
        is Resource.Error -> Resource.Error(this.message ?: "Unknown error", Unit)
        is Resource.Loading -> Resource.Loading(Unit)
        Resource.Idle -> Resource.Idle
    }
}
