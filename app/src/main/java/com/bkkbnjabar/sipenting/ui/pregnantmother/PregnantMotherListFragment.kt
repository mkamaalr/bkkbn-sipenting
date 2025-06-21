package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherListBinding
import com.bkkbnjabar.sipenting.ui.pregnantmother.registration.PregnantMotherRegistrationViewModel
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherListFragment : Fragment() {

    private var _binding: FragmentPregnantMotherListBinding? = null
    private val binding get() = _binding!!

    private val pregnantMotherListViewModel: PregnantMotherListViewModel by viewModels()
    private val pregnantMotherRegistrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    private lateinit var pregnantMotherAdapter: PregnantMotherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        Log.d("PMR_LIST_LIFECYCLE", "onCreateView called.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PMR_LIST_LIFECYCLE", "onViewCreated called.")
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        pregnantMotherListViewModel.loadPregnantMothers()
        Log.d("PMR_LIST_ACTION", "loadPregnantMothers() called.")
    }

    private fun setupRecyclerView() {
        pregnantMotherAdapter = PregnantMotherAdapter { pregnantMother ->
            Toast.makeText(context, "Clicked: ${pregnantMother.name}", Toast.LENGTH_SHORT).show()
            Log.d("PMR_LIST_ACTION", "Item clicked: ${pregnantMother.name}. Navigating to edit.")
            // Anda mungkin ingin menambahkan navigasi ke fragment edit di sini
            // registrationViewModel.loadPregnantMotherForEdit(pregnantMother.localId!!)
            // findNavController().navigate(R.id.action_pregnantMotherListFragment_to_pregnantMotherRegistrationFragment1)
        }
        binding.recyclerViewPregnantMothers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pregnantMotherAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddPregnantMother.setOnClickListener {
            Log.d("PMR_LIST_ACTION", "FAB clicked. Calling startNewRegistration() on ViewModel.")
            // PENTING: Reset ViewModel pendaftaran untuk memulai entri baru
            pregnantMotherRegistrationViewModel.startNewRegistration()
            findNavController().navigate(R.id.action_pregnantMotherListFragment_to_pregnantMotherRegistrationFragment1)
        }
    }

    private fun observeViewModel() {
        pregnantMotherListViewModel.pregnantMothersList.observe(viewLifecycleOwner) { resource ->
            Log.d("PMR_LIST_OBSERVE", "PregnantMothersList observed. Status: ${resource.javaClass.simpleName}")
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewPregnantMothers.visibility = View.GONE
                    binding.tvEmptyListMessage.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPregnantMothers.visibility = View.VISIBLE
                    pregnantMotherAdapter.submitList(resource.data)
                    if (resource.data.isNullOrEmpty()) {
                        binding.tvEmptyListMessage.visibility = View.VISIBLE
                        Log.d("PMR_LIST_OBSERVE", "Pregnant mothers list is empty.")
                    } else {
                        binding.tvEmptyListMessage.visibility = View.GONE
                        Log.d("PMR_LIST_OBSERVE", "Pregnant mothers list updated: ${resource.data.size} items.")
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPregnantMothers.visibility = View.GONE
                    binding.tvEmptyListMessage.visibility = View.VISIBLE
                    binding.tvEmptyListMessage.text = "Gagal memuat data: ${resource.message}"
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    Log.e("PMR_LIST_OBSERVE", "Error loading pregnant mothers: ${resource.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PMR_LIST_LIFECYCLE", "onDestroyView called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d("PMR_LIST_LIFECYCLE", "onResume called. Reloading pregnant mothers list.")
        // Pastikan daftar di-refresh setiap kali fragment terlihat lagi
        pregnantMotherListViewModel.loadPregnantMothers()
    }
}
