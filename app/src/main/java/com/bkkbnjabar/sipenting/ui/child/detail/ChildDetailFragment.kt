package com.bkkbnjabar.sipenting.ui.child.detail

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
import androidx.core.view.isVisible
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentChildDetailBinding
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import com.bkkbnjabar.sipenting.ui.child.registration.ChildRegistrationViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildDetailFragment : Fragment() {

    private var _binding: FragmentChildDetailBinding? = null
    private val binding get() = _binding!!
    // Use viewModels for the ViewModel specific to this screen
    private val detailViewModel: ChildDetailViewModel by viewModels()
    // Use activityViewModels for the ViewModel shared across the registration flow
    private val registrationViewModel: ChildRegistrationViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val visitAdapter = ChildVisitHistoryAdapter { visit ->
            val action = ChildDetailFragmentDirections
                .actionChildDetailFragmentToChildVisitEditFragment(visit.localVisitId)
            findNavController().navigate(action)
        }

        binding.recyclerViewVisits.apply {
            adapter = visitAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // ADDED: FAB click listener
        binding.fabAddVisit.setOnClickListener {
            // Get the current mother's data from the detailViewModel
            detailViewModel.childDetails.value?.let { childEntity ->
                // Prepare the shared registrationViewModel for adding a new visit
                registrationViewModel.startNewVisitForExistingMother(childEntity)

                // Navigate to the visit registration screen
                findNavController().navigate(R.id.action_childDetailFragment_to_childRegistrationFragment2)
            }
        }

        observeViewModel(visitAdapter)
    }

    private fun observeViewModel(adapter: ChildVisitHistoryAdapter) {
        detailViewModel.childDetails.observe(viewLifecycleOwner) { mother ->
            mother?.let {
                binding.tvMotherNameDetail.text = it.name
                binding.tvMotherNikDetail.text = "NIK: ${it.nik}"
                // ADDED: Set new TextViews
                binding.tvMotherDobDetail.text = "Tanggal Lahir: ${it.dateOfBirth}"
                binding.tvMotherPhoneDetail.text = "No. HP: ${it.phoneNumber ?: '-'}"
                val address = "${it.fullAddress}, RT ${it.rtName}, RW ${it.rwName}, ${it.kelurahanName}"
                binding.tvMotherAddressDetail.text = address
                detailViewModel.calculateAllInterpretations()
            }
        }

        detailViewModel.visitHistory.observe(viewLifecycleOwner) { visits ->
            adapter.submitList(visits)
            detailViewModel.calculateAllInterpretations()
        }

        detailViewModel.nextVisitDateText.observe(viewLifecycleOwner) { text ->
            binding.tvNextVisitDate.text = text
            binding.tvNextVisitDate.isVisible = !text.isNullOrEmpty()
        }

        // Observe raw values
        detailViewModel.pregnancyWeekAgeText.observe(viewLifecycleOwner) { binding.tvInterpretationWeekAge.text = it }
        detailViewModel.tbjValueText.observe(viewLifecycleOwner) { binding.tvInterpretationTbjValue.text = it }

        // Observe all interpretation LiveData
        detailViewModel.interpretationAge.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationAge, it) }
        detailViewModel.interpretationChildCount.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationChildCount, it) }
        detailViewModel.interpretationTfu.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationTfu, it) }
        detailViewModel.interpretationImt.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationImt, it) }
        detailViewModel.interpretationDisease.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationDisease, it) }
        detailViewModel.interpretationHb.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationHb, it) }
        detailViewModel.interpretationLila.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationLila, it) }
        detailViewModel.interpretationTbj.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationTbj, it) }
        detailViewModel.interpretationWater.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationWater, it) }
        detailViewModel.interpretationBab.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationBab, it) }
        detailViewModel.interpretationSmoke.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationSmoke, it) }
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