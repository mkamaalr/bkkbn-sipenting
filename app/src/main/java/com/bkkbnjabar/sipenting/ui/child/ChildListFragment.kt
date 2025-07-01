package com.bkkbnjabar.sipenting.ui.child

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
import com.bkkbnjabar.sipenting.databinding.FragmentChildListBinding
import com.bkkbnjabar.sipenting.ui.child.registration.ChildRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildListFragment : Fragment() {

    private var _binding: FragmentChildListBinding? = null
    private val binding get() = _binding!!

    // Use viewModels for the ViewModel specific to this screen's list
    private val viewModel: ChildListViewModel by viewModels()

    // Use activityViewModels for the ViewModel shared across the registration flow
    // This is needed to reset the form before starting a new registration
    private val registrationViewModel: ChildRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the adapter. It now expects to receive the mother's ID on click.
        val motherAdapter = ChildAdapter { motherId ->
            val action = ChildListFragmentDirections.actionNavChildListToChildDetailFragment(motherId)
            findNavController().navigate(action)
        }

        // Setup the RecyclerView
        binding.recyclerView.apply {
            adapter = motherAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Observe the final, UI-ready list from the ViewModel
        viewModel.allChildsForUI.observe(viewLifecycleOwner) { mothers ->
            motherAdapter.submitList(mothers)

            // Show an empty message if the list is empty
            binding.tvEmptyListMessage.isVisible = mothers.isEmpty()
            binding.recyclerView.isVisible = mothers.isNotEmpty()
        }

        // Setup the Floating Action Button to add a new mother
        binding.fabAddMother.setOnClickListener {
            // Reset the registration form to ensure it's empty for a new entry
            registrationViewModel.resetForm()
            // Navigate to the first screen of the registration flow
            val action = ChildListFragmentDirections.actionNavChildListToChildRegistrationFragment1()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}