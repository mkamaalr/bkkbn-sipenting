package com.bkkbnjabar.sipenting.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.auth.AuthResponse
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // Penting: Anotasi ini menandai ViewModel untuk injeksi Hilt
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult: LiveData<Resource<AuthResponse>> = _loginResult

    fun login(username: String? = null, email: String? = null, password: String) {
        _loginResult.value = Resource.Loading()
        viewModelScope.launch {
            val result = loginUseCase.execute(username, email, password)
            _loginResult.postValue(result)
        }
    }
}