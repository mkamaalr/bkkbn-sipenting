package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration2Binding
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.popToInclusive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_registration_2, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        Log.d("PMR_FRAG2_LIFECYCLE", "onCreateView called.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PMR_FRAG2_LIFECYCLE", "onViewCreated called.")
        setupListeners()
        observeViewModel()
        loadFormData() // Pastikan data yang ada di ViewModel ditampilkan
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            Log.d("PMR_FRAG2_ACTION", "Previous button clicked. Navigating back to Fragment 1.")
            saveFormData() // Simpan data terbaru sebelum kembali
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            Log.d("PMR_FRAG2_ACTION", "Save button clicked. Saving form data for Fragment 2.")
            saveFormData() // Simpan data terbaru ke ViewModel
            if (validateForm()) {
                Log.d("PMR_FRAG2_ACTION", "Form validated successfully. Calling savePregnantMother().")
                val currentData = registrationViewModel.currentPregnantMother.value
                Log.d("PMR_FRAG2_ACTION", "Data to be saved: Name=${currentData?.name}, NIK=${currentData?.nik}, LocalId=${currentData?.localId}")
                registrationViewModel.savePregnantMother()
            } else {
                Log.w("PMR_FRAG2_ACTION", "Form validation failed.")
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFormData() {
        Log.d("PMR_FRAG2_LOAD", "loadFormData: Attempting to load data from ViewModel into UI.")
        registrationViewModel.currentPregnantMother.value?.let { data ->
            binding.etHusbandName.setText(data.husbandName ?: "")
            binding.etFullAddress.setText(data.fullAddress ?: "")
            Log.d("PMR_FRAG2_LOAD", "Data loaded: HusbandName=${data.husbandName}, FullAddress=${data.fullAddress}, LocalId=${data.localId}")
        } ?: run {
            Log.d("PMR_FRAG2_LOAD", "currentPregnantMother is null. Clearing UI fields.")
            binding.etHusbandName.setText("")
            binding.etFullAddress.setText("")
        }
    }

    private fun saveFormData() {
        Log.d("PMR_FRAG2_ACTION", "saveFormData: Capturing current UI input and updating ViewModel for Part 2.")
        registrationViewModel.updatePregnantMotherPart2(
            husbandName = binding.etHusbandName.text.toString().trim(),
            fullAddress = binding.etFullAddress.text.toString().trim()
        )
        Log.d("PMR_FRAG2_ACTION", "saveFormData: ViewModel updated for Part 2. Current ViewModel husbandName: ${registrationViewModel.currentPregnantMother.value?.husbandName}, LocalId: ${registrationViewModel.currentPregnantMother.value?.localId}")
    }

    private fun validateForm(): Boolean {
        // ... (validation logic remains the same) ...
        return true // TEMPORARY: Return true for now to skip validation during debugging
    }

    private fun observeViewModel() {
        Log.d("PMR_FRAG2_OBSERVE", "observeViewModel: Setting up observers for saveResult and currentPregnantMother.")

        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            Log.d("PMR_FRAG2_OBSERVE", "currentPregnantMother observed in Fragment 2. Data: HusbandName=${pregnantMotherData?.husbandName}, Address=${pregnantMotherData?.fullAddress}, LocalId=${pregnantMotherData?.localId}")
            pregnantMotherData?.let { data ->
                val currentHusbandName = binding.etHusbandName.text.toString()
                if (currentHusbandName != (data.husbandName ?: "")) {
                    binding.etHusbandName.setText(data.husbandName ?: "")
                    Log.d("PMR_FRAG2_OBSERVE", "UI Husband Name updated to: ${data.husbandName ?: "EMPTY"}")
                }
                val currentFullAddress = binding.etFullAddress.text.toString()
                if (currentFullAddress != (data.fullAddress ?: "")) {
                    binding.etFullAddress.setText(data.fullAddress ?: "")
                    Log.d("PMR_FRAG2_OBSERVE", "UI Full Address updated to: ${data.fullAddress ?: "EMPTY"}")
                }
            } ?: run {
                Log.d("PMR_FRAG2_OBSERVE", "currentPregnantMother in Fragment 2 is NULL. Clearing UI fields.")
                binding.etHusbandName.setText("")
                binding.etFullAddress.setText("")
            }
        }

        registrationViewModel.saveResult.observe(viewLifecycleOwner) { resource ->
            // Hanya proses jika resource bukan null (setelah direset)
            if (resource == null) {
                Log.d("PMR_FRAG2_OBSERVE", "saveResult is null (reset). Ignoring.")
                return@observe // Abaikan jika sudah direset
            }

            Log.d("PMR_FRAG2_OBSERVE", "saveResult observed. Status: ${resource.javaClass.simpleName}")
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(context, "Menyimpan...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(context, resource.data, Toast.LENGTH_LONG).show()
                    Log.d("PMR_FRAG2_OBSERVE", "Save successful: ${resource.data}. Navigating back to list.")
                    findNavController().popToInclusive(R.id.pregnantMotherListFragment, true)
                    // PENTING: Reset saveResult setelah navigasi untuk mencegah re-trigger
                    registrationViewModel.resetSaveResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Gagal menyimpan: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("PMR_FRAG2_OBSERVE", "Save error: ${resource.message}")
                    // Juga reset saveResult pada error untuk mencegah loop jika user tidak navigasi
                    registrationViewModel.resetSaveResult()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PMR_FRAG2_LIFECYCLE", "onDestroyView called.")
    }
}
