package com.bkkbnjabar.sipenting.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userData = MutableLiveData<UserSession?>()
    val userData: LiveData<UserSession?> = _userData

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    /**
     * Loads the current user's session data to be displayed in the UI.
     */
    fun loadUserData() {
        _userData.value = authRepository.isLoggedIn().let {
            if (it) {
                // This is a simplified example. In a real app, you might get the UserSession
                // object from SharedPreferences via the repository.
                // Let's assume AuthRepository has a method to get the session.
                // We'll add this method for clarity.
                // For now, creating a dummy one.
                // TODO: Replace with actual call to get full UserSession
                authRepository.getUserSession()
            } else {
                null
            }
        }
    }

    /**
     * Handles the logout process.
     */
    fun logout() {
        authRepository.logout()
        _navigateToLogin.value = true
    }
}
