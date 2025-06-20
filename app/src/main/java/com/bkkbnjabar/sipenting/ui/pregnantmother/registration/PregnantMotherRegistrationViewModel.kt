package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

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
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus // Import SyncStatus
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest // Import collectLatest
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

    // --- State Formulir Ibu Hamil ---
    // Inisialisasi dengan data kosong dan tanggal pendaftaran hari ini
    private val _currentPregnantMother = MutableLiveData<PregnantMotherRegistrationData>(
        PregnantMotherRegistrationData(
            registrationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            syncStatus = SyncStatus.PENDING_UPLOAD // Default status untuk data baru
        )
    )
    val currentPregnantMother: LiveData<PregnantMotherRegistrationData> = _currentPregnantMother

    // --- Data Lookup Dinamis dari Room ---
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

    // --- Hasil Operasi Penyimpanan ---
    private val _saveResult = MutableLiveData<Resource<String>>()
    val saveResult: LiveData<Resource<String>> = _saveResult

    init {
        // Muat data lokasi pengguna dari SharedPrefs saat ViewModel diinisialisasi
        // dan muat data lookup dari Room
        loadInitialData()
        observeLookupDataFromRoom()
    }

    /**
     * Memuat data lokasi pengguna awal (kecamatan, kabupaten, provinsi) dari SharedPrefs.
     * Data ini diharapkan sudah di-preload saat SplashActivity.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            // Pertama, coba ambil dari SharedPrefs
            val cachedLocation = sharedPrefsManager.getUserLocation()
            if (cachedLocation != null) {
                // Jika ada di cache, gunakan itu (cachedLocation adalah objek Kelurahan)
                _currentPregnantMother.value = _currentPregnantMother.value?.copy(
                    provinsiName = cachedLocation.kecamatan?.kabupaten?.provinsi?.name,
                    provinsiId = cachedLocation.kecamatan?.kabupaten?.provinsi?.id,
                    kabupatenName = cachedLocation.kecamatan?.kabupaten?.name,
                    kabupatenId = cachedLocation.kecamatan?.kabupaten?.id,
                    kecamatanName = cachedLocation.kecamatan?.name,
                    kecamatanId = cachedLocation.kecamatan?.id,
                    kelurahanName = cachedLocation.name,
                    kelurahanId = cachedLocation.id,
                    // RW dan RT akan kosong secara default, dan akan dimuat saat dropdown kelurahan dipilih
                    rwName = null,
                    rwId = null,
                    rtName = null,
                    rtId = null
                )
                // Trigger loading RW untuk kelurahan user setelah kelurahan dimuat dari cache
                cachedLocation.id?.let { kelurahanId ->
                    getRWS(kelurahanId)
                }
            } else {
                // Jika tidak ada di cache, baru ambil dari API
                when (val result = lookupRepository.getUserLocationDataFromApi()) { // Menggunakan getUserLocationData() yang baru mengembalikan Kelurahan
                    is Resource.Success -> {
                        result.data?.let { userKelurahan -> // userKelurahan adalah objek Kelurahan
                            _currentPregnantMother.value = _currentPregnantMother.value?.copy(
                                provinsiName = userKelurahan.kecamatan?.kabupaten?.provinsi?.name,
                                provinsiId = userKelurahan.kecamatan?.kabupaten?.provinsi?.id,
                                kabupatenName = userKelurahan.kecamatan?.kabupaten?.name,
                                kabupatenId = userKelurahan.kecamatan?.kabupaten?.id,
                                kecamatanName = userKelurahan.kecamatan?.name,
                                kecamatanId = userKelurahan.kecamatan?.id,
                                kelurahanName = userKelurahan.name,
                                kelurahanId = userKelurahan.id,
                                // RW dan RT akan kosong secara default, dan akan dimuat saat dropdown kelurahan dipilih
                                rwName = null,
                                rwId = null,
                                rtName = null,
                                rtId = null
                            )
                            // Simpan ke SharedPrefs untuk penggunaan selanjutnya
                            sharedPrefsManager.saveUserLocation(userKelurahan)
                            // Trigger loading RW untuk kelurahan user setelah kelurahan dimuat
                            userKelurahan.id?.let { kelurahanId ->
                                getRWS(kelurahanId)
                            }
                        } // ?: Log.e("PMRViewModel", "User location data is null from API success.") // Log jika data API null
                    }
                    is Resource.Error -> {
                        // Log.e("PMRViewModel", "Error loading user location from API: ${result.message}") // Log error
                        // Pertimbangkan untuk menampilkan pesan error ke user atau mencoba lagi
                    }
                    is Resource.Loading -> {
                        // Handle loading state, mungkin tampilkan indikator loading di UI
                    }
                }
            }
        }
    }

    /**
     * Mengamati data lookup (Provinsi, Kabupaten, Kecamatan) dari Room database.
     * Ini akan secara otomatis memperbarui LiveData saat ada perubahan di DB.
     */
    private fun observeLookupDataFromRoom() {
        viewModelScope.launch {
            lookupRepository.getAllProvinsisFromRoom().collectLatest {
                _provinsis.postValue(Resource.Success(it))
            }
        }
        viewModelScope.launch {
            lookupRepository.getAllKabupatensFromRoom().collectLatest {
                _kabupatens.postValue(Resource.Success(it))
            }
        }
        viewModelScope.launch {
            lookupRepository.getAllKecamatansFromRoom().collectLatest {
                _kecamatans.postValue(Resource.Success(it))
            }
        }
        // Kelurahan, RW, RT akan dimuat berdasarkan ID induknya (dari pilihan user atau data awal)
        // Kita tidak perlu memuat SEMUA kelurahan/rw/rt dari Room di init, hanya yang relevan.
        // Fungsi `getKelurahans`, `getRWS`, `getRTS` di bawah akan menggunakan Room jika ada.
    }

    // Fungsi-fungsi untuk mengambil data lookup dari Room (berdasarkan filter)
    fun getKelurahans(kecamatanId: Int) {
        viewModelScope.launch {
            _kelurahans.postValue(Resource.Loading())
            lookupRepository.getKelurahansByKecamatanFromRoom(kecamatanId).collectLatest {
                _kelurahans.postValue(Resource.Success(it))
            }
        }
    }

    fun getRWS(kelurahanId: Int) {
        viewModelScope.launch {
            _rws.postValue(Resource.Loading())
            lookupRepository.getRWSByKelurahanFromRoom(kelurahanId).collectLatest {
                _rws.postValue(Resource.Success(it))
            }
        }
    }

    fun getRTS(rwId: Int) {
        viewModelScope.launch {
            _rts.postValue(Resource.Loading())
            lookupRepository.getRTSByRwFromRoom(rwId).collectLatest {
                _rts.postValue(Resource.Success(it))
            }
        }
    }

    /**
     * Memperbarui bagian pertama data ibu hamil dalam ViewModel.
     * Ini tidak langsung menyimpan ke database, hanya memperbarui LiveData.
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
        // Ambil data saat ini dari LiveData
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()

        _currentPregnantMother.value = currentData.copy(
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
    }

    /**
     * Memperbarui bagian kedua data ibu hamil dalam ViewModel.
     * Ini tidak langsung menyimpan ke database, hanya memperbarui LiveData.
     */
    fun updatePregnantMotherPart2(
        husbandName: String? = null,
        fullAddress: String? = null
        // Tambahkan parameter lain dari Fragment 2 jika ada
    ) {
        val currentData = _currentPregnantMother.value ?: PregnantMotherRegistrationData()
        _currentPregnantMother.value = currentData.copy(
            husbandName = husbandName ?: currentData.husbandName,
            fullAddress = fullAddress ?: currentData.fullAddress
            // Perbarui properti lain di sini
        )
    }

    /**
     * Menyimpan data ibu hamil yang ada di `_currentPregnantMother` ke database lokal.
     * Ini memicu operasi penyimpanan di repository.
     */
    fun savePregnantMother() {
        _saveResult.value = Resource.Loading()
        viewModelScope.launch {
            val dataToSave = _currentPregnantMother.value
            if (dataToSave == null) {
                _saveResult.postValue(Resource.Error("Data ibu hamil tidak lengkap."))
                return@launch
            }

            // Memanggil createPregnantMother di repository yang akan menyimpan ke Room
            val result = pregnantMotherRepository.createPregnantMother(dataToSave)
            _saveResult.postValue(result)
            if (result is Resource.Success) {
                // Setelah berhasil disimpan lokal, reset form untuk pendaftaran baru
                resetRegistrationData()
            }
        }
    }

    /**
     * Memuat data ibu hamil untuk tujuan edit berdasarkan ID lokal.
     * Ini akan mengisi `_currentPregnantMother` dengan data yang ada.
     */
    fun loadPregnantMotherForEdit(localId: Int) {
        viewModelScope.launch {
            val result = pregnantMotherRepository.getPregnantMotherById(localId)
            when (result) {
                is Resource.Success -> {
                    _currentPregnantMother.postValue(result.data)
                    // Setelah data dimuat, picu pemuatan lookup dinamis jika ID tersedia
                    result.data?.kecamatanId?.let { getKelurahans(it) } // Muat kelurahan untuk kecamatan ini
                    result.data?.kelurahanId?.let { getRWS(it) } // Muat RW untuk kelurahan ini
                    result.data?.rwId?.let { getRTS(it) } // Muat RT untuk RW ini
                }
                is Resource.Error -> {
                    _saveResult.postValue(Resource.Error("Gagal memuat data ibu hamil untuk diedit: ${result.message}"))
                }
                is Resource.Loading -> {
                    // Not handled here
                }
            }
        }
    }

    /**
     * Mereset data form pendaftaran ibu hamil ke kondisi awal (kosong atau default).
     * Ini dipanggil setelah pendaftaran berhasil atau jika ingin memulai form baru.
     */
    fun resetRegistrationData() {
        _currentPregnantMother.value = PregnantMotherRegistrationData(
            registrationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        // Opsional: Muat ulang data lokasi awal jika diperlukan
        loadInitialData()
    }
}
