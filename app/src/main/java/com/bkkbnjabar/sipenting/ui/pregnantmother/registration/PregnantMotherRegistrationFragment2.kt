package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration2Binding
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.utils.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PregnantMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: PregnantMotherRegistrationViewModel by activityViewModels()

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
        loadDataFromViewModel()
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            saveDataToViewModel()
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveDataToViewModel()
                viewModel.saveAllData()
            } else {
                Toast.makeText(requireContext(), "Harap lengkapi semua field yang wajib diisi.", Toast.LENGTH_SHORT).show()
            }
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

        // Listener untuk tombol multi-select
        binding.btnSelectDiseaseHistory.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Riwayat Penyakit",
                viewModel.diseaseHistories.value ?: emptyList(),
                viewModel.currentPregnantMotherVisit.value?.diseaseHistory ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(diseaseHistory = selectedItems)
            }
        }
        binding.btnSelectDrinkingWater.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Sumber Air Minum",
                viewModel.mainSourcesOfDrinkingWater.value ?: emptyList(),
                viewModel.currentPregnantMotherVisit.value?.mainSourceOfDrinkingWater ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(mainSourceOfDrinkingWater = selectedItems)
            }
        }
        binding.btnSelectDefecationFacility.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Fasilitas Jamban",
                viewModel.defecationFacilities.value ?: emptyList(),
                viewModel.currentPregnantMotherVisit.value?.defecationFacility ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(defecationFacility = selectedItems)
            }
        }
        binding.btnSelectSocialAssistance.setOnClickListener {
            showMultiSelectDialog(
                "Pilih Bantuan Sosial",
                viewModel.socialAssistanceOptions.value ?: emptyList(),
                viewModel.currentPregnantMotherVisit.value?.socialAssistanceFacilitationOptions ?: emptyList()
            ) { selectedItems ->
                viewModel.updatePregnantMotherVisitData(socialAssistanceFacilitationOptions = selectedItems)
            }
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

        // Observers untuk mengisi adapter dropdown
        viewModel.pregnantMotherStatuses.observe(viewLifecycleOwner) { setAdapter(binding.etPregnantMotherStatus, it) }
        viewModel.givenBirthStatuses.observe(viewLifecycleOwner) { setAdapter(binding.etGivenBirthStatus, it) }
        viewModel.counselingTypes.observe(viewLifecycleOwner) { setAdapter(binding.etCounselingType, it) }
        viewModel.deliveryPlaces.observe(viewLifecycleOwner) { setAdapter(binding.etDeliveryPlace, it) }
        viewModel.birthAssistants.observe(viewLifecycleOwner) { setAdapter(binding.etBirthAssistant, it) }
        viewModel.contraceptionOptions.observe(viewLifecycleOwner) { setAdapter(binding.etContraceptionOption, it) }

        // Observer untuk mengupdate tampilan ChipGroup
        viewModel.currentPregnantMotherVisit.observe(viewLifecycleOwner) { visitData ->
            if (visitData == null) return@observe
            updateChipGroup(binding.chipGroupDiseaseHistory, visitData.diseaseHistory)
            updateChipGroup(binding.chipGroupDrinkingWater, visitData.mainSourceOfDrinkingWater)
            updateChipGroup(binding.chipGroupDefecationFacility, visitData.defecationFacility)
            updateChipGroup(binding.chipGroupSocialAssistance, visitData.socialAssistanceFacilitationOptions)
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
            isGivenBirth = binding.cbIsGivenBirth.isChecked
        )
    }

    // ================== FUNGSI YANG HILANG SEKARANG ADA DI SINI ==================
    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etVisitDate.text.isNullOrBlank()) {
            binding.tilVisitDate.error = "Tanggal kunjungan wajib diisi"
            isValid = false
        } else {
            binding.tilVisitDate.error = null
        }

        if (binding.etPregnancyWeek.text.isNullOrBlank()) {
            binding.tilPregnancyWeek.error = "Usia kehamilan wajib diisi"
            isValid = false
        } else {
            binding.tilPregnancyWeek.error = null
        }

        // Anda bisa menambahkan validasi untuk field lain di sini dengan pola yang sama
        // if (binding.etCurrentWeight.text.isNullOrBlank()) { ... }

        return isValid
    }
    // ===========================================================================

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
                    selectedList.add(itemsArray[which])
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

    private fun updateChipGroup(chipGroup: ChipGroup, selectedItems: List<String>?) {
        chipGroup.removeAllViews()
        selectedItems?.forEach { item ->
            val chip = Chip(context)
            chip.text = item
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
