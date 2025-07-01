package com.bkkbnjabar.sipenting.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.domain.model.UserSession
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.domain.repository.SyncRepository // Assuming you create this interface
import com.bkkbnjabar.sipenting.utils.Event
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Enum to represent the different states of the synchronization process
enum class SyncStatus {
    IDLE,
    IN_PROGRESS,
    SUCCESS,
    ERROR
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository // A central repository to handle sync logic
) : ViewModel() {

    // LiveData to hold the current user's session details
    private val _userSession = MutableLiveData<UserSession?>()
    val userSession: LiveData<UserSession?> = _userSession

    // LiveData to communicate the sync status to the UI (for the sync button icon)
    private val _syncStatus = MutableLiveData<SyncStatus>(SyncStatus.IDLE)
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    // LiveData to signal when the user has been logged out
    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent: LiveData<Event<Unit>> = _logoutEvent

    // LiveData to signal the MainActivity to open the navigation drawer

    init {
        // When the ViewModel is created, immediately load the user session from SharedPreferences
        loadUserSession()
    }

    private fun loadUserSession() {
        _userSession.value = sharedPrefsManager.getUserSession()
    }

    /**
     * Called when the user clicks the "Sync Data" button on the HomeFragment.
     * This function orchestrates the entire two-way sync process.
     */
    fun startSync() = viewModelScope.launch {
        // Don't start a new sync if one is already in progress
        if (_syncStatus.value == SyncStatus.IN_PROGRESS) return@launch

        _syncStatus.postValue(SyncStatus.IN_PROGRESS)

        try {
            // Step 1: Upload all pending local data to the server.
            syncRepository.uploadPendingData()

            // Step 2: After uploading, download the latest data from the server.
            syncRepository.downloadAllData()

            // If both steps succeed, update the status
            _syncStatus.postValue(SyncStatus.SUCCESS)
        } catch (e: Exception) {
            // If anything goes wrong, set the status to Error
            _syncStatus.postValue(SyncStatus.ERROR)
        }
    }

    /**
     * Logs the user out by clearing their session data and firing an event
     * for the Activity to observe and navigate to the login screen.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutEvent.postValue(Event(Unit))
        }
    }
}
