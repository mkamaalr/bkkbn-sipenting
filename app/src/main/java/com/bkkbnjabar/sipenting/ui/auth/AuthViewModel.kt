package com.bkkbnjabar.sipenting.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest // Import LoginRequest
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCase
import com.bkkbnjabar.sipenting.utils.Resource // Import Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val authRepository: AuthRepository // Inject AuthRepository untuk logout dan manajemen token
) : ViewModel() {

    // LiveData untuk melacak status login
    private val _loginResult = MutableLiveData<Resource<UserSession>>()
    val loginResult: LiveData<Resource<UserSession>> = _loginResult

    // LiveData untuk melacak status validasi token
    private val _tokenValidationResult = MutableLiveData<Resource<Boolean>>()
    val tokenValidationResult: LiveData<Resource<Boolean>> = _tokenValidationResult

    // LiveData untuk melacak status logout
    private val _logoutResult = MutableLiveData<Resource<Unit>>()
    val logoutResult: LiveData<Resource<Unit>> = _logoutResult

    init {
        // Inisialisasi status awal
        _loginResult.value = Resource.Idle
        _tokenValidationResult.value = Resource.Idle
        _logoutResult.value = Resource.Idle
    }

    /**
     * Fungsi untuk melakukan proses login pengguna.
     * Menerima objek LoginRequest yang berisi username dan password.
     */
    fun login(request: LoginRequest) {
        _loginResult.value = Resource.Loading() // Set status menjadi Loading

        viewModelScope.launch {
            val result = loginUseCase.execute(request)
            _loginResult.postValue(result) // Perbarui LiveData dengan hasil login
        }
    }

    /**
     * Fungsi untuk memvalidasi token yang ada (misalnya saat aplikasi dimulai).
     * Akan memanggil use case untuk memeriksa apakah token masih valid.
     */
    fun validateToken() {
        _tokenValidationResult.value = Resource.Loading() // Set status menjadi Loading

        viewModelScope.launch {
            val result = validateTokenUseCase.execute()
            _tokenValidationResult.postValue(result) // Perbarui LiveData dengan hasil validasi
        }
    }

    /**
     * Fungsi untuk melakukan proses logout pengguna.
     * Akan membersihkan sesi pengguna dan token yang tersimpan.
     */
    fun logout() {
        _logoutResult.value = Resource.Loading() // Set status menjadi Loading

        viewModelScope.launch {
            // Panggil fungsi logout dari repository
            // Asumsi AuthRepository memiliki fungsi clearSession() atau logout()
            authRepository.clearSession() // Membersihkan token dan data sesi di SharedPrefs
            _logoutResult.postValue(Resource.Success(Unit)) // Set status menjadi Sukses setelah membersihkan sesi
        }
    }

    /**
     * Mereset status login ke Idle.
     * Berguna setelah menampilkan pesan kesalahan atau navigasi.
     */
    fun resetLoginResult() {
        _loginResult.value = Resource.Idle
    }

    /**
     * Mereset status validasi token ke Idle.
     */
    fun resetTokenValidationResult() {
        _tokenValidationResult.value = Resource.Idle
    }

    /**
     * Mereset status logout ke Idle.
     */
    fun resetLogoutResult() {
        _logoutResult.value = Resource.Idle
    }
}
