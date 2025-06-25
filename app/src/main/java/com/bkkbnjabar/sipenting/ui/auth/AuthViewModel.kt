package com.bkkbnjabar.sipenting.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel shared across the authentication flow (AuthActivity).
 * It handles high-level navigation events, like navigating to the main part of the app.
 */
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> = _navigateToMain

    /**
     * Triggers the navigation event to move to MainActivity.
     */
    fun onLoginSuccess() {
        _navigateToMain.value = true
    }

    /**
     * Resets the navigation event to prevent re-triggering on configuration changes.
     */
    fun onNavigationDone() {
        _navigateToMain.value = false
    }
}
