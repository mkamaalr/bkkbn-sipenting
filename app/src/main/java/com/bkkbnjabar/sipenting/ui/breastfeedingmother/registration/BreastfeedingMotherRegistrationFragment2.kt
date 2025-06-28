package com.bkkbnjabar.sipenting.ui.breastfeedingmother.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentBreastfeedingMotherRegistration2Binding
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreastfeedingMotherRegistrationFragment2 : Fragment() {

    private var _binding: FragmentBreastfeedingMotherRegistration2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: BreastfeedingMotherRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreastfeedingMotherRegistration2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveAllData()
        }

        binding.rgOnContraception.setOnCheckedChangeListener { _, checkedId ->
            binding.tilContraceptionOption.isVisible = (checkedId == R.id.rb_on_contraception_yes)
            if (checkedId != R.id.rb_on_contraception_yes) {
                binding.etContraceptionOption.setText("", false)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            val isLoading = result is Resource.Loading
            // binding.progressBar.isVisible = isLoading // Assuming you add a ProgressBar
            binding.btnSave.isEnabled = !isLoading
            binding.btnPrevious.isEnabled = !isLoading

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_LONG).show()
                    viewModel.resetForm()
                    findNavController().navigate(R.id.action_breastfeedingMotherRegistrationFragment2_to_nav_breastfeeding_mother_list)
                }
                is Resource.Error -> Toast.makeText(context, "Gagal menyimpan: ${result.message}", Toast.LENGTH_LONG).show()
                else -> {}
            }
        }

        viewModel.deliveryPlaces.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { item -> item.name })
            binding.etDeliveryPlace.setAdapter(adapter)
        }
        viewModel.contraceptionOptions.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.map { item -> item.name })
            binding.etContraceptionOption.setAdapter(adapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}