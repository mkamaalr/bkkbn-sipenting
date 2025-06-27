package com.bkkbnjabar.sipenting.ui.pregnantmother.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bkkbnjabar.sipenting.databinding.FragmentPregnantMotherDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PregnantMotherDetailFragment : Fragment() {

    private var _binding: FragmentPregnantMotherDetailBinding? = null
    private val binding get() = _binding!!

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

        binding.fabAddVisit.setOnClickListener {
            // TODO: Navigasi ke halaman tambah kunjungan baru dengan membawa motherId
        }

        observeViewModel(visitAdapter)
    }

    private fun observeViewModel(adapter: VisitHistoryAdapter) {
        viewModel.motherDetails.observe(viewLifecycleOwner) { mother ->
            mother?.let {
                binding.tvMotherNameDetail.text = it.name
                binding.tvMotherNikDetail.text = "NIK: ${it.nik}"
                val address = "${it.fullAddress}, RT ${it.rtName}, RW ${it.rwName}, ${it.kelurahanName}"
                binding.tvMotherAddressDetail.text = address
                viewModel.calculateInterpretation() // Hitung interpretasi setelah data ibu ada
            }
        }

        viewModel.visitHistory.observe(viewLifecycleOwner) { visits ->
            adapter.submitList(visits)
            viewModel.calculateInterpretation() // Hitung ulang jika riwayat kunjungan berubah
        }

        viewModel.interpretationText.observe(viewLifecycleOwner) { text ->
            binding.tvInterpretationResult.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
