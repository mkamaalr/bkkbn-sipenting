package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherListBinding
import com.bkkbnjabar.sipenting.ui.pregnantmother.registration.PregnantMotherRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherListFragment : Fragment() {

    private var _binding: FragmentPregnantMotherListBinding? = null
    private val binding get() = _binding!!

    private val listViewModel: PregnantMotherListViewModel by viewModels()
    private val registrationViewModel: PregnantMotherRegistrationViewModel by activityViewModels()

    // ================== PERBAIKAN UTAMA DI SINI ==================
    // Adapter diinisialisasi langsung saat kelas dibuat, bukan dengan lateinit.
    // Ini menjamin adapter selalu ada sebelum dibutuhkan.
    private val pregnantMotherAdapter = PregnantMotherAdapter { mother ->
        // Saat item diklik, navigasi ke halaman detail dengan membawa ID ibu
        val action = PregnantMotherListFragmentDirections.actionNavPregnantMotherListToPregnantMotherDetailFragment(mother.localId)
        findNavController().navigate(action)
    }
    // ============================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPregnantMotherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Urutan pemanggilan di sini sekarang aman.
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPregnantMothers.apply {
            // Langsung gunakan adapter yang sudah ada.
            adapter = pregnantMotherAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.fabAddPregnantMother.setOnClickListener {
            registrationViewModel.resetForm()
            findNavController().navigate(R.id.action_nav_pregnant_mother_list_to_pregnantMotherRegistrationFragment1)
        }
    }

    private fun observeViewModel() {
        listViewModel.allPregnantMothers.observe(viewLifecycleOwner) { mothers ->
            // Sekarang aman untuk memanggil submitList kapan pun.
            pregnantMotherAdapter.submitList(mothers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Praktik yang baik untuk menghindari memory leak pada RecyclerView
        binding.recyclerViewPregnantMothers.adapter = null
        _binding = null
    }
}
