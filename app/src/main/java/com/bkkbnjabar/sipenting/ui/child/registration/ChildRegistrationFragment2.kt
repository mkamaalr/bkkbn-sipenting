package com.bkkbnjabar.sipenting.ui.child.registration

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
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.databinding.FragmentChildRegistration2Binding
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
class ChildRegistrationFragment2 : Fragment() {

    private var _binding: FragmentChildRegistration2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: ChildRegistrationViewModel by activityViewModels()

    private var isFormPopulated = false

    // Local cache for all lookup options
    private var immunizationOptions: List<LookupItem> = emptyList()
    private var counselingTypeOptions: List<LookupItem> = emptyList()
    private var contraceptionTypeOptions: List<LookupItem> = emptyList()
    private var contraceptionReasonOptions: List<LookupItem> = emptyList()
    private var contraceptionRejectionReasonOptions: List<LookupItem> = emptyList()
    private val contraceptionReasonForUseOptions = listOf("Ingin Anak di Tunda", "Tidak Ingin Anak Lagi")
    private var kkaResultOptions: List<String> = emptyList()
    private var drinkingWaterOptions: List<LookupItem> = emptyList()
    private var defecationFacilityOptions: List<LookupItem> = emptyList()
    private var socialAssistanceOptions: List<LookupItem> = emptyList()
    private var childStatusOptions: List<LookupItem> = emptyList()
    private var referralStatusOptions: List<String> = emptyList()
    private var socialAssistanceStatusOptions: List<String> = emptyList()
    private var pregnantMotherStatusOptions: List<LookupItem> = emptyList()

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
        _binding = FragmentChildRegistration2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()

