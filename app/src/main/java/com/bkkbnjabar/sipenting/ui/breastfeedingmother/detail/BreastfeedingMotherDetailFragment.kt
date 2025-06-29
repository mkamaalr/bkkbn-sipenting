package com.bkkbnjabar.sipenting.ui.breastfeedingmother.detail

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentBreastfeedingMotherDetailBinding
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import com.bkkbnjabar.sipenting.ui.breastfeedingmother.registration.BreastfeedingMotherRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreastfeedingMotherDetailFragment : Fragment() {

    private var _binding: FragmentBreastfeedingMotherDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreastfeedingMotherDetailViewModel by viewModels()
    private val args: BreastfeedingMotherDetailFragmentArgs by navArgs()
    private val registrationViewModel: BreastfeedingMotherRegistrationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreastfeedingMotherDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMotherId(args.motherId)

        val visitAdapter = BreastfeedingMotherVisitHistoryAdapter { visitEntity ->
            val action = BreastfeedingMotherDetailFragmentDirections
                .actionBreastfeedingMotherDetailFragmentToBreastfeedingMotherVisitEditFragment(visitEntity.localVisitId)
            findNavController().navigate(action)
        }

        binding.recyclerViewVisits.apply {
            adapter = visitAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAddVisit.setOnClickListener {
            viewModel.motherDetails.value?.let { motherEntity ->
                registrationViewModel.startNewVisitForExistingMother(motherEntity)
                findNavController().navigate(R.id.action_breastfeedingMotherDetailFragment_to_breastfeedingMotherRegistrationFragment2)
            }
        }

        // This single call sets up all the observers.
        observeViewModel(visitAdapter)
    }

    /**
     * This function now contains the COMPLETE logic for observing all
     * necessary LiveData from the ViewModel and updating the UI.
     */
    private fun observeViewModel(adapter: BreastfeedingMotherVisitHistoryAdapter) {

        // --- Observer for Mother's Details ---
        viewModel.motherDetails.observe(viewLifecycleOwner) { mother ->
            mother?.let {
                // Populate all mother detail TextViews
                binding.tvMotherNameDetail.text = it.name
                binding.tvMotherNikDetail.text = "NIK: ${it.nik}"
                binding.tvMotherDobDetail.text = "Tanggal Lahir: ${it.dateOfBirth}"
                binding.tvMotherPhoneDetail.text = "No. HP: ${it.phoneNumber ?: "-"}"

                // Assuming address fields are available in your BreastfeedingMotherEntity
                val address = "${it.fullAddress}, RT ${it.rtName}, RW ${it.rwName}, ${it.kelurahanName}"
                binding.tvMotherAddressDetail.text = address
            }
        }

        // --- Observer for Visit History ---
        viewModel.visitHistory.observe(viewLifecycleOwner) { visits ->
            // Submit the list of visits to the RecyclerView adapter
            adapter.submitList(visits)

            // Trigger the ViewModel to calculate interpretations based on the new visit list
            viewModel.calculateAllInterpretations()
        }

        // --- Observers for each Health Interpretation value ---
        // These will be triggered after calculateAllInterpretations() runs.
        viewModel.interpretationComplication.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationComplication, it) }
        viewModel.interpretationKb.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationKb, it) }
        viewModel.interpretationWater.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationWater, it) }
        viewModel.interpretationBab.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationBab, it) }
        viewModel.interpretationSmoke.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationSmoke, it) }
    }

    /**
     * A helper function to update a TextView with the interpretation result,
     * including text and color, exactly like in PregnantMotherDetailFragment.
     */
    private fun updateInterpretationUI(textView: TextView, result: InterpretationResult?) {
        result ?: return
        textView.text = result.text
        textView.setTextColor(ContextCompat.getColor(requireContext(), result.color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}