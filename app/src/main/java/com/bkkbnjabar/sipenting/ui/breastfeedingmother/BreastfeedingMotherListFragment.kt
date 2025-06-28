package com.bkkbnjabar.sipenting.ui.breastfeedingmother


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.databinding.FragmentBreastfeedingMotherListBinding
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.ui.breastfeedingmother.registration.BreastfeedingMotherRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreastfeedingMotherListFragment : Fragment() {

    private var _binding: FragmentBreastfeedingMotherListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreastfeedingMotherListViewModel by viewModels()
    private val registrationViewModel: BreastfeedingMotherRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreastfeedingMotherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val motherAdapter = BreastfeedingMotherAdapter { motherId ->
             val action = BreastfeedingMotherListFragmentDirections.actionNavBreastfeedingMotherListToBreastfeedingMotherDetailFragment1(motherId)
             findNavController().navigate(action)

        }

        binding.recyclerView.apply {
            adapter = motherAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.allBreastfeedingMothersForUI.observe(viewLifecycleOwner) { mothers ->
            motherAdapter.submitList(mothers)
            binding.tvEmptyListMessage.isVisible = mothers.isEmpty()
            binding.recyclerView.isVisible = mothers.isNotEmpty()
        }

        binding.fabAddMother.setOnClickListener {
            // Reset the form in the ViewModel before navigating
            registrationViewModel.resetForm()
            findNavController().navigate(R.id.action_nav_breastfeeding_mother_list_to_breastfeedingMotherRegistrationFragment1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}