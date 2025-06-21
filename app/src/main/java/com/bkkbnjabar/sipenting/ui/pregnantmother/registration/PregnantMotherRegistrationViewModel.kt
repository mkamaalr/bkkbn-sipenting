package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.data.model.Kelurahan
import com.bkkbnjabar.sipenting.data.model.Provinsi
import com.bkkbnjabar.sipenting.data.model.Kabupaten
import com.bkkbnjabar.sipenting.data.model.Rt
import com.bkkbnjabar.sipenting.data.model.Rw
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PregnantMotherRegistrationViewModel @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository,
    private val lookupRepository: LookupRepository,
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val _currentPregnantMother = MutableLiveData<PregnantMotherRegistrationData>()
    val currentPregnantMother: LiveData<PregnantMotherRegistrationData> = _currentPregnantMother

    private var initialUserLocation: Kelurahan? = null

    private val _provinsis = MutableLiveData<Resource<List<Provinsi>>>()
    val provinsis: LiveData<Resource<List<Provinsi>>> = _provinsis

    private val _kabupatens = MutableLiveData<Resource<List<Kabupaten>>>()
    val kabupatens: LiveData<Resource<List<Kabupaten>>> = _kabupatens

    private val _kecamatans = MutableLiveData<Resource<List<Kecamatan>>>()
    val kecamatans: LiveData<Resource<List<Kecamatan>>> = _kecamatans

    private val _kelurahans = MutableLiveData<Resource<List<Kelurahan>>>()
    val kelurahans: LiveData<Resource<List<Kelurahan>>> = _kelurahans

    private val _rws = MutableLiveData<Resource<List<Rw>>>()
    val rws: LiveData<Resource<List<Rw>>> = _rws

    private val _rts = MutableLiveData<Resource<List<Rt>>>()
    val rts: LiveData<Resource<List<Rt>>> = _rts

    private val _saveResult = MutableLiveData<Resource<String>>()
    val saveResult: LiveData<Resource<String>> = _saveResult

    init {
        Log.d("PMR_VM_LIFECYCLE", "ViewModel init block called.")
        loadInitialUserLocationAndStartNewForm()
        observeLookupDataFromRoom()
    }

    private fun loadInitialUserLocationAndStartNewForm() {
        viewModelScope.launch {
            Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: Attempting to load location.")
            val cachedLocation = sharedPrefsManager.getUserLocation()
            initialUserLocation = if (cachedLocation != null) {
                Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: Found cached location: ${cachedLocation.name}")
                cachedLocation
            } else {
                Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: No cached location, fetching from repository.")
                when (val result = lookupRepository.getUserLocationDataFromApi()) {
                    is Resource.Success -> {
                        result.data?.also {
                            sharedPrefsManager.saveUserLocation(it)
                            Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: Fetched and saved new location: ${it.name}")
                        }
                    }
                    is Resource.Error -> {
                        Log.e("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: Error fetching location: ${result.message}")
                        null
                    }
                    is Resource.Loading -> {
                        Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: Location data still loading.")
                        null
                    }
                }
            }
            startNewRegistration(fromInit = true) // Panggil dengan flag dari init
            Log.d("PMR_VM_LIFECYCLE", "loadInitialUserLocationAndStartNewForm: startNewRegistration() called.")
        }
    }

    private fun observeLookupDataFromRoom() {
        viewModelScope.launch {
            lookupRepository.getAllProvinsisFromRoom().collectLatest {
                _provinsis.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "Provinsis updated: ${it.size} items")
            }
        }
        viewModelScope.launch {
            lookupRepository.getAllKabupatensFromRoom().collectLatest {
                _kabupatens.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "Kabupatens updated: ${it.size} items")
            }
        }
        viewModelScope.launch {
            lookupRepository.getAllKecamatansFromRoom().collectLatest {
                _kecamatans.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "Kecamatans updated: ${it.size} items")
            }
        }
    }

    fun getKelurahans(kecamatanId: Int) {
        Log.d("PMR_VM_LOOKUP", "getKelurahans for kecamatanId: $kecamatanId")
        viewModelScope.launch {
            _kelurahans.postValue(Resource.Loading())
            lookupRepository.getKelurahansByKecamatanFromRoom(kecamatanId).collectLatest {
                _kelurahans.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "Kelurahans updated for $kecamatanId: ${it.size} items")
            }
        }
    }

    fun getRWS(kelurahanId: Int) {
        Log.d("PMR_VM_LOOKUP", "getRWS for kelurahanId: $kelurahanId")
        viewModelScope.launch {
            _rws.postValue(Resource.Loading())
            lookupRepository.getRWSByKelurahanFromRoom(kelurahanId).collectLatest {
                _rws.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "RWs updated for $kelurahanId: ${it.size} items")
            }
        }
    }

    fun getRTS(rwId: Int) {
        Log.d("PMR_VM_LOOKUP", "getRTS for rwId: $rwId")
        viewModelScope.launch {
            _rts.postValue(Resource.Loading())
            lookupRepository.getRTSByRwFromRoom(rwId).collectLatest {
                _rts.postValue(Resource.Success(it))
                Log.d("PMR_VM_LOOKUP", "RTs updated for $rwId: ${it.size} items")
            }
        }
    }

    /**
     * Memperbarui bagian pertama data ibu hamil dalam ViewModel.
     */
    fun updatePregnantMotherPart1(
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
        rtId: Int? = null
    ) {
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()

        Log.d("PMR_VM_UPDATE", "updatePregnantMotherPart1 called. Current data (before update): Name=${currentData.name}, NIK=${currentData.nik}, RW=${currentData.rwName}, RT=${currentData.rtName}, LocalId=${currentData.localId}")

        val updatedData = currentData.copy(
            name = name ?: currentData.name,
            nik = nik ?: currentData.nik,
            dateOfBirth = dateOfBirth ?: currentData.dateOfBirth,
            phoneNumber = phoneNumber ?: currentData.phoneNumber,
            provinsiId = provinsiId ?: currentData.provinsiId,
            provinsiName = provinsiName ?: currentData.provinsiName,
            kabupatenId = kabupatenId ?: currentData.kabupatenId,
            kabupatenName = kabupatenName ?: currentData.kabupatenName,
            kecamatanId = kecamatanId ?: currentData.kecamatanId,
            kecamatanName = kecamatanName ?: currentData.kecamatanName,
            kelurahanId = kelurahanId ?: currentData.kelurahanId,
            kelurahanName = kelurahanName ?: currentData.kelurahanName,
            rwId = rwId ?: currentData.rwId,
            rwName = rwName ?: currentData.rwName,
            rtId = rtId ?: currentData.rtId,
            rtName = rtName ?: currentData.rtName
        )
        _currentPregnantMother.value = updatedData
        Log.d("PMR_VM_UPDATE", "updatePregnantMotherPart1: _currentPregnantMother value updated to: Name=${updatedData.name}, NIK=${updatedData.nik}, RW=${updatedData.rwName}, RT=${updatedData.rtName}, LocalId=${updatedData.localId}")
    }

    /**
     * Memperbarui bagian kedua data ibu hamil dalam ViewModel.
     */
    fun updatePregnantMotherPart2(
        husbandName: String? = null,
        fullAddress: String? = null
    ) {
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()
        Log.d("PMR_VM_UPDATE", "updatePregnantMotherPart2 called. Current data (before update): Husband=${currentData.husbandName}, Address=${currentData.fullAddress}, LocalId=${currentData.localId}")
        val updatedData = currentData.copy(
            husbandName = husbandName ?: currentData.husbandName,
            fullAddress = fullAddress ?: currentData.fullAddress
        )
        _currentPregnantMother.value = updatedData
        Log.d("PMR_VM_UPDATE", "updatePregnantMotherPart2: _currentPregnantMother value updated to: Husband=${updatedData.husbandName}, Address=${updatedData.fullAddress}, LocalId=${updatedData.localId}")
    }

    /**
     * Menyimpan data ibu hamil yang ada di `_currentPregnantMother` ke database lokal.
     */
    fun savePregnantMother() {
        Log.d("PMR_VM_SAVE", "savePregnantMother: Attempting to save...")
        _saveResult.value = Resource.Loading()
        viewModelScope.launch {
            val dataToSave = _currentPregnantMother.value
            if (dataToSave == null) {
                _saveResult.postValue(Resource.Error("Data ibu hamil tidak lengkap."))
                Log.e("PMR_VM_SAVE", "savePregnantMother: Data to save is null.")
                return@launch
            }

            Log.d("PMR_VM_SAVE", "savePregnantMother: Saving data with Name=${dataToSave.name}, NIK=${dataToSave.nik}, LocalId=${dataToSave.localId}")
            val result = pregnantMotherRepository.createPregnantMother(dataToSave)
            _saveResult.postValue(result)
            if (result is Resource.Success) {
                Log.d("PMR_VM_SAVE", "savePregnantMother: Successfully saved/updated. Result: ${result.data}. Calling startNewRegistration() to clear form.")
                startNewRegistration() // Ini yang akan membersihkan form setelah sukses
            } else if (result is Resource.Error) {
                Log.e("PMR_VM_SAVE", "savePregnantMother: Failed to save: ${result.message}")
            }
        }
    }

    /**
     * Memuat data ibu hamil untuk tujuan edit berdasarkan ID lokal.
     */
    fun loadPregnantMotherForEdit(localId: Int) {
        Log.d("PMR_VM_EDIT", "loadPregnantMotherForEdit: Loading for localId: $localId")
        viewModelScope.launch {
            // Bersihkan form sepenuhnya sebelum memuat data edit
            _currentPregnantMother.value = PregnantMotherRegistrationData(
                registrationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                syncStatus = SyncStatus.PENDING_UPLOAD
            )
            Log.d("PMR_VM_EDIT", "loadPregnantMotherForEdit: _currentPregnantMother cleared before loading edit data. LocalId=${_currentPregnantMother.value?.localId}")

            val result = pregnantMotherRepository.getPregnantMotherById(localId)
            when (result) {
                is Resource.Success -> {
                    _currentPregnantMother.postValue(result.data!!)
                    Log.d("PMR_VM_EDIT", "loadPregnantMotherForEdit: Loaded data: Name=${result.data.name}, NIK=${result.data.nik}, LocalId=${result.data.localId}")
                    result.data.kecamatanId?.let { getKelurahans(it) }
                    result.data.kelurahanId?.let { getRWS(it) }
                    result.data.rwId?.let { getRTS(it) }
                }
                is Resource.Error -> {
                    _saveResult.postValue(Resource.Error("Gagal memuat data ibu hamil untuk diedit: ${result.message}"))
                    Log.e("PMR_VM_EDIT", "loadPregnantMotherForEdit: Error loading data: ${result.message}")
                }
                is Resource.Loading -> { /* Not handled here */ }
            }
        }
    }

    /**
     * Mengatur ViewModel untuk memulai pendaftaran ibu hamil yang baru.
     */
    fun startNewRegistration(fromInit: Boolean = false) {
        Log.d("PMR_VM_LIFECYCLE", "startNewRegistration: called (fromInit=$fromInit).")

        // Buat instance baru PregnantMotherRegistrationData yang bersih, dengan localId = null
        val newFormData = PregnantMotherRegistrationData(
            localId = null, // Secara eksplisit set ke null untuk memastikan ini adalah record baru
            registrationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            syncStatus = SyncStatus.PENDING_UPLOAD,
            createdAt = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )

        initialUserLocation?.let { kelurahan ->
            val dataWithLocation = newFormData.copy(
                provinsiName = kelurahan.kecamatan?.kabupaten?.provinsi?.name,
                provinsiId = kelurahan.kecamatan?.kabupaten?.provinsi?.id,
                kabupatenName = kelurahan.kecamatan?.kabupaten?.name,
                kabupatenId = kelurahan.kecamatan?.kabupaten?.id,
                kecamatanName = kelurahan.kecamatan?.name,
                kecamatanId = kelurahan.kecamatan?.id,
                kelurahanName = kelurahan.name,
                kelurahanId = kelurahan.id
            )
            _currentPregnantMother.value = dataWithLocation
            Log.d("PMR_VM_LIFECYCLE", "startNewRegistration: Form initialized with location: ${kelurahan.name}, LocalId=${dataWithLocation.localId}")
            if (!fromInit) {
                kelurahan.id?.let { getRWS(it) }
                Log.d("PMR_VM_LIFECYCLE", "startNewRegistration: getRWS triggered after manual new registration.")
            }
        } ?: run {
            _currentPregnantMother.value = newFormData
            Log.d("PMR_VM_LIFECYCLE", "startNewRegistration: Form initialized as blank (location not yet available), LocalId=${newFormData.localId}.")
        }
        Log.d("PMR_VM_LIFECYCLE", "startNewRegistration: _currentPregnantMother final value set to: Name=${_currentPregnantMother.value?.name}, NIK=${_currentPregnantMother.value?.nik}, RW=${_currentPregnantMother.value?.rwName}, RT=${_currentPregnantMother.value?.rtName}, LocalId=${_currentPregnantMother.value?.localId}, CreatedAt=${_currentPregnantMother.value?.createdAt}")
    }

    /**
     * Mereset LiveData saveResult untuk mencegah pemicuan ulang event.
     * Ini harus dipanggil setelah event berhasil ditangani (misalnya, setelah navigasi).
     */
    fun resetSaveResult() {
        _saveResult.value = null // Mengatur ke null akan mencegah re-trigger
        Log.d("PMR_VM_LIFECYCLE", "resetSaveResult() called. _saveResult set to null.")
    }
}
