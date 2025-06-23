package com.bkkbnjabar.sipenting.ui.pregnantmother.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val lookupRepository: LookupRepository // Injeksi LookupRepository
) : ViewModel() {

    // --- LiveData untuk daftar lokasi yang dimuat ---
    private val _kelurahanList = MutableLiveData<Resource<List<Kelurahan>>>()
    val kelurahanList: LiveData<Resource<List<Kelurahan>>> = _kelurahanList

    private val _rwList = MutableLiveData<Resource<List<Rw>>>()
    val rwList: LiveData<Resource<List<Rw>>> = _rwList

    private val _rtList = MutableLiveData<Resource<List<Rt>>>()
    val rtList: LiveData<Resource<List<Rt>>> = _rtList

    // --- LiveData untuk objek lokasi yang sedang dipilih/ditampilkan ---
    private val _selectedKelurahan = MutableLiveData<Kelurahan?>()
    val selectedKelurahan: LiveData<Kelurahan?> = _selectedKelurahan

    private val _selectedRw = MutableLiveData<Rw?>()
    val selectedRw: LiveData<Rw?> = _selectedRw

    private val _selectedRt = MutableLiveData<Rt?>()
    val selectedRt: LiveData<Rt?> = _selectedRt

    // --- Fungsi untuk memuat daftar lokasi ---

    /**
     * Memuat daftar Kelurahan dari API berdasarkan ID Kecamatan.
     * Setelah berhasil memuat, akan mencoba mencari kelurahan yang dipilih sebelumnya (jika ada)
     * atau kelurahan pertama jika tidak ada yang dipilih.
     * @param kecamatanId ID Kecamatan untuk memfilter daftar Kelurahan. Jika null, akan memuat semua.
     */
    fun loadKelurahans(kecamatanId: Int?) {
        _kelurahanList.postValue(Resource.Loading()) // Set status loading
        viewModelScope.launch {
            val result = lookupRepository.getKelurahansFromApi(kecamatanId)
            _kelurahanList.postValue(result) // Update LiveData dengan hasil

            if (result is Resource.Success && result.data != null) {
                // Setelah daftar kelurahan dimuat, coba set selectedKelurahan
                // Jika sudah ada selectedKelurahan dari SharedPrefs, gunakan itu.
                // Jika tidak, bisa set yang pertama sebagai default atau biarkan null.
                val currentSelected = _selectedKelurahan.value
                if (currentSelected != null && result.data.any { it.id == currentSelected.id }) {
                    // Kelurahan yang dipilih sebelumnya masih ada di daftar baru
                    setSelectedKelurahan(currentSelected)
                } else if (result.data.isNotEmpty()) {
                    // Jika tidak ada yang dipilih sebelumnya atau tidak ada di daftar baru,
                    // bisa pilih yang pertama atau biarkan null sesuai kebutuhan UI.
                    // Untuk Fragment 1, kita ingin mencocokkan dengan SharedPrefs, jadi biarkan fragment yang set.
                    // Jika ini untuk dropdown yang bisa dipilih, mungkin set yang pertama.
                    // setSelectedKelurahan(result.data.first()) // Opsional: set yang pertama sebagai default
                } else {
                    setSelectedKelurahan(null)
                }
            } else if (result is Resource.Error) {
                setSelectedKelurahan(null)
            }
        }
    }

    /**
     * Memuat daftar RW dari API berdasarkan ID Kelurahan.
     * @param kelurahanId ID Kelurahan untuk memfilter daftar RW.
     */
    fun loadRWS(kelurahanId: Int?) {
        // Jika kelurahanId null, artinya tidak ada kelurahan yang dipilih,
        // jadi kosongkan daftar RW.
        if (kelurahanId == null) {
            _rwList.postValue(Resource.Success(emptyList()))
            setSelectedRw(null) // Reset selected RW
            return
        }

        _rwList.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = lookupRepository.getRWSFromApi(kelurahanId)
            _rwList.postValue(result)

            if (result is Resource.Success && result.data != null) {
                // Periksa apakah RW yang sudah dipilih ada di daftar baru. Jika tidak, reset.
                val currentSelected = _selectedRw.value
                if (currentSelected != null && !result.data.any { it.id == currentSelected.id }) {
                    setSelectedRw(null)
                }
                // Jika selectedRw sudah ada dan masih di daftar (dari loadFormData), ini akan tetap dipertahankan
                // jika tidak, biarkan fragment yang menyetelnya dari data lama (jika ada).
            } else if (result is Resource.Error) {
                setSelectedRw(null)
            }
        }
    }

    /**
     * Memuat daftar RT dari API berdasarkan ID RW.
     * @param rwId ID RW untuk memfilter daftar RT.
     */
    fun loadRTS(rwId: Int?) {
        // Jika rwId null, artinya tidak ada RW yang dipilih,
        // jadi kosongkan daftar RT.
        if (rwId == null) {
            _rtList.postValue(Resource.Success(emptyList()))
            setSelectedRt(null) // Reset selected RT
            return
        }

        _rtList.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = lookupRepository.getRTSFromApi(rwId)
            _rtList.postValue(result)

            if (result is Resource.Success && result.data != null) {
                // Periksa apakah RT yang sudah dipilih ada di daftar baru. Jika tidak, reset.
                val currentSelected = _selectedRt.value
                if (currentSelected != null && !result.data.any { it.id == currentSelected.id }) {
                    setSelectedRt(null)
                }
                // Jika selectedRt sudah ada dan masih di daftar (dari loadFormData), ini akan tetap dipertahankan.
            } else if (result is Resource.Error) {
                setSelectedRt(null)
            }
        }
    }


    // --- Fungsi untuk memperbarui objek yang sedang dipilih ---

    /**
     * Mengatur Kelurahan yang sedang dipilih.
     * Ketika Kelurahan diatur, secara otomatis akan memicu pemuatan daftar RW untuk Kelurahan tersebut.
     * @param kelurahan Objek Kelurahan yang dipilih.
     */
    fun setSelectedKelurahan(kelurahan: Kelurahan?) {
        _selectedKelurahan.value = kelurahan
        // Panggil loadRWS setiap kali selectedKelurahan berubah
        loadRWS(kelurahan?.id)
    }

    /**
     * Mengatur RW yang sedang dipilih.
     * Ketika RW diatur, secara otomatis akan memicu pemuatan daftar RT untuk RW tersebut.
     * @param rw Objek RW yang dipilih.
     */
    fun setSelectedRw(rw: Rw?) {
        _selectedRw.value = rw
        // Panggil loadRTS setiap kali selectedRw berubah
        loadRTS(rw?.id)
    }

    /**
     * Mengatur RT yang sedang dipilih.
     * @param rt Objek RT yang dipilih.
     */
    fun setSelectedRt(rt: Rt?) {
        _selectedRt.value = rt
    }
}
