package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.app.DatePickerDialog
import android.os.Bundle
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

    // Menggunakan activityViewModels() untuk berbagi ViewModel antar fragment
    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    // Variabel untuk menyimpan objek yang dipilih dari dropdown
    // Ini membantu saat menyimpan data ke ViewModel dan validasi
    private var selectedKelurahan: Kelurahan? = null
    private var selectedRw: Rw? = null
    private var selectedRt: Rt? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_registration_1, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel() // Panggil observeViewModel di sini
        loadFormData() // Muat data yang sudah ada ke UI
    }

    private fun setupListeners() {
        binding.etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnNext.setOnClickListener {
            saveFormData() // Simpan data terbaru ke ViewModel
            if (validateForm()) {
                findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment1_to_pregnantMotherRegistrationFragment2)
            } else {
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener untuk autocomplete RW
        binding.autocompleteRw.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position).toString() // Ambil nama (String)
            // Cari objek Rw yang sesuai dari daftar yang ada di ViewModel
            val selectedItem = registrationViewModel.rws.value?.data?.find { it.name == selectedName }

            selectedRw = selectedItem // Simpan objek Rw yang ditemukan
            // Update ViewModel dengan data lokasi yang diperbarui
            // Penting untuk memastikan semua data yang relevan dipertahankan saat update
            val currentData = registrationViewModel.currentPregnantMother.value
            registrationViewModel.updatePregnantMotherPart1(
                name = currentData?.name.orEmpty(),
                nik = currentData?.nik.orEmpty(),
                dateOfBirth = currentData?.dateOfBirth.orEmpty(),
                phoneNumber = currentData?.phoneNumber.orEmpty(),
                provinsiName = currentData?.provinsiName,
                provinsiId = currentData?.provinsiId,
                kabupatenName = currentData?.kabupatenName,
                kabupatenId = currentData?.kabupatenId,
                kecamatanName = currentData?.kecamatanName,
                kecamatanId = currentData?.kecamatanId,
                kelurahanName = currentData?.kelurahanName,
                kelurahanId = currentData?.kelurahanId,
                rwName = selectedItem?.name, // Gunakan properti name dari objek Rw
                rwId = selectedItem?.id, // Gunakan properti id dari objek Rw
                rtName = null, // Reset RT jika RW berubah
                rtId = null
            )
            selectedItem?.id?.let { registrationViewModel.getRTS(it) } // Muat RT berdasarkan RW yang dipilih
        }

        // Listener untuk autocomplete RT
        binding.autocompleteRt.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position).toString() // Ambil nama (String)
            // Cari objek Rt yang sesuai dari daftar yang ada di ViewModel
            val selectedItem = registrationViewModel.rts.value?.data?.find { it.name == selectedName }

            selectedRt = selectedItem // Simpan objek Rt yang ditemukan
            // Update ViewModel dengan data lokasi yang diperbarui
            val currentData = registrationViewModel.currentPregnantMother.value
            registrationViewModel.updatePregnantMotherPart1(
                name = currentData?.name.orEmpty(),
                nik = currentData?.nik.orEmpty(),
                dateOfBirth = currentData?.dateOfBirth.orEmpty(),
                phoneNumber = currentData?.phoneNumber.orEmpty(),
                provinsiName = currentData?.provinsiName,
                provinsiId = currentData?.provinsiId,
                kabupatenName = currentData?.kabupatenName,
                kabupatenId = currentData?.kabupatenId,
                kecamatanName = currentData?.kecamatanName,
                kecamatanId = currentData?.kecamatanId,
                kelurahanName = currentData?.kelurahanName,
                kelurahanId = currentData?.kelurahanId,
                rwName = currentData?.rwName, // Pertahankan RW yang sudah dipilih
                rwId = currentData?.rwId,
                rtName = selectedItem?.name, // Gunakan properti name dari objek Rt
                rtId = selectedItem?.id // Gunakan properti id dari objek Rt
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
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun loadFormData() {
        // Amati perubahan pada currentPregnantMother dari ViewModel
        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            pregnantMotherData?.let { data ->
                binding.etName.setText(data.name)
                binding.etNik.setText(data.nik)
                binding.etDateOfBirth.setText(data.dateOfBirth)
                binding.etPhoneNumber.setText(data.phoneNumber)

                // Isi data lokasi read-only
                binding.etProvinsi.setText(data.provinsiName)
                binding.etKabupaten.setText(data.kabupatenName)
                binding.etKecamatanReadonly.setText(data.kecamatanName)
                binding.etKelurahanReadonly.setText(data.kelurahanName)

                // Isi AutoCompleteTextView untuk RW & RT
                binding.autocompleteRw.setText(data.rwName, false)
                binding.autocompleteRt.setText(data.rtName, false)

                // Inisialisasi selectedKelurahan, selectedRw, selectedRt saat data dimuat
                // Ini penting untuk memastikan ID dan nama yang benar digunakan saat menyimpan
                if (data.kelurahanId != null && data.kelurahanName != null && data.kecamatanId != null) {
                    selectedKelurahan = Kelurahan(data.kelurahanId, data.kelurahanName, data.kecamatanId, null)
                    // Pemicu pemuatan RW jika kelurahan sudah ada
                    registrationViewModel.getRWS(data.kelurahanId)
                } else {
                    selectedKelurahan = null
                }
                if (data.rwId != null && data.rwName != null && data.kelurahanId != null) {
                    selectedRw = Rw(data.rwId, data.rwName, data.kelurahanId)
                    // Pemicu pemuatan RT jika RW sudah ada
                    registrationViewModel.getRTS(data.rwId)
                } else {
                    selectedRw = null
                }
                if (data.rtId != null && data.rtName != null && data.rwId != null) {
                    selectedRt = Rt(data.rtId, data.rtName, data.rwId)
                } else {
                    selectedRt = null
                }
            }
        }
    }

    private fun saveFormData() {
        // Ambil data terbaru dari UI
        registrationViewModel.updatePregnantMotherPart1(
            name = binding.etName.text.toString().trim(),
            nik = binding.etNik.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            phoneNumber = binding.etPhoneNumber.text.toString().trim(),
            // Lokasi read-only diambil dari ViewModel, bukan UI langsung (karena diisi oleh VM)
            provinsiName = registrationViewModel.currentPregnantMother.value?.provinsiName,
            provinsiId = registrationViewModel.currentPregnantMother.value?.provinsiId,
            kabupatenName = registrationViewModel.currentPregnantMother.value?.kabupatenName,
            kabupatenId = registrationViewModel.currentPregnantMother.value?.kabupatenId,
            kecamatanName = registrationViewModel.currentPregnantMother.value?.kecamatanName,
            kecamatanId = registrationViewModel.currentPregnantMother.value?.kecamatanId,
            kelurahanName = registrationViewModel.currentPregnantMother.value?.kelurahanName,
            kelurahanId = registrationViewModel.currentPregnantMother.value?.kelurahanId,
            rwName = selectedRw?.name, // Gunakan objek yang dipilih
            rwId = selectedRw?.id,
            rtName = selectedRt?.name, // Gunakan objek yang dipilih
            rtId = selectedRt?.id
        )
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etName.text.isNullOrBlank()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (binding.etNik.text.isNullOrBlank() || binding.etNik.text?.length != 16) {
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

        // Validasi untuk bidang domisili read-only (pastikan terisi dari data user)
        if (binding.etProvinsi.text.isNullOrBlank()) {
            binding.tilProvinsi.error = "Provinsi tidak terisi"
            isValid = false
        } else {
            binding.tilProvinsi.error = null
        }

        if (binding.etKabupaten.text.isNullOrBlank()) {
            binding.tilKabupaten.error = "Kabupaten tidak terisi"
            isValid = false
        } else {
            binding.tilKabupaten.error = null
        }

        if (binding.etKecamatanReadonly.text.isNullOrBlank()) {
            binding.tilKecamatanReadonly.error = "Kecamatan tidak terisi"
            isValid = false
        } else {
            binding.tilKecamatanReadonly.error = null
        }

        if (binding.etKelurahanReadonly.text.isNullOrBlank()) {
            binding.tilKelurahanReadonly.error = "Kelurahan tidak terisi"
            isValid = false
        } else {
            binding.tilKelurahanReadonly.error = null
        }

        // Validasi untuk RW dan RT harus dipilih dari daftar yang tersedia
        val selectedRwName = binding.autocompleteRw.text.toString().trim()
        val currentRwList = registrationViewModel.rws.value?.data ?: emptyList()
        val isRwSelectedValid = currentRwList.any { it.name == selectedRwName } && selectedRwName.isNotBlank()
        if (!isRwSelectedValid) {
            binding.tilRw.error = "Pilih RW yang valid"
            isValid = false
        } else {
            binding.tilRw.error = null
        }

        val selectedRtName = binding.autocompleteRt.text.toString().trim()
        val currentRtList = registrationViewModel.rts.value?.data ?: emptyList()
        val isRtSelectedValid = currentRtList.any { it.name == selectedRtName } && selectedRtName.isNotBlank()
        if (!isRtSelectedValid) {
            binding.tilRt.error = "Pilih RT yang valid"
            isValid = false
        } else {
            binding.tilRt.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        // Observasi daftar Kelurahan dari ViewModel
        registrationViewModel.kelurahans.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Tampilkan indikator loading untuk kelurahan jika diperlukan
                }
                is Resource.Success -> {
                    // Setelah kelurahan dimuat, perbarui selectedKelurahan jika cocok dengan data VM
                    val currentKelurahanId = registrationViewModel.currentPregnantMother.value?.kelurahanId
                    val matchedKelurahan = resource.data?.find { it.id == currentKelurahanId }
                    matchedKelurahan?.let {
                        selectedKelurahan = it
                        binding.etKelurahanReadonly.setText(it.name)
                        // Karena kelurahan sudah di-set dari SharedPrefs, ini hanya memastikan UI dan selected object selaras
                    }
                    // TODO: Jika Anda punya AutoCompleteTextView untuk Kelurahan (yang tidak read-only), set adapter di sini
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Gagal memuat kelurahan: ${resource.message}", Toast.LENGTH_SHORT).show()
                    binding.etKelurahanReadonly.setText("") // Kosongkan jika gagal
                }
            }
        }

        // Observasi daftar RW dari ViewModel
        registrationViewModel.rws.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tilRw.helperText = "Memuat RW..."
                    binding.autocompleteRw.setText("", false)
                    binding.autocompleteRw.setAdapter(null)
                    binding.tilRt.helperText = null
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRw = null
                    selectedRt = null
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

                    // Setel kembali nilai RW dari ViewModel jika ada dan cocok
                    val currentRwId = registrationViewModel.currentPregnantMother.value?.rwId
                    val matchedRw = rws.find { it.id == currentRwId }
                    if (matchedRw != null) {
                        selectedRw = matchedRw
                        binding.autocompleteRw.setText(matchedRw.name, false)
                        // Pemicu muat RT jika RW sudah terpilih
                        matchedRw.id?.let { registrationViewModel.getRTS(it) }
                    } else {
                        // Jika tidak ada RW yang cocok atau kosong, kosongkan RW dan RT
                        binding.autocompleteRw.setText("", false)
                        binding.autocompleteRt.setText("", false)
                        binding.autocompleteRt.setAdapter(null)
                        selectedRw = null
                        selectedRt = null
                    }
                }
                is Resource.Error -> {
                    binding.tilRw.error = "Gagal memuat RW: ${resource.message}"
                    Toast.makeText(context, "Gagal memuat RW: ${resource.message}", Toast.LENGTH_SHORT).show()
                    binding.autocompleteRw.setText("", false)
                    binding.autocompleteRw.setAdapter(null)
                    binding.tilRt.helperText = null
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRw = null
                    selectedRt = null
                }
            }
        }

        // Observasi daftar RT dari ViewModel
        registrationViewModel.rts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tilRt.helperText = "Memuat RT..."
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRt = null
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

                    // Setel kembali nilai RT dari ViewModel jika ada dan cocok
                    val currentRtId = registrationViewModel.currentPregnantMother.value?.rtId
                    val matchedRt = rts.find { it.id == currentRtId }
                    if (matchedRt != null) {
                        selectedRt = matchedRt
                        binding.autocompleteRt.setText(matchedRt.name, false)
                    } else {
                        binding.autocompleteRt.setText("", false)
                        selectedRt = null
                    }
                }
                is Resource.Error -> {
                    binding.tilRt.error = "Gagal memuat RT: ${resource.message}"
                    Toast.makeText(context, "Gagal memuat RT: ${resource.message}", Toast.LENGTH_SHORT).show()
                    binding.autocompleteRt.setText("", false)
                    binding.autocompleteRt.setAdapter(null)
                    selectedRt = null
                }
            }
        }

        // Observasi currentPregnantMother untuk inisialisasi form
        // Ini harus yang pertama diamati untuk memastikan data dasar tersedia
        registrationViewModel.currentPregnantMother.observe(viewLifecycleOwner) { pregnantMotherData ->
            pregnantMotherData?.let { data ->
                // Isi field read-only Provinsi, Kabupaten, Kecamatan
                binding.etProvinsi.setText(data.provinsiName)
                binding.etKabupaten.setText(data.kabupatenName)
                binding.etKecamatanReadonly.setText(data.kecamatanName)
                // Panggil updateKelurahanUI jika kelurahanId di data sudah ada
                data.kecamatanId?.let { updateKelurahanUI(it) } // Ini akan memicu pemuatan kelurahan dan set kelurahanReadonly
            }
        }
    }


    /**
     * Memperbarui UI Kelurahan dan memuat daftar RW/RT berdasarkan ID Kecamatan.
     * Fungsi ini dipanggil dari observeViewModel ketika data `currentPregnantMother` terisi
     * yang akan memberikan `kecamatanId`.
     */
    private fun updateKelurahanUI(kecamatanId: Int?) {
        if (kecamatanId == null) return

        // Panggil untuk memuat kelurahan (ini akan meng-update LiveData kelurahans di ViewModel)
        registrationViewModel.getKelurahans(kecamatanId)
        // Observer untuk kelurahans sudah ada di observeViewModel(), itu yang akan menangani pembaruan UI dan selectedKelurahan
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
