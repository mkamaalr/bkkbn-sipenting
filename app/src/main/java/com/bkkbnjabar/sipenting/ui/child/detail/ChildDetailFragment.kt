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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentChildDetailBinding
import com.bkkbnjabar.sipenting.domain.model.InterpretationResult
import com.bkkbnjabar.sipenting.ui.child.registration.ChildRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildDetailFragment : Fragment() {

    private var _binding: FragmentChildDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChildDetailViewModel by viewModels()
    private val args: ChildDetailFragmentArgs by navArgs()
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

        val visitAdapter = ChildVisitHistoryAdapter { visitEntity ->
            val action = ChildDetailFragmentDirections
                .actionChildDetailFragmentToChildVisitEditFragment(visitEntity.localVisitId)
            findNavController().navigate(action)
        }

        binding.recyclerViewVisits.apply {
            adapter = visitAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAddVisit.setOnClickListener {
            viewModel.childDetails.value?.let { childEntity ->
                // Tell the shared Registration ViewModel to prepare for a new visit for this specific child
                registrationViewModel.startNewVisitForExistingChild(childEntity)

                // Now, perform the navigation
                val action = ChildDetailFragmentDirections.actionChildDetailFragmentToChildRegistrationFragment2()
                // 3. Navigate to the registration screen.
                findNavController().navigate(action)
            }
        }

        observeViewModel(visitAdapter)
    }

    private fun observeViewModel(adapter: ChildVisitHistoryAdapter) {

        // Use the single MediatorLiveData to react to data changes
        viewModel.detailDataMediator.observe(viewLifecycleOwner, Observer { (child, visits) ->
            child?.let {
                binding.tvChildNameDetail.text = it.name
                binding.tvChildNikDetail.text = "NIK: ${it.nik}"
            }

            adapter.submitList(visits)

            // Trigger calculation only once when data from either source changes
            viewModel.calculateAllInterpretations()
        })

        // --- Observers for each Health Interpretation value ---
        viewModel.interpretationAge.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationAge, it) }
        viewModel.interpretationWeight.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationWeight, it) }
        viewModel.interpretationHeight.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationHeight, it) }
        viewModel.interpretationHeadCircumference.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationHeadCircumference, it) }
        viewModel.interpretationAsi.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationAsi, it) }
        viewModel.interpretationMpasi.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationMpasi, it) }
        viewModel.interpretationPosyandu.observe(viewLifecycleOwner) { updateInterpretationUI(binding.tvInterpretationPosyandu, it) }
    }

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
