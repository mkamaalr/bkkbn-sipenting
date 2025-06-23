package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Untuk menginjeksikan ViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherListBinding // Sesuaikan dengan nama binding Anda
import com.bkkbnjabar.sipenting.utils.Resource // Pastikan Resource diimpor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PregnantMotherListFragment : Fragment() {

    private var _binding: FragmentPregnantMotherListBinding? = null
    private val binding get() = _binding!!

    private val pregnantMotherListViewModel: PregnantMotherListViewModel by viewModels()

    private lateinit var pregnantMotherAdapter: PregnantMotherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPregnantMotherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupFabListener()
        pregnantMotherListViewModel.loadPregnantMothers()
    }

    private fun setupRecyclerView() {
        pregnantMotherAdapter = PregnantMotherAdapter { pregnantMother ->
            Toast.makeText(context, "Clicked: ${pregnantMother.name}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewPregnantMothers.adapter = pregnantMotherAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pregnantMotherListViewModel.pregnantMothersList.observe(viewLifecycleOwner) { resource ->
                    Log.d("PMR_LIST_OBSERVE", "PregnantMothersList observed new resource: ${resource.javaClass.simpleName}")
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
                        Resource.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerViewPregnantMothers.visibility = View.GONE
                            binding.tvEmptyListMessage.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setupFabListener() {
        binding.fabAddPregnantMother.setOnClickListener {
            Log.d("PregnantMotherListFragment", "FAB clicked, navigating to registration.")
            // Navigasi ke PregnantMotherRegistrationFragment1
            // Pastikan Anda memiliki aksi yang tepat di navigation graph Anda.
            // Contoh ID aksi: R.id.action_pregnantMotherListFragment_to_pregnantMotherRegistrationFragment1
            findNavController().navigate(R.id.action_pregnantMotherListFragment_to_pregnantMotherRegistrationFragment1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}