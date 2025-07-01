package com.bkkbnjabar.sipenting.ui.child.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.databinding.FragmentChildVisitEditBinding
import com.bkkbnjabar.sipenting.domain.model.LookupItem
import com.bkkbnjabar.sipenting.ui.child.registration.ChildRegistrationViewModel
import com.bkkbnjabar.sipenting.utils.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildVisitEditFragment : Fragment() {

    private var _binding: FragmentChildVisitEditBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share data across the registration/edit flow
    private val viewModel: ChildRegistrationViewModel by activityViewModels()
    private val args: ChildVisitEditFragmentArgs by navArgs()

    // Flag to prevent the form from being populated multiple times on config changes
    private var isFormPopulated = false

    private var immunizationOptions: List<LookupItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildVisitEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()

        // Tell the ViewModel to load the visit data using the ID from navigation args
        viewModel.loadVisitForEditing(args.visitId)
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            saveUIToViewModel() // Read data from UI into ViewModel
            viewModel.saveAllData() // Tell ViewModel to save
        }
        // ... Add listeners for all your RadioGroups and other interactive views here ...
    }

    private fun observeViewModel() {
        // Observer for save operation result
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

        // Observer for immunization lookup options
        viewModel.immunizations.observe(viewLifecycleOwner) { options ->
            immunizationOptions = options ?: emptyList()
            setupDynamicChips(binding.chipGroupImmunizations, immunizationOptions)
        }

        // Observer for the visit data being edited
        viewModel.currentChildVisit.observe(viewLifecycleOwner) { visitData ->
            // Populate the form only once when the data is first loaded
            if (visitData != null && visitData.localVisitId == args.visitId && !isFormPopulated) {
                updateFormFromData(visitData)
                isFormPopulated = true
            }
        }
    }

    /**
     * Populates all the UI fields with data from the ViewModel.
     */
    private fun updateFormFromData(data: ChildVisitData?) {
        if (data == null) return

        binding.etVisitDate.setText(data.visitDate)
        binding.etMeasurementDate.setText(data.measurementDate)
        binding.etWeightMeasurement.setText(data.weightMeasurement?.toString() ?: "")
        binding.etHeightMeasurement.setText(data.heightMeasurement?.toString() ?: "")
        binding.etHeadCircumference.setText(data.headCircumference?.toString() ?: "")
        binding.etTpkNotes.setText(data.tpkNotes)

        binding.rgIsAsiExclusive.check(if (data.isAsiExclusive == true) R.id.rb_asi_yes else R.id.rb_asi_no)
        // ... Set the checked state for all other RadioGroups ...

        updateChipGroupState(binding.chipGroupImmunizations, data.immunizationsGiven ?: emptyList())
    }

    /**
     * Reads all data from the UI and calls the ViewModel to update its state.
     */
    private fun saveUIToViewModel() {
        fun getSelectedChipIds(chipGroup: ChipGroup): List<String> {
            return chipGroup.children
                .filter { (it as Chip).isChecked }
                .mapNotNull { view ->
                    val chipText = (view as Chip).text.toString()
                    immunizationOptions.find { it.name == chipText }?.id.toString()
                }.toList()
        }

        viewModel.updateChildVisitData(
            visitDate = binding.etVisitDate.text.toString().trim(),
            weightMeasurement = binding.etWeightMeasurement.text.toString().toDoubleOrNull(),
            heightMeasurement = binding.etHeightMeasurement.text.toString().toDoubleOrNull(),
            // ... etc. for all other fields from the UI ...
            immunizationsGiven = getSelectedChipIds(binding.chipGroupImmunizations)
        )
    }

    private fun setupDynamicChips(chipGroup: ChipGroup, options: List<LookupItem>) {
        chipGroup.removeAllViews()
        options.forEach { item ->
            val chip = Chip(context).apply {
                text = item.name
                isCheckable = true
                id = View.generateViewId()
            }
            chipGroup.addView(chip)
        }
    }

    private fun updateChipGroupState(chipGroup: ChipGroup, selectedIds: List<String>) {
        chipGroup.children.forEach { view ->
            val chip = view as? Chip ?: return@forEach
            val chipId = immunizationOptions.find { it.name == chip.text.toString() }?.id.toString()
            if (chip.isChecked != selectedIds.contains(chipId)) {
                chip.isChecked = selectedIds.contains(chipId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset ViewModel state when leaving the screen to avoid stale data
        viewModel.resetForm()
        _binding = null
    }
}
