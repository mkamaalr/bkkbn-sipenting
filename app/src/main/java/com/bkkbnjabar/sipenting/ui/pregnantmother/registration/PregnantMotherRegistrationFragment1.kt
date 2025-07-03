package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration1Binding
import com.bkkbnjabar.sipenting.domain.model.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Halaman pertama formulir pendaftaran ibu hamil (Data Diri & Alamat).
 */
@AndroidEntryPoint
class PregnantMotherRegistrationFragment1 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration1Binding? = null
    private val binding get() = _binding!!

    // Menggunakan activityViewModels() untuk berbagi ViewModel dengan Fragment lain
    private val viewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPregnantMotherRegistration1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
//            Temporary Disable validation
//            if (validateForm()) {
                // If it is, then save the data and navigate
                saveUIToViewModel()
                findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment1_to_pregnantMotherRegistrationFragment2)
//            }
        }

        // Listener untuk text fields yang bisa diedit
        binding.etName.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(name = text.toString()) }
        binding.etNik.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(nik = text.toString()) }
        binding.etPhoneNumber.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(phoneNumber = text.toString()) }
        binding.etFullAddress.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(fullAddress = text.toString()) }
        binding.etDateOfBirth.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(dateOfBirth = text.toString()) }

        // Listener untuk dropdown yang bisa dipilih (RW dan RT)
        binding.etRw.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedRw = adapterView.getItemAtPosition(position) as Rw
            viewModel.updatePregnantMotherData(rwId = selectedRw.id, rwName = selectedRw.name, rtId = null, rtName = null)
            binding.etRt.setText("", false) // Reset RT
            selectedRw.id?.let { viewModel.getRTS(it) }
        }

        binding.etRt.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedRt = adapterView.getItemAtPosition(position) as Rt
            viewModel.updatePregnantMotherData(rtId = selectedRt.id, rtName = selectedRt.name)
        }

        // Listener untuk menampilkan date picker
        binding.etDateOfBirth.setOnClickListener { showDatePickerDialog(it as TextInputEditText) }
//
//        binding.etFullAddress.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                binding.scrollView.post {
//                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
//                }
//            }
//        }

    }

    private fun observeViewModel() {
        // Observer untuk mengisi data form dari ViewModel ke UI
        viewModel.currentPregnantMother.observe(viewLifecycleOwner) { data ->
            updateUiWithData(data)
        }

        // Observer untuk mengatur field read-only
        viewModel.userLocationDetails.observe(viewLifecycleOwner) { details ->
            val isPrefilled = details != null
            binding.tilProvinsi.isEnabled = !isPrefilled
            binding.tilKabupaten.isEnabled = !isPrefilled
            binding.tilKecamatan.isEnabled = !isPrefilled
            binding.tilKelurahan.isEnabled = !isPrefilled
        }

        // Observer untuk mengisi data ke adapter dropdown
        viewModel.rws.observe(viewLifecycleOwner) { rws ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, rws)
            binding.etRw.setAdapter(adapter)
        }
        viewModel.rts.observe(viewLifecycleOwner) { rts ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, rts)
            binding.etRt.setAdapter(adapter)
        }
    }

    private fun updateUiWithData(data: PregnantMotherRegistrationData) {
        // Hanya update UI jika teksnya berbeda untuk menghindari loop tak terbatas
        if (binding.etName.text.toString() != data.name) binding.etName.setText(data.name)
        if (binding.etNik.text.toString() != data.nik) binding.etNik.setText(data.nik)
        if (binding.etPhoneNumber.text.toString() != data.phoneNumber) binding.etPhoneNumber.setText(data.phoneNumber)
        if (binding.etDateOfBirth.text.toString() != data.dateOfBirth) binding.etDateOfBirth.setText(data.dateOfBirth)
        if (binding.etFullAddress.text.toString() != data.fullAddress) binding.etFullAddress.setText(data.fullAddress)

        // ====================== PERBAIKAN UTAMA ADA DI SINI ======================
        // Menggunakan setText() dengan satu parameter untuk TextInputEditText
        binding.etProvinsi.setText(data.provinsiName)
        binding.etKabupaten.setText(data.kabupatenName)
        binding.etKecamatan.setText(data.kecamatanName)
        binding.etKelurahan.setText(data.kelurahanName)

        // Menggunakan setText() dengan dua parameter untuk AutoCompleteTextView
        if (binding.etRw.text.toString() != data.rwName) binding.etRw.setText(data.rwName ?: "", false)
        if (binding.etRt.text.toString() != data.rtName) binding.etRt.setText(data.rtName ?: "", false)
        // =======================================================================
    }

    private fun saveUIToViewModel() {
        val rwName = binding.etRw.text.toString()
        val rwId = viewModel.rws.value?.find { it.name == rwName }?.id

        val rtName = binding.etRt.text.toString()
        val rtId = viewModel.rts.value?.find { it.name == rtName }?.id

        viewModel.updatePregnantMotherData(
            name = binding.etName.text.toString().trim(),
            nik = binding.etNik.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            phoneNumber = binding.etPhoneNumber.text.toString().trim(),
            fullAddress = binding.etFullAddress.text.toString().trim(),
            rwName = rwName,
            rwId = rwId,
            rtName = rtName,
            rtId = rtId
        )
    }

    private fun showDatePickerDialog(editText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(sdf.format(Date(selection)))
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }

    private fun validateForm(): Boolean {
        // Clear previous errors first
        binding.tilName.error = null
        binding.tilNik.error = null
        binding.tilDateOfBirth.error = null
        binding.tilRw.error = null
        binding.tilRt.error = null
        binding.tilFullAddress.error = null
        binding.tilPhoneNumber.error = null

        var isValid = true

        // 1. Validate Name
        if (binding.etName.text.isNullOrBlank()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            isValid = false
        }

        // 2. Validate NIK
        if (binding.etNik.text.isNullOrBlank()) {
            binding.tilNik.error = "NIK tidak boleh kosong"
            isValid = false
        } else if (binding.etNik.text.toString().length != 16) {
            binding.tilNik.error = "NIK harus 16 digit"
            isValid = false
        }

        // 3. Validate Date of Birth
        if (binding.etDateOfBirth.text.isNullOrBlank()) {
            binding.tilDateOfBirth.error = "Tanggal lahir tidak boleh kosong"
            isValid = false
        }

        // 4. Validate RW
        if (binding.etPhoneNumber.text.isNullOrBlank()) {
            binding.tilPhoneNumber.error = "Nomor Handphone tidak boleh kosong"
            isValid = false
        } else if (binding.etPhoneNumber.text.toString().length != 12) {
            binding.tilPhoneNumber.error = "Nomor Handphone harus 12 digit"
            isValid = false
        }

        // 4. Validate RW
        if (binding.etRw.text.isNullOrBlank()) {
            binding.tilRw.error = "RW tidak boleh kosong"
            isValid = false
        }

        // 5. Validate RT
        if (binding.etRt.text.isNullOrBlank()) {
            binding.tilRt.error = "RT tidak boleh kosong"
            isValid = false
        }

        // 6. Validate Full Address
        if (binding.etFullAddress.text.isNullOrBlank()) {
            binding.tilFullAddress.error = "Alamat lengkap tidak boleh kosong"
            isValid = false
        }

        return isValid
    }

    override fun onPause() {
        super.onPause()
        // Save the current state of the UI to the ViewModel when the user navigates away
        saveUIToViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