        val reasonAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, contraceptionReasonForUseOptions)
        binding.etContraceptionReasonForUse.setAdapter(reasonAdapter)
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            saveUIToViewModel()
            viewModel.saveAllData()
        }

        binding.rgIsKkaFilled.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = (checkedId == R.id.rb_kka_yes)
            binding.tilKkaResult.isVisible = isChecked
            if (!isChecked) binding.actvKkaResult.setText("", false)
        }

        binding.rgOnContraception.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = (checkedId == R.id.rb_contraception_yes)
            binding.tilContraceptionType.isVisible = isChecked
            binding.tilContraceptionReasonForUse.isVisible = isChecked
            binding.tilContraceptionRejectionReason.isVisible = !isChecked
            if (isChecked) {
                binding.etContraceptionRejectionReason.setText("", false)
            } else {
                binding.actvContraceptionType.setText("", false)
                binding.etContraceptionReasonForUse.setText("", false)
            }
        }

        binding.rgIsCounselingReceived.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = (checkedId == R.id.rb_is_counseling_received_yes)
            binding.tilCounselingType.isVisible = isChecked
            if (!isChecked) binding.etCounselingType.setText("", false)
        }

        binding.btnCapture1.setOnClickListener { handleImageCapture(1) }
        binding.btnCapture2.setOnClickListener { handleImageCapture(2) }
        binding.btnGetLocation.setOnClickListener { handleLocationCapture() }

        setupDateField(binding.tilVisitDate, binding.etVisitDate)
        setupDateField(binding.tilMeasurementDate, binding.etMeasurementDate)
        setupDateField(binding.tilNextVisitDate, binding.etNextVisitDate)
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
                    findNavController().navigate(R.id.action_childRegistrationFragment2_to_nav_child_list)
                }
                is Resource.Error -> Toast.makeText(context, "Gagal menyimpan: ${result.message}", Toast.LENGTH_LONG).show()
                else -> {}
            }
        }

        // --- Observers for all lookup data ---
        viewModel.pregnantMotherStatuses.observe(viewLifecycleOwner) {
            pregnantMotherStatusOptions = it ?: emptyList()
            binding.etPregnantMotherStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pregnantMotherStatusOptions.map { it.name }))
        }
        viewModel.contraceptionTypes.observe(viewLifecycleOwner) {
            contraceptionTypeOptions = it ?: emptyList()
            binding.actvContraceptionType.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, contraceptionTypeOptions.map { it.name }))
        }
        viewModel.contraceptionRejectionReasons.observe(viewLifecycleOwner) { options ->
            // This 'options' list contains "Ingin hamil", "Alasan kesehatan", etc.
            contraceptionRejectionReasonOptions = options
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.name })
            binding.etContraceptionRejectionReason.setAdapter(adapter)
        }
        viewModel.counselingTypes.observe(viewLifecycleOwner) {
            counselingTypeOptions = it ?: emptyList()
            binding.etCounselingType.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, counselingTypeOptions.map { it.name }))
        }
        viewModel.immunizations.observe(viewLifecycleOwner) {
            immunizationOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupImmunizations, immunizationOptions, listOf("Tidak Ada"), "", null)
            updateFormFromData(viewModel.currentChildVisit.value)
        }
        viewModel.drinkingWaterSources.observe(viewLifecycleOwner) {
            drinkingWaterOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupDrinkingWater, drinkingWaterOptions, listOf("Lainnya"), "Lainnya", binding.tilDrinkingWaterOther)
            updateFormFromData(viewModel.currentChildVisit.value)

        }
        viewModel.defecationFacilities.observe(viewLifecycleOwner) {
            defecationFacilityOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupDefecationFacility, defecationFacilityOptions, listOf("Tidak ada", "Ya, lainnya"), "Ya, lainnya", binding.tilDefecationFacilityOther)
            updateFormFromData(viewModel.currentChildVisit.value)

        }
        viewModel.socialAssistanceOptions.observe(viewLifecycleOwner) {
            socialAssistanceOptions = it ?: emptyList()
            setupDynamicChips(binding.chipGroupSocialAssistance, socialAssistanceOptions, listOf("Lainnya"), "Lainnya", binding.tilSocialAssistanceOther)
            updateFormFromData(viewModel.currentChildVisit.value)
        }

        viewModel.referralStatusOptions.observe(viewLifecycleOwner) {
            referralStatusOptions = it ?: emptyList()
            binding.etFacilitatingReferralService.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, referralStatusOptions))
            updateFormFromData(viewModel.currentChildVisit.value)
        }

        viewModel.socialAssistanceStatusOptions.observe(viewLifecycleOwner) {
            socialAssistanceStatusOptions = it ?: emptyList()
            binding.etFacilitatingSocialAssistance.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, socialAssistanceStatusOptions))
            updateFormFromData(viewModel.currentChildVisit.value)
        }

        viewModel.kkaResultOptions.observe(viewLifecycleOwner) {
            kkaResultOptions = it ?: emptyList()
            binding.actvKkaResult.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, kkaResultOptions))
            updateFormFromData(viewModel.currentChildVisit.value)
        }

        // The MAIN observer to populate the form.
        viewModel.currentChildVisit.observe(viewLifecycleOwner) { visitData ->
            if (visitData != null && !isFormPopulated) {
                updateFormFromData(visitData)
                isFormPopulated = true
            }
        }
    }

    private fun updateFormFromData(data: ChildVisitData?) {
        if (data == null) return

        binding.etVisitDate.setText(data.visitDate)
        binding.etPregnantMotherStatus.setText(pregnantMotherStatusOptions.find { it.id == data.pregnantMotherStatusId }?.name ?: "", false)
        binding.etPregnancyAgeWhenChildbirth.setText(data.pregnancyAgeWhenChildbirth)
        binding.etWeightBirth.setText(data.weightBirth?.toString() ?: "")
        binding.etHeightBirth.setText(data.heightBirth?.toString() ?: "")
        binding.etMeasurementDate.setText(data.measurementDate)
        binding.etWeightMeasurement.setText(data.weightMeasurement?.toString() ?: "")
        binding.etHeightMeasurement.setText(data.heightMeasurement?.toString() ?: "")
        binding.etHeadCircumference.setText(data.headCircumference?.toString() ?: "")
        binding.etTpkNotes.setText(data.tpkNotes)
        binding.etNextVisitDate.setText(data.nextVisitDate)
        binding.etDrinkingWaterOther.setText(data.mainSourceOfDrinkingWaterOther)
        binding.etDefecationFacilityOther.setText(data.defecationFacilityOther)
        binding.etSocialAssistanceOther.setText(data.socialAssistanceFacilitationOptionsOther)

        binding.rgIsAsiExclusive.check(if (data.isAsiExclusive == true) R.id.rb_asi_exclusive_yes else R.id.rb_asi_exclusive_no)
        binding.rgIsOngoingAsi.check(if (data.isOngoingAsi == true) R.id.rb_ongoing_asi_yes else R.id.rb_ongoing_asi_no)
        binding.rgIsMpasi.check(if (data.isMpasi == true) R.id.rb_mpasi_yes else R.id.rb_mpasi_no)
        binding.rgIsKkaFilled.check(if (data.isKkaFilled == true) R.id.rb_kka_yes else R.id.rb_kka_no)
        binding.rgIsExposedToCigarettes.check(if (data.isExposedToCigarettes == true) R.id.rb_smoke_yes else R.id.rb_smoke_no)
        binding.rgIsPosyanduMonth.check(if (data.isPosyanduMonth == true) R.id.rb_posyandu_yes else R.id.rb_posyandu_no)
        binding.rgIsBkbMonth.check(if (data.isBkbMonth == true) R.id.rb_bkb_yes else R.id.rb_bkb_no)
        binding.rgIsCounselingReceived.check(if (data.isCounselingReceived == true) R.id.rb_is_counseling_received_yes else R.id.rb_is_counseling_received_no)
        binding.rgIsReceivedMbg.check(if (data.isReceivedMbg == true) R.id.rb_mbg_yes else R.id.rb_mbg_no)
        binding.rgOnContraception.check(if (data.onContraception == true) R.id.rb_contraception_yes else R.id.rb_contraception_no)

        binding.actvContraceptionType.setText(contraceptionTypeOptions.find { it.id == data.contraceptionTypeId }?.name ?: "", false)
        binding.etContraceptionReasonForUse.setText(contraceptionReasonOptions.find { it.name == data.contraceptionReasonForUse }?.name ?: "", false)
        binding.etContraceptionRejectionReason.setText(contraceptionRejectionReasonOptions.find { it.id == data.contraceptionRTypeId }?.name ?: "", false)
        binding.etCounselingType.setText(counselingTypeOptions.find { it.id == data.counselingTypeId }?.name ?: "", false)
        binding.etFacilitatingReferralService.setText(data.facilitatingReferralServiceStatus ?: "", false)
        binding.etFacilitatingSocialAssistance.setText(data.facilitatingSocialAssistanceStatus ?: "", false)
        binding.actvKkaResult.setText(data.kkaResult ?: "", false)

        updateChipGroupState(binding.chipGroupImmunizations, data.immunizationsGiven ?: emptyList())
        updateChipGroupState(binding.chipGroupDrinkingWater, data.mainSourceOfDrinkingWater ?: emptyList())
        updateChipGroupState(binding.chipGroupDefecationFacility, data.defecationFacility ?: emptyList())
        updateChipGroupState(binding.chipGroupSocialAssistance, data.socialAssistanceFacilitationOptions ?: emptyList())

        data.imagePath1?.let { binding.ivPreview1.tag = it; binding.ivPreview1.setImageURI(Uri.parse(it)) }
        data.imagePath2?.let { binding.ivPreview2.tag = it; binding.ivPreview2.setImageURI(Uri.parse(it)) }
        if (data.latitude != null && data.longitude != null) {
            binding.tvLocationResult.text = String.format(Locale.US, "Lat: %.6f, Long: %.6f", data.latitude, data.longitude)
        }
    }

    private fun saveUIToViewModel() {
        fun getSelectedChipIds(chipGroup: ChipGroup, options: List<LookupItem>): List<String> = chipGroup.children
            .filter { (it as Chip).isChecked }
            .mapNotNull { view -> options.find { it.name == (view as Chip).text.toString() }?.id.toString() }.toList()

        var latitude: Double? = null
        var longitude: Double? = null
        val locationText = binding.tvLocationResult.text.toString()
        if (locationText.startsWith("Lat:")) {
            try {
                val parts = locationText.split(", Long:")
                latitude = parts[0].removePrefix("Lat:").trim().toDouble()
                longitude = parts[1].trim().toDouble()
            } catch (_: Exception) { /* Do nothing */ }
        }

        viewModel.updateChildVisitData(
            visitDate = binding.etVisitDate.text.toString(),
            pregnancyAgeWhenChildbirth = binding.etPregnancyAgeWhenChildbirth.text.toString(),
            weightBirth = binding.etWeightBirth.text.toString().toDoubleOrNull(),
            heightBirth = binding.etHeightBirth.text.toString().toDoubleOrNull(),
            isAsiExclusive = binding.rgIsAsiExclusive.checkedRadioButtonId == R.id.rb_asi_exclusive_yes,
            onContraception = binding.rgOnContraception.checkedRadioButtonId == R.id.rb_contraception_yes,
            contraceptionTypeId = contraceptionTypeOptions.find { it.name == binding.actvContraceptionType.text.toString() }?.id,
            contraceptionReasonForUse = binding.etContraceptionReasonForUse.text.toString(),
            contraceptionRTypeId = contraceptionRejectionReasonOptions.find { it.name == binding.etContraceptionRejectionReason.text.toString() }?.id,
            measurementDate = binding.etMeasurementDate.text.toString(),
            weightMeasurement = binding.etWeightMeasurement.text.toString().toDoubleOrNull(),
            heightMeasurement = binding.etHeightMeasurement.text.toString().toDoubleOrNull(),
            isOngoingAsi = binding.rgIsOngoingAsi.checkedRadioButtonId == R.id.rb_ongoing_asi_yes,
            isMpasi = binding.rgIsMpasi.checkedRadioButtonId == R.id.rb_mpasi_yes,
            isKkaFilled = binding.rgIsKkaFilled.checkedRadioButtonId == R.id.rb_kka_yes,
            isExposedToCigarettes = binding.rgIsExposedToCigarettes.checkedRadioButtonId == R.id.rb_smoke_yes,
            isPosyanduMonth = binding.rgIsPosyanduMonth.checkedRadioButtonId == R.id.rb_posyandu_yes,
            isBkbMonth = binding.rgIsBkbMonth.checkedRadioButtonId == R.id.rb_bkb_yes,
            isCounselingReceived = binding.rgIsCounselingReceived.checkedRadioButtonId == R.id.rb_is_counseling_received_yes,
            isReceivedMbg = binding.rgIsReceivedMbg.checkedRadioButtonId == R.id.rb_mbg_yes,
            headCircumference = binding.etHeadCircumference.text.toString().toDoubleOrNull(),
            counselingTypeId = counselingTypeOptions.find { it.name == binding.etCounselingType.text.toString() }?.id,
            immunizationsGiven = getSelectedChipIds(binding.chipGroupImmunizations, immunizationOptions),
            mainSourceOfDrinkingWater = getSelectedChipIds(binding.chipGroupDrinkingWater, drinkingWaterOptions),
            mainSourceOfDrinkingWaterOther = binding.etDrinkingWaterOther.text.toString(),
            defecationFacility = getSelectedChipIds(binding.chipGroupDefecationFacility, defecationFacilityOptions),
            defecationFacilityOther = binding.etDefecationFacilityOther.text.toString(),
            facilitatingReferralServiceStatus = binding.etFacilitatingReferralService.text.toString(),
            facilitatingSocialAssistanceStatus = binding.etFacilitatingSocialAssistance.text.toString(),
            kkaResult = binding.actvKkaResult.text.toString(),
            socialAssistanceFacilitationOptions = getSelectedChipIds(binding.chipGroupSocialAssistance, socialAssistanceOptions),
            socialAssistanceFacilitationOptionsOther = binding.etSocialAssistanceOther.text.toString(),
            pregnantMotherStatusId = pregnantMotherStatusOptions.find { it.name == binding.etPregnantMotherStatus.text.toString() }?.id,
            nextVisitDate = binding.etNextVisitDate.text.toString(),
            tpkNotes = binding.etTpkNotes.text.toString(),
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
                setOnClickListener { handleChipClick(this, chipGroup, exclusiveOptions, otherOptionName, otherInputLayout) }
            }
            chipGroup.addView(chip)
        }
    }

    private fun handleChipClick(clickedChip: Chip, chipGroup: ChipGroup, exclusiveOptions: List<String>, otherOptionName: String, otherInputLayout: TextInputLayout?) {
        val clickedText = clickedChip.text.toString()
        val isExclusive = exclusiveOptions.contains(clickedText)

        if (isExclusive && clickedChip.isChecked) {
            chipGroup.children.filter { it.id != clickedChip.id }.forEach {
                (it as Chip).isChecked = false
            }
        } else if (clickedChip.isChecked) {
            exclusiveOptions.forEach { exclusiveText ->
                chipGroup.children.firstOrNull { (it as Chip).text.toString() == exclusiveText }
                    ?.let { (it as Chip).isChecked = false }
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

    private fun updateChipGroupState(chipGroup: ChipGroup, selectedIds: List<String>) {
        val options = when (chipGroup.id) {
            R.id.chip_group_immunizations -> immunizationOptions
            R.id.chip_group_drinking_water -> drinkingWaterOptions
            R.id.chip_group_defecation_facility -> defecationFacilityOptions
            R.id.chip_group_social_assistance -> socialAssistanceOptions
            else -> emptyList()
        }
        chipGroup.children.forEach { view ->
            val chip = view as? Chip ?: return@forEach
            val chipId = options.find { it.name == chip.text.toString() }?.id.toString()
            if (chip.isChecked != selectedIds.contains(chipId)) {
                chip.isChecked = selectedIds.contains(chipId)
            }
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

    private fun setupDateField(textInputLayout: TextInputLayout, editText: TextInputEditText) {
        textInputLayout.setOnClickListener { showDatePickerDialog(editText) }
        editText.setOnClickListener { showDatePickerDialog(editText) }
    }

    private fun showDatePickerDialog(editText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(editText.hint)
            .build()

        if (datePicker.isAdded) return

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            editText.setText(sdf.format(Date(selection)))
        }
        datePicker.show(parentFragmentManager, editText.id.toString())
    }

    override fun onPause() {
        super.onPause()
        saveUIToViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
