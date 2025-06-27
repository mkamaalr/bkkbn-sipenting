package com.bkkbnjabar.sipenting.ui.pregnantmother.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherDetailBinding
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import com.bkkbnjabar.sipenting.ui.pregnantmother.registration.PregnantMotherRegistrationViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherDetailFragment : Fragment() {

    private var _binding: FragmentPregnantMotherDetailBinding? = null
    private val binding get() = _binding!!
    // Use viewModels for the ViewModel specific to this screen
    private val detailViewModel: PregnantMotherDetailViewModel by viewModels()
    // Use activityViewModels for the ViewModel shared across the registration flow
    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()


    private val viewModel: PregnantMotherDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPregnantMotherDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val visitAdapter = VisitHistoryAdapter { visit ->
            // TODO: Navigasi ke halaman edit kunjungan dengan membawa visit.localVisitId
        }

        binding.recyclerViewVisits.apply {
            adapter = visitAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // ADDED: FAB click listener
        binding.fabAddVisit.setOnClickListener {
            // Get the current mother's data from the detailViewModel
            detailViewModel.motherDetails.value?.let { motherEntity ->
                // Prepare the shared registrationViewModel for adding a new visit
                registrationViewModel.startNewVisitForExistingMother(motherEntity)

                // Navigate to the visit registration screen
                findNavController().navigate(R.id.action_pregnantMotherDetailFragment_to_pregnantMotherRegistrationFragment2)
            }
        }

        observeViewModel(visitAdapter)
    }

    private fun observeViewModel(adapter: VisitHistoryAdapter) {
        viewModel.motherDetails.observe(viewLifecycleOwner) { mother ->
            mother?.let {
                binding.tvMotherNameDetail.text = it.name
                binding.tvMotherNikDetail.text = "NIK: ${it.nik}"
                // ADDED: Set new TextViews
                binding.tvMotherDobDetail.text = "Tanggal Lahir: ${it.dateOfBirth}"
                binding.tvMotherPhoneDetail.text = "No. HP: ${it.phoneNumber ?: '-'}"
                val address = "${it.fullAddress}, RT ${it.rtName}, RW ${it.rwName}, ${it.kelurahanName}"
                binding.tvMotherAddressDetail.text = address
                viewModel.calculateAllInterpretations()
            }
        }

        viewModel.visitHistory.observe(viewLifecycleOwner) { visits ->
            adapter.submitList(visits)
            viewModel.calculateAllInterpretations()
        }

        // Observe raw values
        viewModel.pregnancyWeekAgeText.observe(viewLifecycleOwner) { binding.tvInterpretationWeekAge.text = it }
        viewModel.tbjValueText.observe(viewLifecycleOwner) { binding.tvInterpretationTbjValue.text = it }

        // Observe all interpretation LiveData
        viewModel.interpretationAge.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationAge, it) }
        viewModel.interpretationChildCount.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationChildCount, it) }
        viewModel.interpretationTfu.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationTfu, it) }
        viewModel.interpretationImt.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationImt, it) }
        viewModel.interpretationDisease.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationDisease, it) }
        viewModel.interpretationHb.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationHb, it) }
        viewModel.interpretationLila.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationLila, it) }
        viewModel.interpretationTbj.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationTbj, it) }
        viewModel.interpretationWater.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationWater, it) }
        viewModel.interpretationBab.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationBab, it) }
        viewModel.interpretationSmoke.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationSmoke, it) }
    }

    private fun updateInterpretationUI(textView: TextView, result: InterpretationResult?) {
        result ?: return
        var displayText = result.text
        if (!result.recommendation.isNullOrBlank()){
            displayText += "\n${result.recommendation}"
        }

        textView.text = displayText
        textView.setTextColor(ContextCompat.getColor(requireContext(), result.color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}