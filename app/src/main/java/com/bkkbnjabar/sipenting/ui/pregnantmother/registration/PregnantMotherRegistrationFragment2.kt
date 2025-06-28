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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration2Binding
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.utils.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PregnantMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    private var pregnantMotherStatusOptions: List<LookupItem> = emptyList()
    private var givenBirthStatusOptions: List<LookupItem> = emptyList()
    private var counselingTypeOptions: List<LookupItem> = emptyList()
    private var deliveryPlaceOptions: List<LookupItem> = emptyList()
    private var birthAssistantOptions: List<LookupItem> = emptyList()
    private var contraceptionOptions: List<LookupItem> = emptyList()
    private var referralStatusOptions: List<String> = emptyList()
    private var socialAssistanceStatusOptions: List<String> = emptyList()
    private var diseaseHistoryOptions: List<LookupItem> = emptyList()
    private var drinkingWaterOptions: List<LookupItem> = emptyList()
    private var defecationFacilityOptions: List<LookupItem> = emptyList()
    private var socialAssistanceOptions: List<LookupItem> = emptyList()

    private var latestTmpUri: Uri? = null
    private var captureRequestIndex: Int = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if (captureRequestIndex > 0) handleImageCapture(captureRequestIndex) else handleLocationCapture()
            } else {
                Toast.makeText(context, "Izin dibutuhkan untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
            }
        }

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                if (captureRequestIndex == 1) {
                    binding.ivPreview1.setImageURI(uri)
                    binding.ivPreview1.tag = uri.toString()
                } else {
                    binding.ivPreview2.setImageURI(uri)
                    binding.ivPreview2.tag = uri.toString()
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
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            saveUIToViewModel()
            viewModel.saveAllData()
        }

        binding.rgIsHbChecked.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = (checkedId == R.id.rb_hb_checked_yes)
            binding.tilHb.isVisible = isChecked
            binding.tilHbReason.isVisible = !isChecked
            if (!isChecked) binding.etHb.text?.clear()
            if (isChecked) binding.etHbReason.text?.clear()
        }
        binding.rgIsTwin.setOnCheckedChangeListener { _, checkedId ->
            binding.tilNumberOfTwins.isVisible = (checkedId == R.id.rb_is_twin_yes)
            if (checkedId != R.id.rb_is_twin_yes) binding.etNumberOfTwins.text?.clear()
        }
        binding.rgIsTbjChecked.setOnCheckedChangeListener { _, checkedId ->
            binding.tilTbj.isVisible = (checkedId == R.id.rb_is_tbj_checked_yes)
            if (checkedId != R.id.rb_is_tbj_checked_yes) binding.etTbj.text?.clear()
        }
        binding.rgTfuStatus.setOnCheckedChangeListener { _, checkedId ->
            binding.tilTfu.isVisible = (checkedId == R.id.rb_tfu_diukur)
            if (checkedId != R.id.rb_tfu_diukur) binding.etTfu.text?.clear()
        }
        binding.etPregnancyWeek.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val week = binding.etPregnancyWeek.text.toString().toIntOrNull() ?: 0
                val isTfuEligible = week >= 20
                binding.tvTfuLabel.isVisible = isTfuEligible
                binding.rgTfuStatus.isVisible = isTfuEligible
                if (!isTfuEligible) {
                    binding.rgTfuStatus.clearCheck()
                    binding.tilTfu.isVisible = false
                }
            }
        }

        binding.btnCapture1.setOnClickListener { handleImageCapture(1) }
        binding.btnCapture2.setOnClickListener { handleImageCapture(2) }
        binding.btnGetLocation.setOnClickListener { handleLocationCapture() }

        binding.etVisitDate.setOnClickListener { showDatePickerDialog(it as TextInputEditText) }
        binding.etDateOfBirthLastChild.setOnClickListener { showDatePickerDialog(it as TextInputEditText) }
        binding.etNextVisitDate.setOnClickListener { showDatePickerDialog(it as TextInputEditText) }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = result is Resource.Loading
            binding.btnSave.isEnabled = result !is Resource.Loading
            binding.btnPrevious.isEnabled = result !is Resource.Loading

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_LONG).show()
                    viewModel.resetForm()
                    findNavController().navigate(R.id.action_pregnantMotherRegistrationFragment2_to_nav_pregnant_mother_list)
                }
                is Resource.Error -> Toast.makeText(context, "Gagal menyimpan: ${result.message}", Toast.LENGTH_LONG).show()
                else -> {}
            }
        }

        viewModel.pregnantMotherStatuses.observe(viewLifecycleOwner) {
            pregnantMotherStatusOptions = it ?: emptyList()
            binding.etPregnantMotherStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pregnantMotherStatusOptions.map { it.name }))
        }
        viewModel.givenBirthStatuses.observe(viewLifecycleOwner) {
            givenBirthStatusOptions = it ?: emptyList()
            binding.etGivenBirthStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, givenBirthStatusOptions.map { it.name }))
        }
        viewModel.counselingTypes.observe(viewLifecycleOwner) {
            counselingTypeOptions = it ?: emptyList()
            binding.etCounselingType.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, counselingTypeOptions.map { it.name }))
        }
        viewModel.deliveryPlaces.observe(viewLifecycleOwner) {
            deliveryPlaceOptions = it ?: emptyList()
            binding.etDeliveryPlace.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, deliveryPlaceOptions.map { it.name }))
        }
        viewModel.birthAssistants.observe(viewLifecycleOwner) {
            birthAssistantOptions = it ?: emptyList()
            binding.etBirthAssistant.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, birthAssistantOptions.map { it.name }))
        }
        viewModel.contraceptionOptions.observe(viewLifecycleOwner) {
            contraceptionOptions = it ?: emptyList()
            binding.etContraceptionOption.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, contraceptionOptions.map { it.name }))
        }
        viewModel.referralStatusOptions.observe(viewLifecycleOwner) {
            referralStatusOptions = it ?: emptyList()
            binding.etFacilitatingReferralService.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, referralStatusOptions))
        }
        viewModel.socialAssistanceStatusOptions.observe(viewLifecycleOwner) {
            socialAssistanceStatusOptions = it ?: emptyList()
            binding.etFacilitatingSocialAssistance.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, socialAssistanceStatusOptions))
        }

        viewModel.diseaseHistories.observe(viewLifecycleOwner) {
            diseaseHistoryOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupDiseaseHistory, diseaseHistoryOptions, listOf("Tidak Ada"), "", null)
        }
        viewModel.mainSourcesOfDrinkingWater.observe(viewLifecycleOwner) {
            drinkingWaterOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupDrinkingWater, drinkingWaterOptions, listOf("Lainnya"), "Lainnya", binding.tilDrinkingWaterOther)
        }
        viewModel.defecationFacilities.observe(viewLifecycleOwner) {
            defecationFacilityOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupDefecationFacility, defecationFacilityOptions, listOf("Tidak ada", "Ya, lainnya"), "Ya, lainnya", binding.tilDefecationFacilityOther)
        }
        viewModel.socialAssistanceOptions.observe(viewLifecycleOwner) {
            socialAssistanceOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupSocialAssistance, socialAssistanceOptions, listOf("Lainnya"), "Lainnya", binding.tilSocialAssistanceOther)
        }
    }

    private fun setupDynamicChips(chipGroup: ChipGroup, options: List<LookupItem>, exclusiveOptions: List<String>, otherOptionName: String, otherInputLayout: TextInputLayout?) {
        chipGroup.removeAllViews()
        options.forEach { item ->
            val chip = Chip(context).apply {
                text = item.name
                isCheckable = true
                id = View.generateViewId()
                setOnClickListener {
                    handleChipClick(this, chipGroup, exclusiveOptions, otherOptionName, otherInputLayout)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun handleChipClick(clickedChip: Chip, chipGroup: ChipGroup, exclusiveOptions: List<String>, otherOptionName: String, otherInputLayout: TextInputLayout?) {
        val clickedText = clickedChip.text.toString()
        val isExclusive = exclusiveOptions.contains(clickedText)

        if (isExclusive) {
            if (clickedChip.isChecked) {
                chipGroup.children.filter { it.id != clickedChip.id }.forEach { (it as Chip).isChecked = false }
            }
        } else {
            if (clickedChip.isChecked) {
                exclusiveOptions.forEach { exclusiveText ->
                    (chipGroup.children.firstOrNull { (it as Chip).text.toString() == exclusiveText } as? Chip)?.isChecked = false
                }
            }
        }

        val isAnyExclusiveSelected = chipGroup.children.any { view ->
            val chip = view as Chip
            exclusiveOptions.contains(chip.text.toString()) && chip.isChecked
        }

        chipGroup.children.forEach { view ->
            val chip = view as Chip
            val isThisChipExclusive = exclusiveOptions.contains(chip.text.toString())
            chip.isEnabled = !isAnyExclusiveSelected || isThisChipExclusive
        }

        otherInputLayout?.isVisible = (chipGroup.children.firstOrNull { view ->
            val chip = view as Chip
            chip.text.toString() == otherOptionName && chip.isChecked
        } != null)
    }

    private fun saveUIToViewModel() {
        fun getSelectedChipTexts(chipGroup: ChipGroup): List<String> = chipGroup.children.filter { (it as Chip).isChecked }.map { (it as Chip).text.toString() }.toList()
        var latitude: Double? = null
        var longitude: Double? = null
        val locationText = binding.tvLocationResult.text.toString()
        if (locationText.startsWith("Lat:")) {
            try {
                val parts = locationText.split(", Long:")
                latitude = parts[0].removePrefix("Lat:").trim().toDouble()
                longitude = parts[1].trim().toDouble()
            } catch (e: Exception) {
                // Could not parse, latitude and longitude will remain null
            }
        }
        viewModel.updatePregnantMotherVisitData(
            visitDate = binding.etVisitDate.text.toString().trim(),
            childNumber = binding.etChildNumber.text.toString().toIntOrNull(),
            dateOfBirthLastChild = binding.etDateOfBirthLastChild.text.toString().trim(),
            pregnancyWeekAge = binding.etPregnancyWeek.text.toString().toIntOrNull(),
            weightTrimester1 = binding.etWeightTrimester1.text.toString().toDoubleOrNull(),
            currentHeight = binding.etCurrentHeight.text.toString().toDoubleOrNull(),
            currentWeight = binding.etCurrentWeight.text.toString().toDoubleOrNull(),
            isHbChecked = binding.rgIsHbChecked.checkedRadioButtonId == R.id.rb_hb_checked_yes,
            hemoglobinLevel = binding.etHb.text.toString().toDoubleOrNull(),
            hemoglobinLevelReason = binding.etHbReason.text.toString().trim(),
            upperArmCircumference = binding.etLila.text.toString().toDoubleOrNull(),
            isTwin = binding.rgIsTwin.checkedRadioButtonId == R.id.rb_is_twin_yes,
            numberOfTwins = binding.etNumberOfTwins.text.toString().toIntOrNull(),
            isEstimatedFetalWeightChecked = binding.rgIsTbjChecked.checkedRadioButtonId == R.id.rb_is_tbj_checked_yes,
            tbj = binding.etTbj.text.toString().toDoubleOrNull(),
            isExposedToCigarettes = binding.rgIsExposedToSmoke.checkedRadioButtonId == R.id.rb_is_exposed_to_smoke_yes,
            isCounselingReceived = binding.rgIsCounselingReceived.checkedRadioButtonId == R.id.rb_is_counseling_received_yes,
            isIronTablesReceived = binding.rgIsIronReceived.checkedRadioButtonId == R.id.rb_is_iron_received_yes,
            isIronTablesTaken = binding.rgIsIronTaken.checkedRadioButtonId == R.id.rb_is_iron_taken_yes,
            nextVisitDate = binding.etNextVisitDate.text.toString().trim(),
            tpkNotes = binding.etTpkNotes.text.toString().trim(),
            isAlive = binding.rgIsAlive.checkedRadioButtonId == R.id.rb_is_alive_yes,
            isGivenBirth = binding.rgIsGivenBirth.checkedRadioButtonId == R.id.rb_is_given_birth_yes,
            tfu = binding.etTfu.text.toString().toDoubleOrNull(),
            isReceivedMbg = binding.rgIsMbgReceived.checkedRadioButtonId == R.id.rb_is_mbg_received_yes,
            isTfuMeasured = binding.rgTfuStatus.checkedRadioButtonId == R.id.rb_tfu_diukur,
            pregnantMotherStatusId = pregnantMotherStatusOptions.find { it.name == binding.etPregnantMotherStatus.text.toString() }?.id,
            givenBirthStatusId = givenBirthStatusOptions.find { it.name == binding.etGivenBirthStatus.text.toString() }?.id,
            counselingTypeId = counselingTypeOptions.find { it.name == binding.etCounselingType.text.toString() }?.id,
            deliveryPlaceId = deliveryPlaceOptions.find { it.name == binding.etDeliveryPlace.text.toString() }?.id,
            birthAssistantId = birthAssistantOptions.find { it.name == binding.etBirthAssistant.text.toString() }?.id,
            contraceptionOptionId = contraceptionOptions.find { it.name == binding.etContraceptionOption.text.toString() }?.id,
            facilitatingReferralServiceStatus = binding.etFacilitatingReferralService.text.toString(),
            facilitatingSocialAssistanceStatus = binding.etFacilitatingSocialAssistance.text.toString(),
            diseaseHistory = getSelectedChipTexts(binding.chipGroupDiseaseHistory),
            mainSourceOfDrinkingWater = getSelectedChipTexts(binding.chipGroupDrinkingWater),
            mainSourceOfDrinkingWaterOther = binding.etDrinkingWaterOther.text.toString().trim(),
            defecationFacility = getSelectedChipTexts(binding.chipGroupDefecationFacility),
            defecationFacilityOther = binding.etDefecationFacilityOther.text.toString().trim(),
            socialAssistanceFacilitationOptions = getSelectedChipTexts(binding.chipGroupSocialAssistance),
            socialAssistanceFacilitationOptionsOther = binding.etSocialAssistanceOther.text.toString().trim(),
            imagePath1 = binding.ivPreview1.tag as? String,
            imagePath2 = binding.ivPreview2.tag as? String,
            latitude = latitude,
            longitude = longitude
        )
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
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun handleLocationCapture() {
        captureRequestIndex = 0
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> getCurrentLocation()
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("sipenting_${System.currentTimeMillis()}", ".png", requireActivity().cacheDir).apply { createNewFile(); deleteOnExit() }
        return FileProvider.getUriForFile(requireActivity(), "${requireActivity().packageName}.provider", tmpFile)
    }

    private fun getCurrentLocation() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    binding.tvLocationResult.text = String.format(Locale.US, "Lat: %.6f, Long: %.6f", location.latitude, location.longitude)
                } else {
                    Toast.makeText(context, "Tidak bisa mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Izin lokasi tidak diberikan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog(editText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(editText.hint.toString()).build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            editText.setText(sdf.format(Date(selection)))
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG_${editText.id}")
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