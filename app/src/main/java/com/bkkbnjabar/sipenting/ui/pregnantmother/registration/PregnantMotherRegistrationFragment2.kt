package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration2Binding
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.utils.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.io.File
import androidx.core.content.FileProvider
import java.util.*

@AndroidEntryPoint
class PregnantMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    // Properti lokal untuk menyimpan daftar opsi setelah dimuat dari ViewModel
    private var diseaseHistoryOptions: List<LookupItem> = emptyList()
    private var drinkingWaterOptions: List<LookupItem> = emptyList()
    private var defecationFacilityOptions: List<LookupItem> = emptyList()
    private var socialAssistanceOptions: List<LookupItem> = emptyList()

    private var imageUri1: Uri? = null
    private var imageUri2: Uri? = null
    private var latestTmpUri: Uri? = null
    private var captureRequestIndex: Int = 0

    // Launcher untuk meminta izin
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Jika izin diberikan, panggil kembali fungsi yang memintanya
                when {
                    captureRequestIndex > 0 -> handleImageCapture(captureRequestIndex)
                    else -> handleLocationCapture()
                }
            } else {
                Toast.makeText(context, "Izin dibutuhkan untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher untuk mengambil gambar dari kamera
    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                if (captureRequestIndex == 1) {
                    imageUri1 = uri
                    binding.ivPreview1.setImageURI(uri)
                    viewModel.updatePregnantMotherVisitData(imagePath1 = uri.path)
                } else {
                    imageUri2 = uri
                    binding.ivPreview2.setImageURI(uri)
                    viewModel.updatePregnantMotherVisitData(imagePath2 = uri.path)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPregnantMotherRegistration2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set tombol multi-select tidak aktif di awal
        binding.btnSelectDiseaseHistory.isEnabled = false
        binding.btnSelectDrinkingWater.isEnabled = false
        binding.btnSelectDefecationFacility.isEnabled = false
        binding.btnSelectSocialAssistance.isEnabled = false
        setupListeners()
        observeViewModel()
        loadDataFromViewModel()
    }


    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            saveDataToViewModel()
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            saveDataToViewModel()
            viewModel.saveAllData()
        }

        binding.etFacilitatingReferralService.setOnItemClickListener { _, _, position, _ ->
            val selected = binding.etFacilitatingReferralService.adapter.getItem(position) as String
            viewModel.updatePregnantMotherVisitData(facilitatingReferralServiceStatus = selected)
        }
        binding.etFacilitatingSocialAssistance.setOnItemClickListener { _, _, position, _ ->
            val selected = binding.etFacilitatingSocialAssistance.adapter.getItem(position) as String
            viewModel.updatePregnantMotherVisitData(facilitatingSocialAssistanceStatus = selected)
        }

        // Listeners untuk Date Picker
        binding.etVisitDate.setOnClickListener { showDatePickerDialog(it as TextInputEditText, "Pilih Tanggal Kunjungan") }
        binding.etDateOfBirthLastChild.setOnClickListener { showDatePickerDialog(it as TextInputEditText, "Pilih Tgl Lahir Anak Terakhir") }
        binding.etNextVisitDate.setOnClickListener { showDatePickerDialog(it as TextInputEditText, "Pilih Tgl Kunjungan Berikutnya") }

        // Listener untuk checkbox Hamil Kembar
        binding.cbIsTwin.setOnCheckedChangeListener { _, isChecked ->
            binding.tilNumberOfTwins.isVisible = isChecked
            if (!isChecked) {
                binding.etNumberOfTwins.setText("")
            }
        }

        // Listeners untuk semua dropdown
        setupDropdownListener(binding.etPregnantMotherStatus) { viewModel.updatePregnantMotherVisitData(pregnantMotherStatusId = it.id) }
        setupDropdownListener(binding.etGivenBirthStatus) { viewModel.updatePregnantMotherVisitData(givenBirthStatusId = it.id) }
        setupDropdownListener(binding.etCounselingType) { viewModel.updatePregnantMotherVisitData(counselingTypeId = it.id) }
        setupDropdownListener(binding.etDeliveryPlace) { viewModel.updatePregnantMotherVisitData(deliveryPlaceId = it.id) }
        setupDropdownListener(binding.etBirthAssistant) { viewModel.updatePregnantMotherVisitData(birthAssistantId = it.id) }
        setupDropdownListener(binding.etContraceptionOption) { viewModel.updatePregnantMotherVisitData(contraceptionOptionId = it.id) }

        // ================== LOGIKA LISTENER DIPERBAIKI ==================
        binding.btnSelectDiseaseHistory.setOnClickListener {
            // Gunakan properti lokal yang sudah diisi oleh observer
            showMultiSelectDialog(
                "Pilih Riwayat Penyakit",
                diseaseHistoryOptions,
                viewModel.currentPregnantMotherVisit.value?.diseaseHistory ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(diseaseHistory = selectedItems)
            }
        }
        binding.btnSelectDrinkingWater.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Sumber Air Minum",
                drinkingWaterOptions,
                viewModel.currentPregnantMotherVisit.value?.mainSourceOfDrinkingWater ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(mainSourceOfDrinkingWater = selectedItems)
            }
        }
        binding.btnSelectDefecationFacility.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Fasilitas Jamban",
                defecationFacilityOptions,
                viewModel.currentPregnantMotherVisit.value?.defecationFacility ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(defecationFacility = selectedItems)
            }
        }
        binding.btnSelectSocialAssistance.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Bantuan Sosial",
                socialAssistanceOptions,
                viewModel.currentPregnantMotherVisit.value?.socialAssistanceFacilitationOptions ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(socialAssistanceFacilitationOptions = selectedItems)
            }
        }

        binding.rgTfuStatus.setOnCheckedChangeListener { _, checkedId ->
            val isMeasured = checkedId == R.id.rb_tfu_diukur
            binding.tilTfu.isVisible = isMeasured
            viewModel.updatePregnantMotherVisitData(isTfuMeasured = isMeasured)
            if (!isMeasured) {
                binding.etTfu.setText("") // Kosongkan nilai jika "Tidak Diukur"
            }
        }

        binding.btnCapture1.setOnClickListener { handleImageCapture(1) }
        binding.btnCapture2.setOnClickListener { handleImageCapture(2) }
        binding.btnGetLocation.setOnClickListener { handleLocationCapture() }
    }

    private fun handleImageCapture(imageIndex: Int) {
        captureRequestIndex = imageIndex
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                getTmpFileUri().let { uri ->
                    latestTmpUri = uri
                    takeImageLauncher.launch(uri)
                }
            }
            else -> {
                // Minta izin kamera
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handleLocationCapture() {
        captureRequestIndex = 0 // Reset index
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                // Minta izin lokasi
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("sipenting_${System.currentTimeMillis()}", ".png", requireActivity().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(requireActivity(), "${requireActivity().packageName}.provider", tmpFile)
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val long = location.longitude
                    binding.tvLocationResult.text = String.format(Locale.US, "Lat: %.6f, Long: %.6f", lat, long)
                    viewModel.updatePregnantMotherVisitData(latitude = lat, longitude = long)
                } else {
                    Toast.makeText(context, "Tidak bisa mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Izin lokasi tidak diberikan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDropdownListener(view: AutoCompleteTextView, onSelect: (LookupItem) -> Unit) {
        view.setOnItemClickListener { _, _, position, _ ->
            (view.adapter.getItem(position) as? LookupItem)?.let(onSelect)
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            val isLoading = result is Resource.Loading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
            binding.btnPrevious.isEnabled = !isLoading

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_LONG).show()
                    viewModel.resetForm()
                    findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment2_to_nav_pregnant_mother_list)
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Gagal menyimpan: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.referralStatusOptions.observe(viewLifecycleOwner) { options ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
            binding.etFacilitatingReferralService.setAdapter(adapter)
        }
        viewModel.socialAssistanceStatusOptions.observe(viewLifecycleOwner) { options ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
            binding.etFacilitatingSocialAssistance.setAdapter(adapter)
        }

        // Observers untuk mengisi adapter dropdown
        viewModel.pregnantMotherStatuses.observe(viewLifecycleOwner) { setAdapter(binding.etPregnantMotherStatus, it) }
        viewModel.givenBirthStatuses.observe(viewLifecycleOwner) { setAdapter(binding.etGivenBirthStatus, it) }
        viewModel.counselingTypes.observe(viewLifecycleOwner) { setAdapter(binding.etCounselingType, it) }
        viewModel.deliveryPlaces.observe(viewLifecycleOwner) { setAdapter(binding.etDeliveryPlace, it) }
        viewModel.birthAssistants.observe(viewLifecycleOwner) { setAdapter(binding.etBirthAssistant, it) }
        viewModel.contraceptionOptions.observe(viewLifecycleOwner) { setAdapter(binding.etContraceptionOption, it) }

        // ================== LOGIKA OBSERVER DIPERBAIKI ==================
        // Observer untuk data multi-select: simpan ke properti lokal dan aktifkan tombol saat data datang
        viewModel.diseaseHistories.observe(viewLifecycleOwner) { options ->
            diseaseHistoryOptions = options ?: emptyList()
            binding.btnSelectDiseaseHistory.isEnabled = diseaseHistoryOptions.isNotEmpty()
        }
        viewModel.mainSourcesOfDrinkingWater.observe(viewLifecycleOwner) { options ->
            drinkingWaterOptions = options ?: emptyList()
            binding.btnSelectDrinkingWater.isEnabled = options.isNotEmpty()
        }
        viewModel.defecationFacilities.observe(viewLifecycleOwner) { options ->
            defecationFacilityOptions = options ?: emptyList()
            binding.btnSelectDefecationFacility.isEnabled = options.isNotEmpty()
        }
        viewModel.socialAssistanceOptions.observe(viewLifecycleOwner) { options ->
            socialAssistanceOptions = options ?: emptyList()
            binding.btnSelectSocialAssistance.isEnabled = options.isNotEmpty()
        }

        // Observer untuk mengupdate tampilan ChipGroup setiap kali data pilihan berubah
        viewModel.currentPregnantMotherVisit.observe(viewLifecycleOwner) { visitData ->
            if (visitData != null) {
                updateChipGroup(binding.chipGroupDiseaseHistory, visitData.diseaseHistory)
                updateChipGroup(binding.chipGroupDrinkingWater, visitData.mainSourceOfDrinkingWater)
                updateChipGroup(binding.chipGroupDefecationFacility, visitData.defecationFacility)
                updateChipGroup(binding.chipGroupSocialAssistance, visitData.socialAssistanceFacilitationOptions)

                // Tampilkan/sembunyikan seluruh bagian TFU berdasarkan usia kehamilan
                val isTfuEligible = (visitData.pregnancyWeekAge ?: 0) >= 20
                binding.tvTfuLabel.isVisible = isTfuEligible
                binding.rgTfuStatus.isVisible = isTfuEligible

                // Jika tidak eligible, pastikan input TFU juga tersembunyi
                if (!isTfuEligible) {
                    binding.tilTfu.isVisible = false
                }
            }
        }
    }

    private fun setAdapter(view: AutoCompleteTextView, items: List<LookupItem>?) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items ?: emptyList())
        view.setAdapter(adapter)
    }

    private fun loadDataFromViewModel() {
        viewModel.currentPregnantMotherVisit.value?.let { data ->
            binding.etVisitDate.setText(data.visitDate)
            binding.etChildNumber.setText(data.childNumber?.toString() ?: "")
            binding.etDateOfBirthLastChild.setText(data.dateOfBirthLastChild)
            binding.etPregnancyWeek.setText(data.pregnancyWeekAge?.toString() ?: "")
            binding.etWeightTrimester1.setText(data.weightTrimester1?.toString() ?: "")
            binding.etCurrentWeight.setText(data.currentWeight?.toString() ?: "")
            binding.etCurrentHeight.setText(data.currentHeight?.toString() ?: "")
            binding.etLila.setText(data.upperArmCircumference?.toString() ?: "")
            binding.etHb.setText(data.hemoglobinLevel?.toString() ?: "")
            binding.etNumberOfTwins.setText(data.numberOfTwins?.toString() ?: "")
            binding.etTpkNotes.setText(data.tpkNotes)
            binding.etNextVisitDate.setText(data.nextVisitDate)
            binding.etFacilitatingReferralService.setText(data.facilitatingReferralServiceStatus ?: "", false)
            binding.etFacilitatingSocialAssistance.setText(data.facilitatingSocialAssistanceStatus ?: "", false)

            binding.cbIsAlive.isChecked = data.isAlive ?: true
            binding.cbIsGivenBirth.isChecked = data.isGivenBirth ?: false
            binding.cbIsHbChecked.isChecked = data.isHbChecked ?: false
            binding.cbIsTwin.isChecked = data.isTwin ?: false
            binding.tilNumberOfTwins.isVisible = data.isTwin ?: false
            binding.cbIsEstimatedFetalWeightChecked.isChecked = data.isEstimatedFetalWeightChecked ?: false
            binding.cbIsCounselingReceived.isChecked = data.isCounselingReceived ?: false
            binding.cbIsIronTablesReceived.isChecked = data.isIronTablesReceived ?: false
            binding.cbIsIronTablesTaken.isChecked = data.isIronTablesTaken ?: false
            binding.cbIsExposedToCigarettes.isChecked = data.isExposedToCigarettes ?: false
            binding.etTfu.setText(data.tfu?.toString() ?: "")
            binding.cbIsReceivedMbg.isChecked = data.isReceivedMbg ?: false
        }

        viewModel.currentPregnantMotherVisit.value?.let { data ->
            data.imagePath1?.let {
                val uri = Uri.parse(it)
                binding.ivPreview1.setImageURI(uri)
            }
            data.imagePath2?.let {
                val uri = Uri.parse(it)
                binding.ivPreview2.setImageURI(uri)
            }
            if (data.latitude != null && data.longitude != null) {
                binding.tvLocationResult.text = String.format(Locale.US, "Lat: %.6f, Long: %.6f", data.latitude, data.longitude)
            }
        }
    }

    private fun saveDataToViewModel() {
        viewModel.updatePregnantMotherVisitData(
            visitDate = binding.etVisitDate.text.toString().trim(),
            childNumber = binding.etChildNumber.text.toString().toIntOrNull(),
            dateOfBirthLastChild = binding.etDateOfBirthLastChild.text.toString().trim(),
            pregnancyWeekAge = binding.etPregnancyWeek.text.toString().toIntOrNull(),
            weightTrimester1 = binding.etWeightTrimester1.text.toString().toDoubleOrNull(),
            currentHeight = binding.etCurrentHeight.text.toString().toDoubleOrNull(),
            currentWeight = binding.etCurrentWeight.text.toString().toDoubleOrNull(),
            isHbChecked = binding.cbIsHbChecked.isChecked,
            hemoglobinLevel = binding.etHb.text.toString().toDoubleOrNull(),
            upperArmCircumference = binding.etLila.text.toString().toDoubleOrNull(),
            isTwin = binding.cbIsTwin.isChecked,
            numberOfTwins = binding.etNumberOfTwins.text.toString().toIntOrNull(),
            isEstimatedFetalWeightChecked = binding.cbIsEstimatedFetalWeightChecked.isChecked,
            isExposedToCigarettes = binding.cbIsExposedToCigarettes.isChecked,
            isCounselingReceived = binding.cbIsCounselingReceived.isChecked,
            isIronTablesReceived = binding.cbIsIronTablesReceived.isChecked,
            isIronTablesTaken = binding.cbIsIronTablesTaken.isChecked,
            nextVisitDate = binding.etNextVisitDate.text.toString().trim(),
            tpkNotes = binding.etTpkNotes.text.toString().trim(),
            isAlive = binding.cbIsAlive.isChecked,
            isGivenBirth = binding.cbIsGivenBirth.isChecked,
            tfu = binding.etTfu.text.toString().toDoubleOrNull(),
            isReceivedMbg = binding.cbIsReceivedMbg.isChecked
        )
    }

    private fun showMultiSelectDialog(
        title: String,
        options: List<LookupItem>,
        preselectedItems: List<String>,
        onConfirm: (List<String>) -> Unit
    ) {
        val itemsArray = options.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(itemsArray.size) {
            preselectedItems.contains(itemsArray[it])
        }
        val selectedList = preselectedItems.toMutableList()

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMultiChoiceItems(itemsArray, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    if (!selectedList.contains(itemsArray[which])) {
                        selectedList.add(itemsArray[which])
                    }
                } else {
                    selectedList.remove(itemsArray[which])
                }
            }
            .setPositiveButton("OK") { dialog, _ ->
                onConfirm(selectedList)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Fungsi ini menggambar Chip berdasarkan daftar string yang dipilih.
     */
    private fun updateChipGroup(chipGroup: ChipGroup, selectedItems: List<String>?) {
        // Hapus semua chip lama sebelum menggambar yang baru
        chipGroup.removeAllViews()
        selectedItems?.forEach { item ->
            val chip = Chip(context).apply {
                text = item
                isClickable = false // Chip hanya untuk display
                isCheckable = false
            }
            chipGroup.addView(chip)
        }
    }

    private fun showDatePickerDialog(editText: TextInputEditText, title: String) {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            editText.setText(sdf.format(Date(selection)))
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG_${editText.id}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
