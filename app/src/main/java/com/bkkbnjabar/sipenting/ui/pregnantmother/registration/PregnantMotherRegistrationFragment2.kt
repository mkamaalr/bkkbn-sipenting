package com.bkkbnjabar.sipenting.ui.pregnantmother.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.google.android.material.checkbox.MaterialCheckBox
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherRegistration2Binding
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.popToInclusive
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class PregnantMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentPregnantMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    // Opsi ini sekarang akan diisi dari ViewModel
    private var currentDiseaseHistoryOptions: List<String> = emptyList()
    private var currentMainSourceOfDrinkingWaterOptions: List<String> = emptyList()
    private var currentDefecationFacilityOptions: List<String> = emptyList()
    private var currentSocialAssistanceFacilitationOptions: List<String> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_registration_2, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        Log.d("PMR_FRAG2_LIFECYCLE", "onCreateView called.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PMR_FRAG2_LIFECYCLE", "onViewCreated called.")
        setupListeners()
        // setupCheckboxes() akan dipanggil setelah data options diobservasi
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            Log.d("PMR_FRAG2_ACTION", "Previous button clicked. Navigating back to Fragment 1.")
            saveFormData() // Simpan data terbaru kunjungan sebelum kembali
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            Log.d("PMR_FRAG2_ACTION", "Save button clicked. Calling savePregnantMother() from Fragment 2.")
            saveFormData() // Simpan data terbaru kunjungan ke ViewModel
            if (validateForm()) {
                val currentMotherData = registrationViewModel.currentPregnantMother.value
                val currentVisitData = registrationViewModel.currentPregnantMotherVisit.value
                Log.d("PMR_FRAG2_ACTION", "Data to be saved - Mother: Name=${currentMotherData?.name}, LocalId=${currentMotherData?.localId}")
                Log.d("PMR_FRAG2_ACTION", "Data to be saved - Visit: VisitDate=${currentVisitData?.visitDate}, PregnantMotherLocalId=${currentVisitData?.pregnantMotherLocalId}, LocalVisitId=${currentVisitData?.localVisitId}, DiseaseHistory=${currentVisitData?.diseaseHistory}")
                registrationViewModel.savePregnantMother() // Ini akan memicu penyimpanan ibu hamil, lalu kunjungan
            } else {
                Log.w("PMR_FRAG2_ACTION", "Form validation failed.")
                Toast.makeText(context, "Silakan lengkapi semua bidang yang diperlukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etVisitDate.setOnClickListener { showDatePickerDialog(binding.etVisitDate) }
        binding.etDateOfBirthLastChild.setOnClickListener { showDatePickerDialog(binding.etDateOfBirthLastChild) }
        binding.etNextVisitDate.setOnClickListener { showDatePickerDialog(binding.etNextVisitDate) }
    }

    private fun showDatePickerDialog(editText: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(selectedDate.time))
            Log.d("PMR_FRAG2_ACTION", "Date set to: ${editText.text}")
        }, year, month, day)

        datePickerDialog.show()
    }

    // Fungsi untuk membuat dan menambahkan checkbox secara dinamis
    private fun createAndAddCheckboxes(container: LinearLayout, options: List<String>) {
        container.removeAllViews() // Pastikan container kosong sebelum menambahkan
        options.forEach { option ->
            val checkBox = MaterialCheckBox(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = option
                textSize = 16f
            }
            container.addView(checkBox)
        }
    }

    private fun loadFormData() {
        Log.d("PMR_FRAG2_LOAD", "loadFormData: Attempting to load visit data from ViewModel into UI.")
        registrationViewModel.currentPregnantMotherVisit.value?.let { data ->
            binding.etVisitDate.setText(data.visitDate ?: "")
            binding.etChildNumber.setText(data.childNumber?.toString() ?: "")
            binding.etDateOfBirthLastChild.setText(data.dateOfBirthLastChild ?: "")
            binding.etPregnancyWeekAge.setText(data.pregnancyWeekAge?.toString() ?: "")
            binding.etWeightTrimester1.setText(data.weightTrimester1?.toString() ?: "")
            binding.etCurrentHeight.setText(data.currentHeight?.toString() ?: "")
            binding.etCurrentWeight.setText(data.currentWeight?.toString() ?: "")
            binding.cbIsHbChecked.isChecked = data.isHbChecked ?: false
            binding.etHemoglobinLevel.setText(data.hemoglobinLevel?.toString() ?: "")
            binding.etUpperArmCircumference.setText(data.upperArmCircumference?.toString() ?: "")
            binding.cbIsTwin.isChecked = data.isTwin ?: false
            binding.etNumberOfTwins.setText(data.numberOfTwins?.toString() ?: "")
            binding.cbIsEstimatedFetalWeightChecked.isChecked = data.isEstimatedFetalWeightChecked ?: false
            binding.cbIsExposedToCigarettes.isChecked = data.isExposedToCigarettes ?: false
            binding.cbIsCounselingReceived.isChecked = data.isCounselingReceived ?: false
            binding.etCounselingTypeId.setText(data.counselingTypeId?.toString() ?: "")
            binding.cbIsIronTablesReceived.isChecked = data.isIronTablesReceived ?: false
            binding.cbIsIronTablesTaken.isChecked = data.isIronTablesTaken ?: false
            binding.etFacilitatingReferralServiceStatus.setText(data.facilitatingReferralServiceStatus ?: "")
            binding.etFacilitatingSocialAssistanceStatus.setText(data.facilitatingSocialAssistanceStatus ?: "")
            binding.etNextVisitDate.setText(data.nextVisitDate ?: "")
            binding.etTpkNotes.setText(data.tpkNotes ?: "")
            binding.cbIsAlive.isChecked = data.isAlive ?: false
            binding.cbIsGivenBirth.isChecked = data.isGivenBirth ?: false
            binding.etGivenBirthStatusId.setText(data.givenBirthStatusId?.toString() ?: "")
            binding.etPregnantMotherStatusId.setText(data.pregnantMotherStatusId?.toString() ?: "")

            // Set state checkbox berdasarkan data yang dimuat
            setCheckedCheckboxes(binding.llDiseaseHistoryCheckboxes, data.diseaseHistory)
            setCheckedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes, data.mainSourceOfDrinkingWater)
            setCheckedCheckboxes(binding.llDefecationFacilityCheckboxes, data.defecationFacility)
            setCheckedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes, data.socialAssistanceFacilitationOptions)

            Log.d("PMR_FRAG2_LOAD", "Visit data loaded: VisitDate=${data.visitDate}, ChildNum=${data.childNumber}, PregnantMotherLocalId=${data.pregnantMotherLocalId}, LocalVisitId=${data.localVisitId}, DiseaseHistory=${data.diseaseHistory}")
        } ?: run {
            Log.d("PMR_FRAG2_LOAD", "currentPregnantMotherVisit is null. Clearing all UI fields and unchecking all checkboxes.")
            // Clear all fields
            binding.etVisitDate.setText("")
            binding.etChildNumber.setText("")
            binding.etDateOfBirthLastChild.setText("")
            binding.etPregnancyWeekAge.setText("")
            binding.etWeightTrimester1.setText("")
            binding.etCurrentHeight.setText("")
            binding.etCurrentWeight.setText("")
            binding.cbIsHbChecked.isChecked = false
            binding.etHemoglobinLevel.setText("")
            binding.etUpperArmCircumference.setText("")
            binding.cbIsTwin.isChecked = false
            binding.etNumberOfTwins.setText("")
            binding.cbIsEstimatedFetalWeightChecked.isChecked = false
            binding.cbIsExposedToCigarettes.isChecked = false
            binding.cbIsCounselingReceived.isChecked = false
            binding.etCounselingTypeId.setText("")
            binding.cbIsIronTablesReceived.isChecked = false
            binding.cbIsIronTablesTaken.isChecked = false
            binding.etFacilitatingReferralServiceStatus.setText("")
            binding.etFacilitatingSocialAssistanceStatus.setText("")
            binding.etNextVisitDate.setText("")
            binding.etTpkNotes.setText("")
            binding.cbIsAlive.isChecked = false
            binding.cbIsGivenBirth.isChecked = false
            binding.etGivenBirthStatusId.setText("")
            binding.etPregnantMotherStatusId.setText("")
            setCheckedCheckboxes(binding.llDiseaseHistoryCheckboxes, null)
            setCheckedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes, null)
            setCheckedCheckboxes(binding.llDefecationFacilityCheckboxes, null)
            setCheckedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes, null)
        }
    }

    private fun setCheckedCheckboxes(container: LinearLayout, selectedItems: List<String>?) {
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is MaterialCheckBox) {
                view.isChecked = selectedItems?.contains(view.text.toString()) == true
            }
        }
    }

    private fun saveFormData() {
        Log.d("PMR_FRAG2_ACTION", "saveFormData: Capturing current UI input and updating ViewModel for Visit Data.")

        fun getSelectedCheckboxes(container: LinearLayout): List<String>? {
            val selected = mutableListOf<String>()
            for (i in 0 until container.childCount) {
                val view = container.getChildAt(i)
                if (view is MaterialCheckBox && view.isChecked) {
                    selected.add(view.text.toString())
                }
            }
            return if (selected.isEmpty()) null else selected
        }

        registrationViewModel.updatePregnantMotherVisitData(
            visitDate = binding.etVisitDate.text.toString().trim(),
            childNumber = binding.etChildNumber.text.toString().trim().toIntOrNull(),
            dateOfBirthLastChild = binding.etDateOfBirthLastChild.text.toString().trim(),
            pregnancyWeekAge = binding.etPregnancyWeekAge.text.toString().trim().toIntOrNull(),
            weightTrimester1 = binding.etWeightTrimester1.text.toString().trim().toDoubleOrNull(),
            currentHeight = binding.etCurrentHeight.text.toString().trim().toDoubleOrNull(),
            currentWeight = binding.etCurrentWeight.text.toString().trim().toDoubleOrNull(),
            isHbChecked = binding.cbIsHbChecked.isChecked,
            hemoglobinLevel = binding.etHemoglobinLevel.text.toString().trim().toDoubleOrNull(),
            upperArmCircumference = binding.etUpperArmCircumference.text.toString().trim().toDoubleOrNull(),
            isTwin = binding.cbIsTwin.isChecked,
            numberOfTwins = binding.etNumberOfTwins.text.toString().trim().toIntOrNull(),
            isEstimatedFetalWeightChecked = binding.cbIsEstimatedFetalWeightChecked.isChecked,
            isExposedToCigarettes = binding.cbIsExposedToCigarettes.isChecked,
            isCounselingReceived = binding.cbIsCounselingReceived.isChecked,
            counselingTypeId = binding.etCounselingTypeId.text.toString().trim().toIntOrNull(),
            isIronTablesReceived = binding.cbIsIronTablesReceived.isChecked,
            isIronTablesTaken = binding.cbIsIronTablesTaken.isChecked,
            facilitatingReferralServiceStatus = binding.etFacilitatingReferralServiceStatus.text.toString().trim(),
            facilitatingSocialAssistanceStatus = binding.etFacilitatingSocialAssistanceStatus.text.toString().trim(),
            nextVisitDate = binding.etNextVisitDate.text.toString().trim(),
            tpkNotes = binding.etTpkNotes.text.toString().trim(),
            isAlive = binding.cbIsAlive.isChecked,
            isGivenBirth = binding.cbIsGivenBirth.isChecked,
            givenBirthStatusId = binding.etGivenBirthStatusId.text.toString().trim().toIntOrNull(),
            pregnantMotherStatusId = binding.etPregnantMotherStatusId.text.toString().trim().toIntOrNull(),
            diseaseHistory = getSelectedCheckboxes(binding.llDiseaseHistoryCheckboxes),
            mainSourceOfDrinkingWater = getSelectedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes),
            defecationFacility = getSelectedCheckboxes(binding.llDefecationFacilityCheckboxes),
            socialAssistanceFacilitationOptions = getSelectedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes)
        )
        Log.d("PMR_FRAG2_ACTION", "saveFormData: ViewModel updated for Visit. Current ViewModel VisitDate: ${registrationViewModel.currentPregnantMotherVisit.value?.visitDate}, LocalVisitId: ${registrationViewModel.currentPregnantMotherVisit.value?.localVisitId}, DiseaseHistory: ${registrationViewModel.currentPregnantMotherVisit.value?.diseaseHistory}")
    }

    private fun validateForm(): Boolean {

        fun getSelectedCheckboxes(container: LinearLayout): List<String>? {
            val selected = mutableListOf<String>()
            for (i in 0 until container.childCount) {
                val view = container.getChildAt(i)
                if (view is MaterialCheckBox && view.isChecked) {
                    selected.add(view.text.toString())
                }
            }
            return if (selected.isEmpty()) null else selected
        }
        var isValid = true

        if (binding.etVisitDate.text.isNullOrBlank()) {
            binding.tilVisitDate.error = "Tanggal Kunjungan tidak boleh kosong"
            isValid = false
        } else {
            binding.tilVisitDate.error = null
        }

        if (binding.etChildNumber.text.isNullOrBlank()) {
            binding.tilChildNumber.error = "Anak Ke-berapa tidak boleh kosong"
            isValid = false
        } else if (binding.etChildNumber.text.toString().toIntOrNull() == null) {
            binding.tilChildNumber.error = "Anak Ke-berapa harus angka"
            isValid = false
        } else {
            binding.tilChildNumber.error = null
        }

        if (binding.etPregnancyWeekAge.text.isNullOrBlank()) {
            binding.tilPregnancyWeekAge.error = "Usia Kehamilan tidak boleh kosong"
            isValid = false
        } else if (binding.etPregnancyWeekAge.text.toString().toIntOrNull() == null) {
            binding.tilPregnancyWeekAge.error = "Usia Kehamilan harus angka"
            isValid = false
        } else {
            binding.tilPregnancyWeekAge.error = null
        }

        if (binding.etCurrentWeight.text.isNullOrBlank()) {
            binding.tilCurrentWeight.error = "Berat Badan Saat Ini tidak boleh kosong"
            isValid = false
        } else if (binding.etCurrentWeight.text.toString().toDoubleOrNull() == null) {
            binding.tilCurrentWeight.error = "Berat Badan Saat Ini harus angka"
            isValid = false
        } else {
            binding.tilCurrentWeight.error = null
        }

        // Contoh validasi: setidaknya satu opsi harus dipilih untuk kategori ini
        if (getSelectedCheckboxes(binding.llDiseaseHistoryCheckboxes).isNullOrEmpty()) {
            Toast.makeText(context, "Pilih setidaknya satu Riwayat Penyakit", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (getSelectedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes).isNullOrEmpty()) {
            Toast.makeText(context, "Pilih setidaknya satu Sumber Air Minum Utama", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (getSelectedCheckboxes(binding.llDefecationFacilityCheckboxes).isNullOrEmpty()) {
            Toast.makeText(context, "Pilih setidaknya satu Fasilitas Buang Air Besar", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (getSelectedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes).isNullOrEmpty()) {
            Toast.makeText(context, "Pilih setidaknya satu Pilihan Fasilitasi Bantuan Sosial", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        Log.d("PMR_FRAG2_OBSERVE", "observeViewModel: Setting up observers for currentPregnantMotherVisit and dynamic lookup options.")

        // BARU: Observe dynamic lookup options from ViewModel
        registrationViewModel.diseaseHistoryOptions.observe(viewLifecycleOwner) { options ->
            currentDiseaseHistoryOptions = options
            createAndAddCheckboxes(binding.llDiseaseHistoryCheckboxes, options)
            loadFormData() // Muat ulang data form setelah opsi tersedia
            Log.d("PMR_FRAG2_OBSERVE", "Disease History options updated. Total: ${options.size}. Rebuilding checkboxes.")
        }
        registrationViewModel.mainSourceOfDrinkingWaterOptions.observe(viewLifecycleOwner) { options ->
            currentMainSourceOfDrinkingWaterOptions = options
            createAndAddCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes, options)
            loadFormData() // Muat ulang data form setelah opsi tersedia
            Log.d("PMR_FRAG2_OBSERVE", "Drinking Water options updated. Total: ${options.size}. Rebuilding checkboxes.")
        }
        registrationViewModel.defecationFacilityOptions.observe(viewLifecycleOwner) { options ->
            currentDefecationFacilityOptions = options
            createAndAddCheckboxes(binding.llDefecationFacilityCheckboxes, options)
            loadFormData() // Muat ulang data form setelah opsi tersedia
            Log.d("PMR_FRAG2_OBSERVE", "Defecation Facility options updated. Total: ${options.size}. Rebuilding checkboxes.")
        }
        registrationViewModel.socialAssistanceFacilitationOptions.observe(viewLifecycleOwner) { options ->
            currentSocialAssistanceFacilitationOptions = options
            createAndAddCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes, options)
            loadFormData() // Muat ulang data form setelah opsi tersedia
            Log.d("PMR_FRAG2_OBSERVE", "Social Assistance options updated. Total: ${options.size}. Rebuilding checkboxes.")
        }

        registrationViewModel.currentPregnantMotherVisit.observe(viewLifecycleOwner) { pregnantMotherVisitData ->
            Log.d("PMR_FRAG2_OBSERVE", "currentPregnantMotherVisit observed. Data: VisitDate=${pregnantMotherVisitData?.visitDate}, ChildNum=${pregnantMotherVisitData?.childNumber}, PregnantMotherLocalId=${pregnantMotherVisitData?.pregnantMotherLocalId}, LocalVisitId=${pregnantMotherVisitData?.localVisitId}, DiseaseHistory=${pregnantMotherVisitData?.diseaseHistory}")
            pregnantMotherVisitData?.let { data ->
                if (binding.etVisitDate.text.toString() != (data.visitDate ?: "")) {
                    binding.etVisitDate.setText(data.visitDate ?: "")
                }
                if (binding.etChildNumber.text.toString() != (data.childNumber?.toString() ?: "")) {
                    binding.etChildNumber.setText(data.childNumber?.toString() ?: "")
                }
                if (binding.etDateOfBirthLastChild.text.toString() != (data.dateOfBirthLastChild ?: "")) {
                    binding.etDateOfBirthLastChild.setText(data.dateOfBirthLastChild ?: "")
                }
                if (binding.etPregnancyWeekAge.text.toString() != (data.pregnancyWeekAge?.toString() ?: "")) {
                    binding.etPregnancyWeekAge.setText(data.pregnancyWeekAge?.toString() ?: "")
                }
                if (binding.etWeightTrimester1.text.toString() != (data.weightTrimester1?.toString() ?: "")) {
                    binding.etWeightTrimester1.setText(data.weightTrimester1?.toString() ?: "")
                }
                if (binding.etCurrentHeight.text.toString() != (data.currentHeight?.toString() ?: "")) {
                    binding.etCurrentHeight.setText(data.currentHeight?.toString() ?: "")
                }
                if (binding.etCurrentWeight.text.toString() != (data.currentWeight?.toString() ?: "")) {
                    binding.etCurrentWeight.setText(data.currentWeight?.toString() ?: "")
                }
                if (binding.cbIsHbChecked.isChecked != (data.isHbChecked ?: false)) {
                    binding.cbIsHbChecked.isChecked = data.isHbChecked ?: false
                }
                if (binding.etHemoglobinLevel.text.toString() != (data.hemoglobinLevel?.toString() ?: "")) {
                    binding.etHemoglobinLevel.setText(data.hemoglobinLevel?.toString() ?: "")
                }
                if (binding.etUpperArmCircumference.text.toString() != (data.upperArmCircumference?.toString() ?: "")) {
                    binding.etUpperArmCircumference.setText(data.upperArmCircumference?.toString() ?: "")
                }
                if (binding.cbIsTwin.isChecked != (data.isTwin ?: false)) {
                    binding.cbIsTwin.isChecked = data.isTwin ?: false
                }
                if (binding.etNumberOfTwins.text.toString() != (data.numberOfTwins?.toString() ?: "")) {
                    binding.etNumberOfTwins.setText(data.numberOfTwins?.toString() ?: "")
                }
                if (binding.cbIsEstimatedFetalWeightChecked.isChecked != (data.isEstimatedFetalWeightChecked ?: false)) {
                    binding.cbIsEstimatedFetalWeightChecked.isChecked = data.isEstimatedFetalWeightChecked ?: false
                }
                if (binding.cbIsExposedToCigarettes.isChecked != (data.isExposedToCigarettes ?: false)) {
                    binding.cbIsExposedToCigarettes.isChecked = data.isExposedToCigarettes ?: false
                }
                if (binding.cbIsCounselingReceived.isChecked != (data.isCounselingReceived ?: false)) {
                    binding.cbIsCounselingReceived.isChecked = data.isCounselingReceived ?: false
                }
                if (binding.etCounselingTypeId.text.toString() != (data.counselingTypeId?.toString() ?: "")) {
                    binding.etCounselingTypeId.setText(data.counselingTypeId?.toString() ?: "")
                }
                if (binding.cbIsIronTablesReceived.isChecked != (data.isIronTablesReceived ?: false)) {
                    binding.cbIsIronTablesReceived.isChecked = data.isIronTablesReceived ?: false
                }
                if (binding.cbIsIronTablesTaken.isChecked != (data.isIronTablesTaken ?: false)) {
                    binding.cbIsIronTablesTaken.isChecked = data.isIronTablesTaken ?: false
                }
                if (binding.etFacilitatingReferralServiceStatus.text.toString() != (data.facilitatingReferralServiceStatus ?: "")) {
                    binding.etFacilitatingReferralServiceStatus.setText(data.facilitatingReferralServiceStatus ?: "")
                }
                if (binding.etFacilitatingSocialAssistanceStatus.text.toString() != (data.facilitatingSocialAssistanceStatus ?: "")) {
                    binding.etFacilitatingSocialAssistanceStatus.setText(data.facilitatingSocialAssistanceStatus ?: "")
                }
                if (binding.etNextVisitDate.text.toString() != (data.nextVisitDate ?: "")) {
                    binding.etNextVisitDate.setText(data.nextVisitDate ?: "")
                }
                if (binding.etTpkNotes.text.toString() != (data.tpkNotes ?: "")) {
                    binding.etTpkNotes.setText(data.tpkNotes ?: "")
                }
                if (binding.cbIsAlive.isChecked != (data.isAlive ?: false)) {
                    binding.cbIsAlive.isChecked = data.isAlive ?: false
                }
                if (binding.cbIsGivenBirth.isChecked != (data.isGivenBirth ?: false)) {
                    binding.cbIsGivenBirth.isChecked = data.isGivenBirth ?: false
                }
                if (binding.etGivenBirthStatusId.text.toString() != (data.givenBirthStatusId?.toString() ?: "")) {
                    binding.etGivenBirthStatusId.setText(data.givenBirthStatusId?.toString() ?: "")
                }
                if (binding.etPregnantMotherStatusId.text.toString() != (data.pregnantMotherStatusId?.toString() ?: "")) {
                    binding.etPregnantMotherStatusId.setText(data.pregnantMotherStatusId?.toString() ?: "")
                }
                // Set state checkbox dari ViewModel (dipanggil lagi di loadFormData untuk memastikan setelah options terisi)
                setCheckedCheckboxes(binding.llDiseaseHistoryCheckboxes, data.diseaseHistory)
                setCheckedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes, data.mainSourceOfDrinkingWater)
                setCheckedCheckboxes(binding.llDefecationFacilityCheckboxes, data.defecationFacility)
                setCheckedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes, data.socialAssistanceFacilitationOptions)

            } ?: run {
                Log.d("PMR_FRAG2_OBSERVE", "currentPregnantMotherVisit is NULL. Clearing all UI fields.")
                // Clear all fields
                binding.etVisitDate.setText("")
                binding.etChildNumber.setText("")
                binding.etDateOfBirthLastChild.setText("")
                binding.etPregnancyWeekAge.setText("")
                binding.etWeightTrimester1.setText("")
                binding.etCurrentHeight.setText("")
                binding.etCurrentWeight.setText("")
                binding.cbIsHbChecked.isChecked = false
                binding.etHemoglobinLevel.setText("")
                binding.etUpperArmCircumference.setText("")
                binding.cbIsTwin.isChecked = false
                binding.etNumberOfTwins.setText("")
                binding.cbIsEstimatedFetalWeightChecked.isChecked = false
                binding.cbIsExposedToCigarettes.isChecked = false
                binding.cbIsCounselingReceived.isChecked = false
                binding.etCounselingTypeId.setText("")
                binding.cbIsIronTablesReceived.isChecked = false
                binding.cbIsIronTablesTaken.isChecked = false
                binding.etFacilitatingReferralServiceStatus.setText("")
                binding.etFacilitatingSocialAssistanceStatus.setText("")
                binding.etNextVisitDate.setText("")
                binding.etTpkNotes.setText("")
                binding.cbIsAlive.isChecked = false
                binding.cbIsGivenBirth.isChecked = false
                binding.etGivenBirthStatusId.setText("")
                binding.etPregnantMotherStatusId.setText("")
                setCheckedCheckboxes(binding.llDiseaseHistoryCheckboxes, null)
                setCheckedCheckboxes(binding.llMainSourceOfDrinkingWaterCheckboxes, null)
                setCheckedCheckboxes(binding.llDefecationFacilityCheckboxes, null)
                setCheckedCheckboxes(binding.llSocialAssistanceFacilitationOptionsCheckboxes, null)
            }
        }

        registrationViewModel.saveResult.observe(viewLifecycleOwner) { resource ->
            if (resource == null) {
                Log.d("PMR_FRAG2_OBSERVE", "saveResult is null (reset). Ignoring.")
                return@observe
            }
            Log.d("PMR_FRAG2_OBSERVE", "saveResult observed. Status: ${resource.javaClass.simpleName}. Message: ${resource.data ?: resource.message}")

            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(context, "Menyimpan data Ibu Hamil...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Log.d("PMR_FRAG2_OBSERVE", "Mother data save successful. Waiting for visit data save result.")
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Gagal menyimpan Ibu Hamil: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("PMR_FRAG2_OBSERVE", "Mother data save error: ${resource.message}")
                    registrationViewModel.resetSaveResult()
                }
                Resource.Idle -> { // Tambahan branch 'Idle'
                    Log.d("PMR_FRAG2_OBSERVE", "saveResult: Dalam kondisi Idle, siap untuk operasi baru atau telah direset.")
                    // Pastikan tidak ada indikator loading yang tersisa jika ini adalah state reset
                    // binding.progressBar.visibility = View.GONE // Contoh, pastikan semua indikator disembunyikan
                }
            }
        }

        registrationViewModel.saveVisitResult.observe(viewLifecycleOwner) { resource ->
            if (resource == null) {
                Log.d("PMR_FRAG2_OBSERVE", "saveVisitResult is null (reset). Ignoring.")
                return@observe
            }
            Log.d("PMR_FRAG2_OBSERVE", "saveVisitResult observed. Status: ${resource.javaClass.simpleName}. Message: ${resource.data ?: resource.message}")

            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(context, "Menyimpan data Kunjungan...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(context, "Semua data berhasil disimpan!", Toast.LENGTH_LONG).show()
                    Log.d("PMR_FRAG2_OBSERVE", "Visit data save successful. Navigating back to list.")
                    findNavController().popToInclusive(R.id.pregnantMotherListFragment, true)
                    registrationViewModel.resetSaveResult()
                    registrationViewModel.resetSaveVisitResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Gagal menyimpan Kunjungan: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("PMR_FRAG2_OBSERVE", "Visit data save error: ${resource.message}")
                    registrationViewModel.resetSaveResult()
                    registrationViewModel.resetSaveVisitResult()
                }
                Resource.Idle -> { // Tambahan branch 'Idle'
                    Log.d("PMR_FRAG2_OBSERVE", "saveVisitResult: Dalam kondisi Idle, siap untuk operasi baru atau telah direset.")
                    // Pastikan tidak ada indikator loading yang tersisa jika ini adalah state reset
                    // binding.progressBar.visibility = View.GONE // Contoh, pastikan semua indikator disembunyikan
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PMR_FRAG2_LIFECYCLE", "onDestroyView called.")
    }
}
