package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PregnantMotherListViewModel @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository
) : ViewModel() {

    private val _pregnantMothersList = MutableLiveData<Resource<List<PregnantMotherRegistrationData>>>()
    val pregnantMothersList: LiveData<Resource<List<PregnantMotherRegistrationData>>> = _pregnantMothersList

    fun loadPregnantMothers() {
        viewModelScope.launch {
            pregnantMotherRepository.getPregnantMothers().collectLatest { resource ->
                _pregnantMothersList.postValue(resource)
            }
        }
    }
}
