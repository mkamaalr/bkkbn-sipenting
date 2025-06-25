package com.bkkbnjabar.sipenting.ui.pregnantmother

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel untuk PregnantMotherListFragment.
 */
@HiltViewModel
class PregnantMotherListViewModel @Inject constructor(
    private val repository: PregnantMotherRepository
) : ViewModel() {

    /**
     * Menyediakan daftar semua ibu hamil dari database lokal sebagai LiveData.
     */
    val allPregnantMothers: LiveData<List<PregnantMotherEntity>> = repository.getAllPregnantMothers().asLiveData()
}
