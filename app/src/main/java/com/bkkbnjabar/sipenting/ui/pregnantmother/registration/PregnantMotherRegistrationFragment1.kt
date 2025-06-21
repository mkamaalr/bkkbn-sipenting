package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.data.model.Kelurahan
import com.bkkbnjabar.sipenting.data.model.Rt
import com.bkkbnjabar.sipenting.data.model.Rw
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration1Binding
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class PregnantMotherRegistrationFragment1 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration1Binding? = null
    private val binding get() = _binding!!

    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    private var selectedKelurahan: Kelurahan? = null
    private var selectedRw: Rw? = null
    private var selectedRt: Rt? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_registration_1, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        Log.d("PMR_FRAG1_LIFECYCLE", "onCreateView called.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PMR_FRAG1_LIFECYCLE", "onViewCreated called.")
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnNext.setOnClickListener {
            Log.d("PMR_FRAG1_ACTION", "Next button clicked. Saving form data for Fragment 1.")
            saveFormData() // Simpan data terbaru ke ViewModel
            if (validateForm()) {
                Log.d("PMR_FRAG1_ACTION", "Form validated successfully. Navigating to Fragment 2.")
                findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment1_to_pregnantMotherRegistrationFragment2)
            } else {
                Log.w("PMR_FRAG1_ACTION", "Form validation failed.")
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.autocompleteRw.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val selectedItem = registrationViewModel.rws.value?.data?.find { it.name == selectedName }

            selectedRw = selectedItem
            selectedRt = null // Kosongkan RT saat RW berubah
            binding.autocompleteRt.setText("", false) // Kosongkan visual RT

            Log.d("PMR_FRAG1_ACTION", "RW selected: ${selectedItem?.name} (ID: ${selectedItem?.id}). Updating ViewModel.")
            registrationViewModel.updatePregnantMotherPart1(
                name = binding.etName.text.toString().trim(),
                nik = binding.etNik.text.toString().trim(),
                dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
                phoneNumber = binding.etPhoneNumber.text.toString().trim(),
                rwName = selectedItem?.name,
                rwId = selectedItem?.id,
                rtName = null,
                rtId = null
            )
            selectedItem?.id?.let { registrationViewModel.getRTS(it) }
        }

        binding.autocompleteRt.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val selectedItem = registrationViewModel.rts.value?.data?.find { it.name == selectedName }

            selectedRt = selectedItem
            Log.d("PMR_FRAG1_ACTION", "RT selected: ${selectedItem?.name} (ID: ${selectedItem?.id}). Updating ViewModel.")
            registrationViewModel.updatePregnantMotherPart1(
                name = binding.etName.text.toString().trim(),
                nik = binding.etNik.text.toString().trim(),
                dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
                phoneNumber = binding.etPhoneNumber.text.toString().trim(),
                rwName = registrationViewModel.currentPregnantMother.value?.rwName,
                rwId = registrationViewModel.currentPregnantMother.value?.rwId,
                rtName = selectedItem?.name,
                rtId = selectedItem?.id
            )
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etDateOfBirth.setText(dateFormat.format(selectedDate.time))
            Log.d("PMR_FRAG1_ACTION", "Date of birth set to: ${binding.etDateOfBirth.text}")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun saveFormData() {
        Log.d("PMR_FRAG1_ACTION", "saveFormData: Capturing current UI input and updating ViewModel for Part 1.")
        registrationViewModel.updatePregnantMotherPart1(
            name = binding.etName.text.toString().trim(),
            nik = binding.etNik.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            phoneNumber = binding.etPhoneNumber.text.toString().trim(),
            provinsiName = registrationViewModel.currentPregnantMother.value?.provinsiName,
            provinsiId = registrationViewModel.currentPregnantMother.value?.provinsiId,
            kabupatenName = registrationViewModel.currentPregnantMother.value?.kabupatenName,
            kabupatenId = registrationViewModel.currentPregnantMother.value?.kabupatenId,
            kecamatanName = registrationViewModel.currentPregnantMother.value?.kecamatanName,
            kecamatanId = registrationViewModel.currentPregnantMother.value?.kecamatanId,
            kelurahanName = registrationViewModel.currentPregnantMother.value?.kelurahanName,
            kelurahanId = registrationViewModel.currentPregnantMother.value?.kelurahanId,
            rwName = selectedRw?.name,
            rwId = selectedRw?.id,
            rtName = selectedRt?.name,
            rtId = selectedRt?.id
        )
        Log.d("PMR_FRAG1_ACTION", "saveFormData: ViewModel updated for Part 1. Current ViewModel name: ${registrationViewModel.currentPregnantMother.value?.name}, LocalId: ${registrationViewModel.currentPregnantMother.value?.localId}")
    }

    private fun validateForm(): Boolean {
        // ... (validation logic remains the same) ...
        return true // TEMPORARY: Return true for now to skip validation during debugging
    }

    private fun observeViewModel() {
        Log.d("PMR_FRAG1_OBSERVE", "observeViewModel: Setting up observers for currentPregnantMother, RWs, RTs.")
        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            Log.d("PMR_FRAG1_OBSERVE", "currentPregnantMother observed. Data: Name=${pregnantMotherData?.name}, NIK=${pregnantMotherData?.nik}, RW=${pregnantMotherData?.rwName}, RT=${pregnantMotherData?.rtName}, LocalId=${pregnantMotherData?.localId}")
            pregnantMotherData?.let { data ->
                // Update field input utama hanya jika nilai di ViewModel berbeda
                if (binding.etName.text.toString() != (data.name ?: "")) {
                    binding.etName.setText(data.name ?: "")
                    Log.d("PMR_FRAG1_OBSERVE", "UI Name updated to: ${data.name ?: "EMPTY"}")
                }
                if (binding.etNik.text.toString() != (data.nik ?: "")) {
                    binding.etNik.setText(data.nik ?: "")
                    Log.d("PMR_FRAG1_OBSERVE", "UI NIK updated to: ${data.nik ?: "EMPTY"}")
                }
                if (binding.etDateOfBirth.text.toString() != (data.dateOfBirth ?: "")) {
                    binding.etDateOfBirth.setText(data.dateOfBirth ?: "")
                    Log.d("PMR_FRAG1_OBSERVE", "UI DoB updated to: ${data.dateOfBirth ?: "EMPTY"}")
                }
                if (binding.etPhoneNumber.text.toString() != (data.phoneNumber ?: "")) {
                    binding.etPhoneNumber.setText(data.phoneNumber ?: "")
                    Log.d("PMR_FRAG1_OBSERVE", "UI Phone updated to: ${data.phoneNumber ?: "EMPTY"}")
                }

                // Isi data lokasi read-only
                binding.etProvinsi.setText(data.provinsiName ?: "")
                binding.etKabupaten.setText(data.kabupatenName ?: "")
                binding.etKecamatanReadonly.setText(data.kecamatanName ?: "")
                binding.etKelurahanReadonly.setText(data.kelurahanName ?: "")
                Log.d("PMR_FRAG1_OBSERVE", "UI Location fields updated from ViewModel.")

                // Isi AutoCompleteTextView untuk RW & RT
                if (binding.autocompleteRw.text.toString() != (data.rwName ?: "")) {
                    binding.autocompleteRw.setText(data.rwName ?: "", false)
                    Log.d("PMR_FRAG1_OBSERVE", "UI RW updated to: ${data.rwName ?: "EMPTY"}")
                }
                if (binding.autocompleteRt.text.toString() != (data.rtName ?: "")) {
                    binding.autocompleteRt.setText(data.rtName ?: "", false)
                    Log.d("PMR_FRAG1_OBSERVE", "UI RT updated to: ${data.rtName ?: "EMPTY"}")
                }

                // Inisialisasi selectedKelurahan, selectedRw, selectedRt saat data dimuat
                // dan picu pemuatan daftar RW/RT jika ID tersedia
                if (data.kelurahanId != null && data.kecamatanId != null && selectedKelurahan?.id != data.kelurahanId) {
                    selectedKelurahan = Kelurahan(data.kelurahanId, data.kelurahanName ?: "", data.kecamatanId, null)
                    Log.d("PMR_FRAG1_OBSERVE", "Selected Kelurahan initialized/updated: ${selectedKelurahan?.name}. Triggering getRWS.")
                    registrationViewModel.getRWS(data.kelurahanId) // PENTING: Panggil getRWS di sini
                } else if (data.kelurahanId == null) {
                    selectedKelurahan = null
                    Log.d("PMR_FRAG1_OBSERVE", "Selected Kelurahan reset to null.")
                }

                if (data.rwId != null && data.kelurahanId != null && selectedRw?.id != data.rwId) {
                    selectedRw = Rw(data.rwId, data.rwName ?: "", data.kelurahanId)
                    Log.d("PMR_FRAG1_OBSERVE", "Selected RW initialized/updated: ${selectedRw?.name}. Triggering getRTS.")
                    registrationViewModel.getRTS(data.rwId) // PENTING: Panggil getRTS di sini
                } else if (data.rwId == null) {
                    selectedRw = null
                    Log.d("PMR_FRAG1_OBSERVE", "Selected RW reset to null.")
                }

                if (data.rtId != null && data.rwId != null && selectedRt?.id != data.rtId) {
                    selectedRt = Rt(data.rtId, data.rtName ?: "", data.rwId)
                    Log.d("PMR_FRAG1_OBSERVE", "Selected RT initialized/updated: ${selectedRt?.name}.")
                } else if (data.rtId == null) {
                    selectedRt = null
                    Log.d("PMR_FRAG1_OBSERVE", "Selected RT reset to null.")
                } else {

                }

            } ?: run {
                // If pregnantMotherData is null, explicitly clear all fields
                Log.d("PMR_FRAG1_OBSERVE", "currentPregnantMother is NULL. Clearing all UI fields.")
                binding.etName.setText("")
                binding.etNik.setText("")
                binding.etDateOfBirth.setText("")
                binding.etPhoneNumber.setText("")
                binding.etProvinsi.setText("")
                binding.etKabupaten.setText("")
                binding.etKecamatanReadonly.setText("")
                binding.etKelurahanReadonly.setText("")
                binding.autocompleteRw.setText("", false)
                binding.autocompleteRt.setText("", false)
                selectedKelurahan = null
                selectedRw = null
                selectedRt = null
            }
        }

        // Observasi daftar RW dari ViewModel
        registrationViewModel.rws.observe(viewLifecycleOwner) { resource ->
            Log.d("PMR_FRAG1_OBSERVE", "RWS observed. Status: ${resource.javaClass.simpleName}")
            when (resource) {
                is Resource.Loading -> {
                    binding.tilRw.helperText = "Memuat RW..."
                    // Hanya reset adapter, jangan kosongkan selectedRw/selectedRt terlalu dini
                    binding.autocompleteRw.setAdapter(null)
                    binding.autocompleteRt.setText("", false) // Kosongkan visual RT saat RW loading
                    binding.autocompleteRt.setAdapter(null)
                }
                is Resource.Success -> {
                    val rws = resource.data ?: emptyList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        rws.map { it.name }
                    )
                    binding.autocompleteRw.setAdapter(adapter)
                    binding.tilRw.helperText = null

                    val currentRwId = registrationViewModel.currentPregnantMother.value?.rwId
                    val matchedRw = rws.find { it.id == currentRwId }
                    if (matchedRw != null) {
                        selectedRw = matchedRw
                        if (binding.autocompleteRw.text.toString() != matchedRw.name) {
                            binding.autocompleteRw.setText(matchedRw.name, false)
                        }
                        Log.d("PMR_FRAG1_OBSERVE", "RWS: Matched and set selectedRw to ${matchedRw.name} (ID: ${matchedRw.id}). Triggering getRTS.")
                        registrationViewModel.getRTS(matchedRw.id) // PENTING: Panggil getRTS di sini
                    } else {
                        // Jika tidak ada RW yang cocok dengan ID di ViewModel, kosongkan UI dan selectedRw
                        if (binding.autocompleteRw.text.toString().isNotBlank() || selectedRw != null) {
                            binding.autocompleteRw.setText("", false)
                            selectedRw = null
                            Log.d("PMR_FRAG1_OBSERVE", "RWS: No match for RW ID $currentRwId. Clearing UI RW and selectedRw.")
                        }
                        // Juga kosongkan RT jika RW tidak cocok
                        if (binding.autocompleteRt.text.toString().isNotBlank() || selectedRt != null) {
                            binding.autocompleteRt.setText("", false)
                            binding.autocompleteRt.setAdapter(null)
                            selectedRt = null
                            Log.d("PMR_FRAG1_OBSERVE", "RWS: Clearing UI RT and selectedRt as well.")
                        }
                    }
                }
                is Resource.Error -> {
                    binding.tilRw.error = "Gagal memuat RW: ${resource.message}"
                    Toast.makeText(context, "Gagal memuat RW: ${resource.message}", Toast.LENGTH_SHORT).show()
                    binding.autocompleteRw.setText("", false)
                    binding.autocompleteRw.setAdapter(null)
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRw = null
                    selectedRt = null
                    Log.e("PMR_FRAG1_OBSERVE", "RWS: Error: ${resource.message}")
                }
            }
        }

        // Observasi daftar RT dari ViewModel
        registrationViewModel.rts.observe(viewLifecycleOwner) { resource ->
            Log.d("PMR_FRAG1_OBSERVE", "RTS observed. Status: ${resource.javaClass.simpleName}")
            when (resource) {
                is Resource.Loading -> {
                    binding.tilRt.helperText = "Memuat RT..."
                    binding.autocompleteRt.setAdapter(null)
                }
                is Resource.Success -> {
                    val rts = resource.data ?: emptyList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        rts.map { it.name }
                    )
                    binding.autocompleteRt.setAdapter(adapter)
                    binding.tilRt.helperText = null

                    val currentRtId = registrationViewModel.currentPregnantMother.value?.rtId
                    val matchedRt = rts.find { it.id == currentRtId }
                    if (matchedRt != null) {
                        selectedRt = matchedRt
                        if (binding.autocompleteRt.text.toString() != matchedRt.name) {
                            binding.autocompleteRt.setText(matchedRt.name, false)
                        }
                        Log.d("PMR_FRAG1_OBSERVE", "RTS: Matched and set selectedRt to ${matchedRt.name} (ID: ${matchedRt.id})")
                    } else {
                        if (binding.autocompleteRt.text.toString().isNotBlank()) {
                            binding.autocompleteRt.setText("", false)
                            Log.d("PMR_FRAG1_OBSERVE", "RTS: No match for RT ID $currentRtId. Clearing UI RT.")
                        }
                        selectedRt = null
                    }
                }
                is Resource.Error -> {
                    binding.tilRt.error = "Gagal memuat RT: ${resource.message}"
                    Toast.makeText(context, "Gagal memuat RT: ${resource.message}", Toast.LENGTH_SHORT).show()
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRt = null
                    Log.e("PMR_FRAG1_OBSERVE", "RTS: Error: ${resource.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PMR_FRAG1_LIFECYCLE", "onDestroyView called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d("PMR_FRAG1_LIFECYCLE", "onResume called.")
    }

    override fun onPause() {
        super.onPause()
        Log.d("PMR_FRAG1_LIFECYCLE", "onPause called.")
    }
}
