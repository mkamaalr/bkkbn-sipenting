package com.bkkbnjabar.sipenting.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val lookupRepository: LookupRepository
) : ViewModel() {

    // NEW: LiveData untuk status pemuatan data lookup
    private val _lookupDataLoadResult = MutableLiveData<Resource<Unit>>() // Unit karena kita hanya peduli status sukses/error
    val lookupDataLoadResult: LiveData<Resource<Unit>> = _lookupDataLoadResult

    init {
        Log.d("SplashViewModel", "SplashViewModel initialized. Starting initial data load.")
        _lookupDataLoadResult.value = Resource.Idle // Inisialisasi awal
        loadInitialLookupData()
    }

    // NEW: Fungsi untuk memuat data lookup awal
    fun loadInitialLookupData() {
        _lookupDataLoadResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                // Panggil repository untuk memuat data lookup
                // Asumsi lookupRepository memiliki fungsi untuk memuat/menyinkronkan data
                val result = lookupRepository.preloadAllLocationAndLookupData() // Contoh: fungsi ini memuat dari API dan menyimpan ke Room
                if (result is Resource.Success) {
                    _lookupDataLoadResult.postValue(Resource.Success(Unit))
                } else if (result is Resource.Error) {
                    _lookupDataLoadResult.postValue(Resource.Error(result.message ?: "Unknown error loading lookup data"))
                }
            } catch (e: Exception) {
                _lookupDataLoadResult.postValue(Resource.Error("Exception loading lookup data: ${e.localizedMessage}"))
            }
        }
    }
}
