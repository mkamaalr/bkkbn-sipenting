package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
            if (validateForm()) {
                findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment1_to_pregnantMotherRegistrationFragment2)
            } else {
                Toast.makeText(requireContext(), "Harap lengkapi semua field yang wajib diisi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener untuk text fields yang bisa diedit
        binding.etName.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(name = text.toString()) }
        binding.etNik.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(nik = text.toString()) }
        binding.etPhoneNumber.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(phoneNumber = text.toString()) }
        binding.etFullAddress.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(fullAddress = text.toString()) }
        binding.etRegistrationDate.doAfterTextChanged { text -> viewModel.updatePregnantMotherData(registrationDate = text.toString()) }
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
        binding.etRegistrationDate.setOnClickListener { showDatePickerDialog(it as TextInputEditText) }
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
        if (binding.etRegistrationDate.text.toString() != data.registrationDate) binding.etRegistrationDate.setText(data.registrationDate)
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
        // Implementasi validasi
        return true // Ganti dengan logika validasi yang sebenarnya
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
