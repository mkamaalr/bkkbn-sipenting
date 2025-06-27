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

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val lookupRepository: LookupRepository
) : ViewModel() {

    // LiveData untuk memberitahu Activity status preloading
    private val _isPreloadingComplete = MutableLiveData<Boolean>(false)
    val isPreloadingComplete: LiveData<Boolean> = _isPreloadingComplete

    // ================== PERBAIKAN DI SINI ==================
    // Tambahkan flag untuk memastikan preload hanya dijalankan sekali
    private var hasPreloadingInitiated = false
    // =======================================================
    /**
     * Memeriksa apakah pengguna sudah login.
     */
    fun isUserLoggedIn(): Boolean {
        return validateTokenUseCase.execute()
    }

    /**
     * Memulai proses preloading data dan akan mengubah _isPreloadingComplete menjadi true saat selesai.
     */
    fun startPreloading() {
        // ================== LOGIKA PENCEGAHAN EKSEKUSI GANDA ==================
        // Jika proses sudah pernah dimulai, langsung keluar dari fungsi ini.
        if (hasPreloadingInitiated) {
            return
        }
        // Jika belum, tandai bahwa proses sekarang sudah dimulai.
        hasPreloadingInitiated = true
        // ======================================================================

        viewModelScope.launch {
            try {
                // Memanggil repository untuk memuat data.
                // Repository ini sekarang cerdas, ia akan memeriksa DB dulu
                // dan memastikan urutan insert benar.
                lookupRepository.preloadAllLookupData()
            } finally {
                // Apapun yang terjadi (sukses atau gagal), tandai bahwa proses telah selesai
                // agar aplikasi bisa melanjutkan navigasi dan tidak terjebak di splash screen.
                _isPreloadingComplete.postValue(true)
            }
        }
    }
}
