package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Untuk mendapatkan ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager // Untuk layout RecyclerView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherListBinding
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherListFragment : Fragment() {

    private var _binding: FragmentPregnantMotherListBinding? = null
    private val binding get() = _binding!!

    // Menggunakan ViewModel baru untuk daftar
    private val viewModel: PregnantMotherListViewModel by viewModels()

    private lateinit var pregnantMotherAdapter: PregnantMotherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant_mother_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Panggil untuk memuat data saat fragment dibuat
        viewModel.loadPregnantMothers()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan listener klik
        pregnantMotherAdapter = PregnantMotherAdapter { pregnantMother ->
            // TODO: Implementasikan aksi ketika item daftar diklik (misalnya, navigasi ke detail/edit)
            Toast.makeText(context, "Clicked: ${pregnantMother.name}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewPregnantMothers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pregnantMotherAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddPregnantMother.setOnClickListener {
            findNavController().navigate(R.id.action_pregnantMotherListFragment_to_pregnantMotherRegistrationFragment1)
        }
        // TODO: Tambahkan listener untuk tombol upload (jika ada)
        // Misalnya: binding.btnUpload.setOnClickListener { viewModel.uploadPendingPregnantMothers() }
    }

    private fun observeViewModel() {
        viewModel.pregnantMothersList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewPregnantMothers.visibility = View.GONE
                    binding.tvEmptyListMessage.visibility = View.GONE // Sembunyikan pesan kosong saat loading
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPregnantMothers.visibility = View.VISIBLE
                    // Submit daftar ke adapter
                    pregnantMotherAdapter.submitList(resource.data)
                    if (resource.data.isNullOrEmpty()) {
                        binding.tvEmptyListMessage.visibility = View.VISIBLE
                    } else {
                        binding.tvEmptyListMessage.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPregnantMothers.visibility = View.GONE
                    binding.tvEmptyListMessage.visibility = View.VISIBLE
                    binding.tvEmptyListMessage.text = "Gagal memuat data: ${resource.message}"
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
