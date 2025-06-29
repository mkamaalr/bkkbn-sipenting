package com.bkkbnjabar.sipenting.ui.breastfeedingmother.edit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.databinding.FragmentBreastfeedingMotherVisitEditBinding
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.ui.breastfeedingmother.registration.BreastfeedingMotherRegistrationViewModel
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
class BreastfeedingMotherVisitEditFragment : Fragment() {

    private var _binding: FragmentBreastfeedingMotherVisitEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreastfeedingMotherRegistrationViewModel by activityViewModels()
    private val args: BreastfeedingMotherVisitEditFragmentArgs by navArgs()

    private var isFormPopulated = false

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
        _binding = FragmentBreastfeedingMotherVisitEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupListeners()
        viewModel.loadVisitForEditing(args.visitId)
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            saveUIToViewModel()
            viewModel.saveAllData()
        }

        binding.rgIsCounselingReceived.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = (checkedId == R.id.rb_is_counseling_received_yes)
            binding.tilCounselingType.isVisible = isChecked
            if (!isChecked) {
                // Clear the selection if user chooses "Tidak"
                binding.etCounselingType.setText("", false)
            }
        }

        binding.btnCapture1.setOnClickListener { handleImageCapture(1) }
        binding.btnCapture2.setOnClickListener { handleImageCapture(2) }
        binding.btnGetLocation.setOnClickListener { handleLocationCapture() }

        setupDateField(binding.tilVisitDate, binding.etVisitDate)
        setupDateField(binding.tilNextVisitDate, binding.etNextVisitDate)
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = result is Resource.Loading
            binding.btnSave.isEnabled = result !is Resource.Loading
            binding.btnPrevious.isEnabled = result !is Resource.Loading

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, "Data berhasil diperbarui!", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> Toast.makeText(context, "Gagal menyimpan: ${result.message}", Toast.LENGTH_LONG).show()
                else -> {}
            }
        }

        viewModel.pregnantMotherStatuses.observe(viewLifecycleOwner) {
            pregnantMotherStatusOptions = it ?: emptyList()
            binding.etBreastfeedingMotherStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pregnantMotherStatusOptions.map { it.name }))
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

        viewModel.currentBreastFeedingMotherVisit.observe(viewLifecycleOwner) { visitData ->
            if (visitData != null && visitData.localVisitId == args.visitId && !isFormPopulated) {
                updateFormFromData(visitData)
                isFormPopulated = true
            }
        }
    }

    private fun updateFormFromData(data: BreastfeedingMotherVisitData?) {
        if (data == null) return

//        binding.etVisitDate.setText(data.visitDate)
//        binding.etChildNumber.setText(data.childNumber?.toString() ?: "")
//        binding.etDateOfBirthLastChild.setText(data.dateOfBirthLastChild)
//        binding.etPregnancyWeek.setText(data.pregnancyWeekAge?.toString() ?: "")
//        binding.etWeightTrimester1.setText(data.weightTrimester1?.toString() ?: "")
//        binding.etCurrentWeight.setText(data.currentWeight?.toString() ?: "")
//        binding.etCurrentHeight.setText(data.currentHeight?.toString() ?: "")
//        binding.etLila.setText(data.upperArmCircumference?.toString() ?: "")
//        binding.etHb.setText(data.hemoglobinLevel?.toString() ?: "")
//        binding.etHbReason.setText(data.hemoglobinLevelReason)
//        binding.etNumberOfTwins.setText(data.numberOfTwins?.toString() ?: "")
//        binding.etTbj.setText(data.tbj?.toString() ?: "")
//        binding.etTpkNotes.setText(data.tpkNotes)
//        binding.etNextVisitDate.setText(data.nextVisitDate)
//        binding.etTfu.setText(data.tfu?.toString() ?: "")
//        binding.tilGivenBirthStatus.isVisible = (data.isGivenBirth == true)
//
//        binding.rgIsAlive.check(if (data.isAlive == true) R.id.rb_is_alive_yes else R.id.rb_is_alive_no)
//        binding.rgIsGivenBirth.check(if (data.isGivenBirth == true) R.id.rb_is_given_birth_yes else R.id.rb_is_given_birth_no)
//        binding.rgIsHbChecked.check(if (data.isHbChecked == true) R.id.rb_hb_checked_yes else R.id.rb_hb_checked_no)
//        binding.rgIsTwin.check(if (data.isTwin == true) R.id.rb_is_twin_yes else R.id.rb_is_twin_no)
//        binding.rgIsTbjChecked.check(if (data.isEstimatedFetalWeightChecked == true) R.id.rb_is_tbj_checked_yes else R.id.rb_is_tbj_checked_no)
//        binding.rgIsCounselingReceived.check(if (data.isCounselingReceived == true) R.id.rb_is_counseling_received_yes else R.id.rb_is_counseling_received_no)
//        binding.tilCounselingType.isVisible = data.isCounselingReceived == true
//
//        binding.rgIsIronReceived.check(if (data.isIronTablesReceived == true) R.id.rb_is_iron_received_yes else R.id.rb_is_iron_received_no)
//        binding.rgIsIronTaken.check(if (data.isIronTablesTaken == true) R.id.rb_is_iron_taken_yes else R.id.rb_is_iron_taken_no)
//        binding.rgIsExposedToSmoke.check(if (data.isExposedToCigarettes == true) R.id.rb_is_exposed_to_smoke_yes else R.id.rb_is_exposed_to_smoke_no)
//        binding.rgIsMbgReceived.check(if (data.isReceivedMbg == true) R.id.rb_is_mbg_received_yes else R.id.rb_is_mbg_received_no)
//        binding.rgTfuStatus.check(if (data.isTfuMeasured == true) R.id.rb_tfu_diukur else R.id.rb_tfu_tidak_diukur)
//        binding.etBreastfeedingMotherStatus.setText(pregnantMotherStatusOptions.find { it.id == data.pregnantMotherStatusId }?.name ?: "", false)
//        binding.tilGivenBirthStatus.isVisible = (data.isGivenBirth == true)
//        binding.etGivenBirthStatus.setText(givenBirthStatusOptions.find { it.id == data.givenBirthStatusId }?.name ?: "", false)
//        binding.etCounselingType.setText(counselingTypeOptions.find { it.id == data.counselingTypeId }?.name ?: "", false)
//        binding.etDeliveryPlace.setText(deliveryPlaceOptions.find { it.id == data.deliveryPlaceId }?.name ?: "", false)
//        binding.etBirthAssistant.setText(birthAssistantOptions.find { it.id == data.birthAssistantId }?.name ?: "", false)
//        binding.etContraceptionOption.setText(contraceptionOptions.find { it.id == data.contraceptionOptionId }?.name ?: "", false)
//        binding.etFacilitatingReferralService.setText(data.facilitatingReferralServiceStatus ?: "", false)
//        binding.etFacilitatingSocialAssistance.setText(data.facilitatingSocialAssistanceStatus ?: "", false)
//
//        updateChipGroupState(binding.chipGroupDiseaseHistory, data.diseaseHistory ?: emptyList(), listOf("Tidak Ada"))
//        updateChipGroupState(binding.chipGroupDrinkingWater, data.mainSourceOfDrinkingWater ?: emptyList(), listOf("Lainnya"))
//        updateChipGroupState(binding.chipGroupDefecationFacility, data.defecationFacility ?: emptyList(), listOf("Tidak ada", "Ya, lainnya"))
//        updateChipGroupState(binding.chipGroupSocialAssistance, data.socialAssistanceFacilitationOptions ?: emptyList(), listOf("Lainnya"))
//
//        binding.etDrinkingWaterOther.setText(data.mainSourceOfDrinkingWaterOther)
//        binding.etDefecationFacilityOther.setText(data.defecationFacilityOther)
//        binding.etSocialAssistanceOther.setText(data.socialAssistanceFacilitationOptionsOther)
//
//        data.imagePath1?.let { binding.ivPreview1.tag = it; binding.ivPreview1.setImageURI(Uri.parse(it)) }
//        data.imagePath2?.let { binding.ivPreview2.tag = it; binding.ivPreview2.setImageURI(Uri.parse(it)) }
//        if (data.latitude != null && data.longitude != null) {
//            binding.tvLocationResult.text = String.format(Locale.US, "Lat: %.6f, Long: %.6f", data.latitude, data.longitude)
//        } else {
//            binding.tvLocationResult.text = ""
//        }
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
            } catch (_: Exception) { /* Do nothing, leave as null */ }
        }

        viewModel.updateBreastfeedingMotherVisitData(
            visitDate = binding.etVisitDate.text.toString().trim(),
            currentHeight = binding.etCurrentHeight.text.toString().toDoubleOrNull(),
            currentWeight = binding.etCurrentWeight.text.toString().toDoubleOrNull(),
            isTwin = binding.rgIsTwin.checkedRadioButtonId == R.id.rb_is_twin_yes,
            isExposedToCigarettes = binding.rgIsExposedToSmoke.checkedRadioButtonId == R.id.rb_is_exposed_to_smoke_yes,
            isCounselingReceived = binding.rgIsCounselingReceived.checkedRadioButtonId == R.id.rb_is_counseling_received_yes,
            isIronTablesReceived = binding.rgIsIronReceived.checkedRadioButtonId == R.id.rb_is_iron_received_yes,
            isIronTablesTaken = binding.rgIsIronTaken.checkedRadioButtonId == R.id.rb_is_iron_taken_yes,
            nextVisitDate = binding.etNextVisitDate.text.toString().trim(),
            tpkNotes = binding.etTpkNotes.text.toString().trim(),
            isAlive = binding.rgIsAlive.checkedRadioButtonId == R.id.rb_is_alive_yes,
            isReceivedMbg = binding.rgIsMbgReceived.checkedRadioButtonId == R.id.rb_is_mbg_received_yes,
            isAsiExclusive = binding.rgIsAsiExclusive.checkedRadioButtonId == R.id.rb_is_asi_exclusive_yes,
            counselingTypeId = counselingTypeOptions.find { it.name == binding.etCounselingType.text.toString() }?.id,
            deliveryPlaceId = deliveryPlaceOptions.find { it.name == binding.etDeliveryPlace.text.toString() }?.id,
            birthAssistantId = birthAssistantOptions.find { it.name == binding.etBirthAssistant.text.toString() }?.id,
            contraceptionOptionId = contraceptionOptions.find { it.name == binding.etContraceptionOption.text.toString() }?.id,
            facilitatingReferralServiceStatus = binding.etFacilitatingReferralService.text.toString(),
            facilitatingSocialAssistanceStatus = binding.etFacilitatingSocialAssistance.text.toString(),
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

    private fun updateChipGroupState(
        chipGroup: ChipGroup,
        selectedItems: List<String>,
        exclusiveOptions: List<String>
    ) {
        // This is the variable you asked about. It checks if any of the selected items
        // is one of the "exclusive" ones (like "Tidak Ada" or "Lainnya").
        val isAnExclusiveItemSelected = selectedItems.any { exclusiveOptions.contains(it) }

        // Loop through every chip in the group
        chipGroup.children.forEach { view ->
            val chip = view as? Chip ?: return@forEach // Safe cast to Chip

            // 1. Set the checked state
            val isThisChipSelected = selectedItems.contains(chip.text.toString())
            if (chip.isChecked != isThisChipSelected) {
                chip.isChecked = isThisChipSelected
            }

            // 2. Set the enabled state
            val isThisChipExclusive = exclusiveOptions.contains(chip.text.toString())
            // A chip is enabled only if no exclusive option is selected,
            // OR if it is the exclusive option that is currently selected.
            chip.isEnabled = !isAnExclusiveItemSelected || isThisChipExclusive
        }

        // 3. Handle visibility of the "Other" text input, if it exists for this group
        val otherInputLayout = when (chipGroup.id) {
            R.id.chip_group_drinking_water -> binding.tilDrinkingWaterOther
            R.id.chip_group_defecation_facility -> binding.tilDefecationFacilityOther
            R.id.chip_group_social_assistance -> binding.tilSocialAssistanceOther
            else -> null
        }
        val otherOptionName = when (chipGroup.id) {
            R.id.chip_group_drinking_water -> "Lainnya"
            R.id.chip_group_defecation_facility -> "Ya, lainnya"
            R.id.chip_group_social_assistance -> "Lainnya"
            else -> ""
        }

        if (otherOptionName.isNotBlank()) {
            otherInputLayout?.isVisible = selectedItems.contains(otherOptionName)
        }
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
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(editText.hint)
            .build()

        // This check prevents a crash if the user clicks the icon very rapidly
        if (datePicker.isAdded) {
            return
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val dateString = sdf.format(Date(selection))
            editText.setText(dateString)
        }

        datePicker.show(parentFragmentManager, editText.id.toString())
    }

    private fun setupDateField(textInputLayout: TextInputLayout, editText: TextInputEditText) {
        // This makes the entire layout box clickable to show the picker
        textInputLayout.setOnClickListener {
            showDatePickerDialog(editText)
        }

        // This makes the EditText itself also clickable
        editText.setOnClickListener {
            showDatePickerDialog(editText)
        }

        // This listener handles the action of the icon at the end of the field
        textInputLayout.setEndIconOnClickListener {
            // If the field has text, the icon is a 'clear' button. Clear the text.
            if (editText.text.toString().isNotEmpty()) {
                editText.text = null
            }
            // Otherwise, the icon is a calendar. Show the picker.
            else {
                showDatePickerDialog(editText)
            }
        }

        // This watcher intelligently swaps the icon based on whether there is text
        editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s.isNullOrBlank()) {
                    textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar)
                } else {
                    textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_text)
                }
            }
        })

        // Set the initial icon state
        if (editText.text.isNullOrBlank()) {
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar)
        } else {
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetForm()
        _binding = null
    }
}