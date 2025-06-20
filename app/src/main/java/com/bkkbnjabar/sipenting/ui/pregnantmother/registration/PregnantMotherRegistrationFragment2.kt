package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
        loadFormData() // Muat data yang sudah ada ke UI
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            saveFormData() // Simpan data sebelum kembali
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            saveFormData() // Simpan data terbaru ke ViewModel
            if (validateForm()) {
                registrationViewModel.savePregnantMother() // Panggil fungsi simpan ke database/API
            } else {
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFormData() {
        // Obeservasi currentPregnantMother untuk mengisi form jika ada data
        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            pregnantMotherData?.let { data ->
                binding.etHusbandName.setText(data.husbandName)
                binding.etFullAddress.setText(data.fullAddress)
                // Isi bidang lain untuk Fragment 2 di sini jika ada
            }
        }
    }

    private fun saveFormData() {
        registrationViewModel.updatePregnantMotherPart2(
            husbandName = binding.etHusbandName.text.toString().trim(),
            fullAddress = binding.etFullAddress.text.toString().trim()
            // Perbarui bidang lain untuk Fragment 2 di sini
        )
    }

    private fun validateForm(): Boolean {
        // Validasi sederhana untuk bidang yang diperlukan di Fragment 2
        val isHusbandNameValid = binding.etHusbandName.text?.isNotBlank() ?: false
        val isFullAddressValid = binding.etFullAddress.text?.isNotBlank() ?: false

        // Tambahkan logika untuk menampilkan error pada TextInputLayout jika validasi gagal
        if (!isHusbandNameValid) binding.tilHusbandName.error = "Nama Suami tidak boleh kosong" else binding.tilHusbandName.error = null
        if (!isFullAddressValid) binding.tilFullAddress.error = "Alamat Lengkap tidak boleh kosong" else binding.tilFullAddress.error = null

        // Gabungkan semua validasi
        return isHusbandNameValid && isFullAddressValid
    }

    private fun observeViewModel() {
        registrationViewModel.saveResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                    binding.btnPrevious.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnPrevious.isEnabled = true
                    Toast.makeText(context, resource.data ?: "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    // Navigasi kembali ke daftar setelah berhasil menyimpan
                    findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment2_to_pregnantMotherListFragment)
                    // registrationViewModel.resetRegistrationData() // Tidak perlu di sini, sudah dipanggil di savePregnantMother()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnPrevious.isEnabled = true
                    val errorMessage = resource.message ?: "Terjadi kesalahan tidak dikenal saat menyimpan"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
