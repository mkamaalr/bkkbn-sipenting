package com.bkkbnjabar.sipenting.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.domain.model.UserSession // Pastikan import UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository // Pastikan import AuthRepository
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager // Pastikan import SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository, // Digunakan untuk logout
    private val sharedPrefsManager: SharedPrefsManager // Untuk akses sesi
) : ViewModel() {

    // LiveData untuk menyimpan informasi sesi pengguna lengkap
    private val _userSession = MutableLiveData<UserSession?>()
    val userSession: LiveData<UserSession?> = _userSession

    // Menggunakan MutableLiveData langsung untuk isAuthenticated.
    // Nilainya akan diperbarui secara eksplisit oleh loadUserSession() dan logout().
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated // Ini adalah LiveData yang akan diobservasi MainActivity

    private val _logoutResult = MutableLiveData<Resource<Unit>>()
    val logoutResult: LiveData<Resource<Unit>> = _logoutResult

    init {
        // Panggil ini untuk pertama kali saat ViewModel dibuat (misalnya, dengan MainActivity's onCreate).
        // loadUserSession() akan memuat data dan juga memperbarui _isAuthenticated secara langsung.
        loadUserSession()
        _logoutResult.value = Resource.Idle
    }

    /**
     * Memuat informasi sesi pengguna dari SharedPrefsManager.
     * Memperbarui _userSession dan _isAuthenticated berdasarkan data yang ditemukan.
     * Operasi ini dilakukan di Dispatchers.IO karena melibatkan I/O disk.
     * Ini dipanggil dari init{} dan onResume() di MainActivity.
     */
    fun loadUserSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserSession = sharedPrefsManager.getUserSession()
            _userSession.postValue(currentUserSession) // Update _userSession
            // Perbarui _isAuthenticated secara eksplisit berdasarkan status isLoggedIn di sesi
            _isAuthenticated.postValue(currentUserSession?.isLoggedIn == true)
            Log.d("MainViewModel", "Sesi pengguna dimuat: ${currentUserSession?.userName}, Terautentikasi: ${_isAuthenticated.value}")
        }
    }

    /**
     * Melakukan proses logout pengguna.
     * Menggunakan AuthRepository untuk membersihkan sesi baik di backend maupun lokal.
     */
    fun logout() {
        _logoutResult.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {

            // TODO: Fix this logout
//            try {
//                // AuthRepository.logout() akan memanggil sharedPrefsManager.clearAllSessionData()
//                val result = authRepository.logout() // Mengasumsikan ini mengembalikan Resource<Unit>
//
//                if (result is Resource.Success) {
//                    _logoutResult.postValue(Resource.Success(Unit))
//                    _userSession.postValue(null) // Set _userSession menjadi null setelah logout
//                    _isAuthenticated.postValue(false) // Set _isAuthenticated menjadi false secara eksplisit
//                    Log.d("MainViewModel", "Logout berhasil. Sesi dihapus.")
//                } else if (result is Resource.Error) {
//                    _logoutResult.postValue(Resource.Error(result.message))
//                    Log.e("MainViewModel", "Logout gagal: ${result.message}")
//                }
//            } catch (e: Exception) {
//                val errorMessage = "Gagal logout: ${e.localizedMessage ?: "Terjadi kesalahan tidak diketahui"}"
//                _logoutResult.postValue(Resource.Error(errorMessage))
//                Log.e("MainViewModel", "Logout gagal: $errorMessage", e)
//            }
        }
    }

    /**
     * Mereset status logout ke Idle.
     * Metode ini harus dipanggil dari Main thread.
     */
    fun resetLogoutResult() {
        _logoutResult.value = Resource.Idle
    }

    /**
     * Memuat ulang status autentikasi. Ini pada dasarnya sama dengan `loadUserSession()`.
     * Berguna jika Anda ingin secara eksplisit memicu pemeriksaan status.
     */
    fun checkAuthenticationStatus() {
        loadUserSession()
        Log.d("MainViewModel", "Status autentikasi diperiksa ulang melalui loadUserSession().")
    }
}
