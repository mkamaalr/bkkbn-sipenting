package com.bkkbnjabar.sipenting.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // LiveData untuk melacak status proses login
    private val _loginResult = MutableLiveData<Resource<UserSession>>()
    val loginResult: LiveData<Resource<UserSession>> = _loginResult

    init {
        // Inisialisasi status awal sebagai Idle
        _loginResult.value = Resource.Idle
    }

    /**
     * Memulai proses login dengan kredensial yang diberikan.
     * Mengatur status _loginResult ke Loading, kemudian memanggil LoginUseCase.
     * Setelah use case mengembalikan hasil, _loginResult diperbarui.
     *
     * @param request Objek LoginRequest yang berisi email/username dan password.
     */
    fun login(request: LoginRequest) {
        _loginResult.value = Resource.Loading() // Set status ke Loading

        viewModelScope.launch {
            val result = loginUseCase.execute(request) // Panggil use case
            _loginResult.postValue(result) // Perbarui LiveData dengan hasil
        }
    }

    /**
     * Mereset status _loginResult ke Idle.
     * Berguna setelah menampilkan pesan sukses/error atau setelah navigasi,
     * agar LiveData tidak memicu ulang observer saat konfigurasi berubah.
     */
    fun resetLoginResult() {
        _loginResult.value = Resource.Idle
    }
}
