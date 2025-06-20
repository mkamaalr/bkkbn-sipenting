package com.bkkbnjabar.sipenting.ui.pregnantmother

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PregnantMotherListViewModel @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository // Injeksi repository
) : ViewModel() {

    private val _pregnantMothersList = MutableLiveData<Resource<List<PregnantMotherRegistrationData>>>()
    val pregnantMothersList: LiveData<Resource<List<PregnantMotherRegistrationData>>> = _pregnantMothersList

    init {
        // Muat data saat ViewModel pertama kali dibuat
        loadPregnantMothers()
    }

    /**
     * Memuat daftar ibu hamil dari repository.
     * Mengamati perubahan data dari Room database.
     */
    fun loadPregnantMothers() {
        _pregnantMothersList.value = Resource.Loading() // Set status loading
        viewModelScope.launch {
            // Menggunakan collectLatest untuk membatalkan pengumpul sebelumnya jika ada yang baru dimulai
            // dan mengamati Flow dari repository
            pregnantMotherRepository.getAllPregnantMothers().collectLatest { resource ->
                _pregnantMothersList.postValue(resource)
            }
        }
    }

    /**
     * Memulai proses sinkronisasi/upload data ibu hamil yang tertunda ke server.
     */
    fun uploadPendingPregnantMothers() {
        viewModelScope.launch {
            _pregnantMothersList.postValue(Resource.Loading(_pregnantMothersList.value?.data)) // Pertahankan data lama saat loading
            val result = pregnantMotherRepository.uploadPendingPregnantMothers()
            // Setelah upload selesai, muat ulang daftar untuk mencerminkan status sinkronisasi terbaru
            loadPregnantMothers()
            // Anda mungkin ingin memberikan umpan balik (Toast) berdasarkan 'result' upload
            when (result) {
                is Resource.Success -> {
                    println("Upload successful: ${result.data}")
                }
                is Resource.Error -> {
                    println("Upload failed: ${result.message}")
                }
                is Resource.Loading -> { /* Tidak relevan di sini */ }
            }
        }
    }
}
