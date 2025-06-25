package com.bkkbnjabar.sipenting.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.domain.model.AuthResponse
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the LoginFragment.
 * It handles the business logic for the login process.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<AuthResponse>>(Resource.Idle)
    val loginResult: LiveData<Resource<AuthResponse>> = _loginResult

    /**
     * Initiates the login process.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    fun login(username: String?, password: String?) {
        viewModelScope.launch {
            // Basic validation before creating the request
            if (username.isNullOrBlank() || password.isNullOrBlank()){
                _loginResult.postValue(Resource.Error("Username dan password tidak boleh kosong."))
                return@launch
            }

            val loginRequest = LoginRequest(username, password)

            try {
                loginUseCase.execute(loginRequest).collectLatest { result ->
                    _loginResult.postValue(result)
                }
            } catch (e: Exception) {
                _loginResult.postValue(Resource.Error(e.message ?: "Terjadi kesalahan tidak diketahui"))
            }
        }
    }
}
