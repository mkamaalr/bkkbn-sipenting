package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText // PENTING: Import EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration1Binding
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.utils.Resource
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PregnantMotherRegistrationFragment1 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration1Binding? = null
    private val binding get() = _binding!!

    // Menggunakan activityViewModels() untuk berbagi ViewModel antar Fragment dalam Activity yang sama
    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    // Data untuk Spinner/AutoCompleteTextView
    private var currentProvinsis: List<Provinsi> = emptyList()
    private var currentKabupatens: List<Kabupaten> = emptyList()
    private var currentKecamatans: List<Kecamatan> = emptyList()
    private var currentKelurahans: List<Kelurahan> = emptyList()
    private var currentRws: List<Rw> = emptyList()
    private var currentRts: List<Rt> = emptyList()

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
        loadFormData() // Memuat data awal dari ViewModel ke UI
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            Log.d("PMR_FRAG1_ACTION", "Next button clicked. Calling saveFormData().")
            saveFormData() // Simpan data terbaru ke ViewModel
            if (validateForm()) {
                findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment1_to_pregnantMotherRegistrationFragment2)
                Log.d("PMR_FRAG1_ACTION", "Validation successful. Navigating to Fragment 2.")
            } else {
                Log.w("PMR_FRAG1_ACTION", "Form validation failed.")
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }

        // FIXED: etRegistrationDate sekarang akan menggunakan showDatePickerDialog(EditText)
        binding.etRegistrationDate.setOnClickListener { showDatePickerDialog(binding.etRegistrationDate) }
        // FIXED: etDateOfBirth sekarang akan menggunakan showDatePickerDialog(EditText)
        binding.etDateOfBirth.setOnClickListener { showDatePickerDialog(binding.etDateOfBirth) }

        // Listener untuk AutoCompleteTextView Lokasi
        binding.etProvinsi.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedProvinsi = adapterView.getItemAtPosition(position) as Provinsi
            registrationViewModel.updatePregnantMotherData(
                provinsiId = selectedProvinsi.id,
                provinsiName = selectedProvinsi.name,
                kabupatenId = null, kabupatenName = null, // Reset when parent changes
                kecamatanId = null, kecamatanName = null,
                kelurahanId = null, kelurahanName = null,
                rwId = null, rwName = null,
                rtId = null, rtName = null
            )
            binding.etKabupaten.setText("")
            binding.etKecamatan.setText("")
            binding.etKelurahan.setText("")
            binding.etRw.setText("")
            binding.etRt.setText("")
        }

        binding.etKabupaten.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedKabupaten = adapterView.getItemAtPosition(position) as Kabupaten
            registrationViewModel.updatePregnantMotherData(
                kabupatenId = selectedKabupaten.id,
                kabupatenName = selectedKabupaten.name,
                kecamatanId = null, kecamatanName = null, // Reset when parent changes
                kelurahanId = null, kelurahanName = null,
                rwId = null, rwName = null,
                rtId = null, rtName = null
            )
            binding.etKecamatan.setText("")
            binding.etKelurahan.setText("")
            binding.etRw.setText("")
            binding.etRt.setText("")
            selectedKabupaten.id?.let { registrationViewModel.getKecamatans(it) }
        }

        binding.etKecamatan.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedKecamatan = adapterView.getItemAtPosition(position) as Kecamatan
            registrationViewModel.updatePregnantMotherData(
                kecamatanId = selectedKecamatan.id,
                kecamatanName = selectedKecamatan.name,
                kelurahanId = null, kelurahanName = null, // Reset when parent changes
                rwId = null, rwName = null,
                rtId = null, rtName = null
            )
            binding.etKelurahan.setText("")
            binding.etRw.setText("")
            binding.etRt.setText("")
            selectedKecamatan.id?.let { registrationViewModel.getKelurahans(it) }
        }

        binding.etKelurahan.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedKelurahan = adapterView.getItemAtPosition(position) as Kelurahan
            registrationViewModel.updatePregnantMotherData(
                kelurahanId = selectedKelurahan.id,
                kelurahanName = selectedKelurahan.name,
                rwId = null, rwName = null, // Reset when parent changes
                rtId = null, rtName = null
            )
            binding.etRw.setText("")
            binding.etRt.setText("")
            selectedKelurahan.id?.let { registrationViewModel.getRWS(it) }
        }

        binding.etRw.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedRw = adapterView.getItemAtPosition(position) as Rw
            registrationViewModel.updatePregnantMotherData(
                rwId = selectedRw.id,
                rwName = selectedRw.name,
                rtId = null, rtName = null // Reset when parent changes
            )
            binding.etRt.setText("")
            selectedRw.id?.let { registrationViewModel.getRTS(it) }
        }

        binding.etRt.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedRt = adapterView.getItemAtPosition(position) as Rt
            registrationViewModel.updatePregnantMotherData(
                rtId = selectedRt.id,
                rtName = selectedRt.name
            )
        }
    }

    // FIXED: Mengubah tipe parameter menjadi EditText
    private fun showDatePickerDialog(editText: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.format(Date(selection))
            editText.setText(date)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun loadFormData() {
        Log.d("PMR_FRAG1_LOAD", "loadFormData: Attempting to load mother data from ViewModel into UI.")
        registrationViewModel.currentPregnantMother.value?.let { data ->
            binding.etName.setText(data.name ?: "")
            binding.etNik.setText(data.nik ?: "")
            binding.etDateOfBirth.setText(data.dateOfBirth ?: "")
            binding.etPhoneNumber.setText(data.phoneNumber ?: "")
            binding.etProvinsi.setText(data.provinsiName ?: "", false)
            binding.etKabupaten.setText(data.kabupatenName ?: "", false)
            binding.etKecamatan.setText(data.kecamatanName ?: "", false)
            binding.etKelurahan.setText(data.kelurahanName ?: "", false)
            binding.etRw.setText(data.rwName ?: "", false)
            binding.etRt.setText(data.rtName ?: "", false)
            binding.etFullAddress.setText(data.fullAddress ?: "")
            binding.etRegistrationDate.setText(data.registrationDate ?: "")

            Log.d("PMR_FRAG1_LOAD", "Mother data loaded: Name=${data.name}, NIK=${data.nik}, Provinsi=${data.provinsiName}, LocalId=${data.localId}")

        } ?: run {
            Log.d("PMR_FRAG1_LOAD", "currentPregnantMother is null. Clearing all UI fields.")
            // Clear all fields if data is null
            binding.etName.setText("")
            binding.etNik.setText("")
            binding.etDateOfBirth.setText("")
            binding.etPhoneNumber.setText("")
            binding.etProvinsi.setText("", false)
            binding.etKabupaten.setText("", false)
            binding.etKecamatan.setText("", false)
            binding.etKelurahan.setText("", false)
            binding.etRw.setText("", false)
            binding.etRt.setText("", false)
            binding.etFullAddress.setText("")
            // Default tanggal pendaftaran hari ini untuk form baru
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            binding.etRegistrationDate.setText(today)
        }
    }

    private fun saveFormData() {
        Log.d("PMR_FRAG1_ACTION", "saveFormData: Capturing current UI input and updating ViewModel for Mother Data.")
        val currentMotherData = registrationViewModel.currentPregnantMother.value

        registrationViewModel.updatePregnantMotherData(
            name = binding.etName.text.toString().trim(),
            nik = binding.etNik.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            phoneNumber = binding.etPhoneNumber.text.toString().trim(),
            fullAddress = binding.etFullAddress.text.toString().trim(),
            registrationDate = binding.etRegistrationDate.text.toString().trim()
        )
        Log.d("PMR_FRAG1_ACTION", "saveFormData: ViewModel updated for Mother. Current ViewModel Name: ${registrationViewModel.currentPregnantMother.value?.name}, NIK: ${registrationViewModel.currentPregnantMother.value?.nik}, LocalId: ${registrationViewModel.currentPregnantMother.value?.localId}")
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etName.text.isNullOrBlank()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (binding.etNik.text.isNullOrBlank()) {
            binding.tilNik.error = "NIK tidak boleh kosong"
            isValid = false
        } else if (binding.etNik.text.toString().length != 16) {
            binding.tilNik.error = "NIK harus 16 digit"
            isValid = false
        } else {
            binding.tilNik.error = null
        }

        if (binding.etDateOfBirth.text.isNullOrBlank()) {
            binding.tilDateOfBirth.error = "Tanggal Lahir tidak boleh kosong"
            isValid = false
        } else {
            binding.tilDateOfBirth.error = null
        }

        if (binding.etPhoneNumber.text.isNullOrBlank()) {
            binding.tilPhoneNumber.error = "Nomor Telepon tidak boleh kosong"
            isValid = false
        } else {
            binding.tilPhoneNumber.error = null
        }

        // Validasi AutoCompleteTextViews untuk lokasi
        if (binding.etProvinsi.text.isNullOrBlank()) {
            binding.tilProvinsi.error = "Provinsi tidak boleh kosong"
            isValid = false
        } else {
            binding.tilProvinsi.error = null
        }
        if (binding.etKabupaten.text.isNullOrBlank()) {
            binding.tilKabupaten.error = "Kabupaten tidak boleh kosong"
            isValid = false
        } else {
            binding.tilKabupaten.error = null
        }
        if (binding.etKecamatan.text.isNullOrBlank()) {
            binding.tilKecamatan.error = "Kecamatan tidak boleh kosong"
            isValid = false
        } else {
            binding.tilKecamatan.error = null
        }
        if (binding.etKelurahan.text.isNullOrBlank()) {
            binding.tilKelurahan.error = "Kelurahan tidak boleh kosong"
            isValid = false
        } else {
            binding.tilKelurahan.error = null
        }
        if (binding.etRw.text.isNullOrBlank()) {
            binding.tilRw.error = "RW tidak boleh kosong"
            isValid = false
        } else {
            binding.tilRw.error = null
        }
        if (binding.etRt.text.isNullOrBlank()) {
            binding.tilRt.error = "RT tidak boleh kosong"
            isValid = false
        } else {
            binding.tilRt.error = null
        }
        if (binding.etFullAddress.text.isNullOrBlank()) {
            binding.tilFullAddress.error = "Alamat Lengkap tidak boleh kosong"
            isValid = false
        } else {
            binding.tilFullAddress.error = null
        }
        if (binding.etRegistrationDate.text.isNullOrBlank()) {
            binding.tilRegistrationDate.error = "Tanggal Pendaftaran tidak boleh kosong"
            isValid = false
        } else {
            binding.tilRegistrationDate.error = null
        }

        return isValid
    }


    private fun observeViewModel() {
        Log.d("PMR_FRAG1_OBSERVE", "observeViewModel: Setting up observers.")

        // Menggunakan viewLifecycleOwner.lifecycleScope.launch untuk observasi Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi provinsis
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.provinsis.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading provinsis...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentProvinsis = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { p -> p.name })
                                binding.etProvinsi.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "Provinsis loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading provinsis: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading provinsis: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Provinsis idle.")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi kabupatens
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.kabupatens.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading kabupatens...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentKabupatens = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { k -> k.name })
                                binding.etKabupaten.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "Kabupatens loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading kabupatens: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading kabupatens: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Kabupatens idle.")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi kecamatans
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.kecamatans.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading kecamatans...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentKecamatans = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { k -> k.name })
                                binding.etKecamatan.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "Kecamatans loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading kecamatans: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading kecamatans: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Kecamatans idle.")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi kelurahans
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.kelurahans.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading kelurahans...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentKelurahans = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { k -> k.name })
                                binding.etKelurahan.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "Kelurahans loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading kelurahans: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading kelurahans: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Kelurahans idle.")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi RWs
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.rws.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading RWs...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentRws = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { r -> r.name })
                                binding.etRw.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "RWs loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading RWs: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading RWs: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "RWs idle.")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observasi RTs
                // FIXED: Mengubah .collect menjadi .observe(viewLifecycleOwner)
                registrationViewModel.rts.observe(viewLifecycleOwner) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("PMR_FRAG1_OBSERVE", "Loading RTs...")
                        }
                        is Resource.Success -> {
                            resource.data?.let {
                                currentRts = it
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { r -> r.name })
                                binding.etRt.setAdapter(adapter)
                                Log.d("PMR_FRAG1_OBSERVE", "RTs loaded: ${it.size} items.")
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Error loading RTs: ${resource.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PMR_FRAG1_OBSERVE", "Error loading RTs: ${resource.message}")
                        }
                        Resource.Idle -> {
                            Log.d("PMR_FRAG1_OBSERVE", "RTs idle.")
                        }
                    }
                }
            }
        }

        // Observing currentPregnantMother to update UI fields
        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            Log.d("PMR_FRAG1_OBSERVE", "currentPregnantMother observed. Data: Name=${pregnantMotherData?.name}, NIK=${pregnantMotherData?.nik}, LocalId=${pregnantMotherData?.localId}")
            pregnantMotherData?.let { data ->
                // Perbarui UI hanya jika nilai berbeda untuk menghindari loop atau reset fokus
                if (binding.etName.text.toString() != (data.name ?: "")) {
                    binding.etName.setText(data.name ?: "")
                }
                if (binding.etNik.text.toString() != (data.nik ?: "")) {
                    binding.etNik.setText(data.nik ?: "")
                }
                if (binding.etDateOfBirth.text.toString() != (data.dateOfBirth ?: "")) {
                    binding.etDateOfBirth.setText(data.dateOfBirth ?: "")
                }
                if (binding.etPhoneNumber.text.toString() != (data.phoneNumber ?: "")) {
                    binding.etPhoneNumber.setText(data.phoneNumber ?: "")
                }
                // Khusus untuk AutoCompleteTextViews, setText(text, false) agar tidak memicu listener
                if (binding.etProvinsi.text.toString() != (data.provinsiName ?: "")) {
                    binding.etProvinsi.setText(data.provinsiName ?: "", false)
                }
                if (binding.etKabupaten.text.toString() != (data.kabupatenName ?: "")) {
                    binding.etKabupaten.setText(data.kabupatenName ?: "", false)
                }
                if (binding.etKecamatan.text.toString() != (data.kecamatanName ?: "")) {
                    binding.etKecamatan.setText(data.kecamatanName ?: "", false)
                }
                if (binding.etKelurahan.text.toString() != (data.kelurahanName ?: "")) {
                    binding.etKelurahan.setText(data.kelurahanName ?: "", false)
                }
                if (binding.etRw.text.toString() != (data.rwName ?: "")) {
                    binding.etRw.setText(data.rwName ?: "", false)
                }
                if (binding.etRt.text.toString() != (data.rtName ?: "")) {
                    binding.etRt.setText(data.rtName ?: "", false)
                }
                if (binding.etFullAddress.text.toString() != (data.fullAddress ?: "")) {
                    binding.etFullAddress.setText(data.fullAddress ?: "")
                }
                if (binding.etRegistrationDate.text.toString() != (data.registrationDate ?: "")) {
                    binding.etRegistrationDate.setText(data.registrationDate ?: "")
                }
            } ?: run {
                Log.d("PMR_FRAG1_OBSERVE", "currentPregnantMother is NULL. Re-initializing form.")
                loadFormData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PMR_FRAG1_LIFECYCLE", "onDestroyView called.")
    }
}
