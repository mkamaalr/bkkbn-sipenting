package com.bkkbnjabar.sipenting.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPregnantMotherList.setOnClickListener {
            findNavController().navigate(R.id.pregnantMotherListFragment) // Navigasi langsung ke tujuan tingkat atas
        }
        binding.btnBreastfeedingMotherList.setOnClickListener {
            findNavController().navigate(R.id.breastfeedingMotherListFragment) // Navigasi langsung ke tujuan tingkat atas
        }
        binding.btnChildList.setOnClickListener {
            findNavController().navigate(R.id.childListFragment) // Navigasi langsung ke tujuan tingkat atas
        }
        // TODO: Tambahkan listener untuk daftar/formulir lain dari Home
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}