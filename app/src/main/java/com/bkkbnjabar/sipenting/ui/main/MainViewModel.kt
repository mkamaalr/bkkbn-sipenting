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

data class PieChartData(val label: String, val value: Float)


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

    // LiveData to hold the last sync timestamp
    private val _lastSyncTimestamp = MutableLiveData<Long>()
    val lastSyncTimestamp: LiveData<Long> = _lastSyncTimestamp

    private val _ageChartData = MutableLiveData<List<PieChartData>>()
    val ageChartData: LiveData<List<PieChartData>> = _ageChartData

    private val _immunizationChartData = MutableLiveData<List<PieChartData>>()
    val immunizationChartData: LiveData<List<PieChartData>> = _immunizationChartData


    // LiveData to signal the MainActivity to open the navigation drawer

    init {
        // When the ViewModel is created, immediately load the user session from SharedPreferences
        loadUserSession()
        loadDashboardData() // Load chart data
        _lastSyncTimestamp.value = sharedPrefsManager.getLastSyncTimestamp()
    }

    private fun loadUserSession() {
        _userSession.value = sharedPrefsManager.getUserSession()
    }

    /**
     * Fetches dashboard data. In a real app, this would come from a repository.
     */
    private fun loadDashboardData() {
        // Create dummy data that matches the screenshot
        val dummyData = listOf(
            PieChartData("Laki - Laki", 17f),
            PieChartData("Perempuan", 83f)
        )
        _ageChartData.value = dummyData

        val dummyImmunizationData = listOf(
            PieChartData("Tidak Lengkap", 30f),
            PieChartData("Lengkap", 70f)
        )
        _immunizationChartData.value = dummyImmunizationData
    }

    /**
     * Called when the user clicks the "Sync Data" button on the HomeFragment.
     * This function orchestrates the entire two-way sync process.
     */
    fun startSync() = viewModelScope.launch {
        if (_syncStatus.value == SyncStatus.IN_PROGRESS) return@launch
        _syncStatus.postValue(SyncStatus.IN_PROGRESS)
        try {
            syncRepository.uploadPendingData()
            syncRepository.downloadAllData()

            // On success, save the current time and update LiveData
            val currentTime = System.currentTimeMillis()
            sharedPrefsManager.saveLastSyncTimestamp(currentTime)
            _lastSyncTimestamp.postValue(currentTime)

            _syncStatus.postValue(SyncStatus.SUCCESS)
        } catch (e: Exception) {
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
