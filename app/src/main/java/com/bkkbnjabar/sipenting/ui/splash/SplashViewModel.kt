package com.bkkbnjabar.sipenting.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val lookupRepository: LookupRepository,
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    enum class NavigationDestination {
        LOGIN, MAIN
    }

    private val _navigateTo = MutableLiveData<NavigationDestination>()
    val navigateTo: LiveData<NavigationDestination> = _navigateTo

    fun checkAuthentication() {
        viewModelScope.launch {
            val isValid = validateTokenUseCase.execute()
            if (isValid) {
                // 1. Coba ambil dan simpan data lokasi pengguna (Kelurahan user)
                when (val userLocationResult = lookupRepository.getUserLocationDataFromApi()) {
                    is Resource.Success -> {
                        userLocationResult.data?.let { kelurahan ->
                            sharedPrefsManager.saveUserLocation(kelurahan)
                            println("User location data saved: ${kelurahan.name}")
                        } ?: run {
                            println("User location data is null.")
                        }
                    }
                    is Resource.Error -> {
                        println("Error fetching user location data: ${userLocationResult.message}")
                        // Lanjutkan meskipun ada error, tapi mungkin perlu ditangani di UI utama
                    }
                    is Resource.Loading -> { /* Tidak relevan di sini */ }
                }

                // 2. Preload semua data lokasi (Provinsi, Kabupaten, Kecamatan, Kelurahan, RW, RT)
                when (val preloadResult = lookupRepository.preloadAllLocationData()) {
                    is Resource.Success -> {
                        println("All location data preloaded successfully!")
                    }
                    is Resource.Error -> {
                        println("Failed to preload all location data: ${preloadResult.message}")
                        // Anda bisa menampilkan Toast atau log ini jika perlu
                    }
                    is Resource.Loading -> { /* Tidak relevan di sini */ }
                }

                _navigateTo.postValue(NavigationDestination.MAIN)
            } else {
                _navigateTo.postValue(NavigationDestination.LOGIN)
            }
        }
    }
}
