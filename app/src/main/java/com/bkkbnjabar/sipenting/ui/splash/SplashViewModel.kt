package com.bkkbnjabar.sipenting.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SplashActivity.
 * It handles initial data loading and session validation.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val lookupRepository: LookupRepository
) : ViewModel() {

    // LiveData untuk memberitahu Activity status preloading
    private val _isPreloadingComplete = MutableLiveData<Boolean>(false)
    val isPreloadingComplete: LiveData<Boolean> = _isPreloadingComplete

    fun isUserLoggedIn(): Boolean {
        return validateTokenUseCase.execute()
    }

    /**
     * Memulai preloading data dan akan mengubah _isPreloadingComplete menjadi true saat selesai.
     */
    fun startPreloading() {
        // Jangan jalankan lagi jika sudah selesai
        if (_isPreloadingComplete.value == true) return

        viewModelScope.launch {
            try {
                lookupRepository.preloadAllLookupData()
            } finally {
                // Apapun yang terjadi (sukses atau gagal), tandai bahwa proses telah selesai
                // agar aplikasi bisa melanjutkan navigasi.
                _isPreloadingComplete.postValue(true)
            }
        }
    }
}