package com.bkkbnjabar.sipenting.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentHomeBinding
import com.bkkbnjabar.sipenting.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Set a click listener on the home icon
        binding.ivHomeIcon.setOnClickListener { view ->
            // Show the popup menu, anchored to the icon itself
            showOverflowMenu(view)
        }

        // Pregnant Mother Menu
        binding.menuPregnantMother.setOnClickListener {
//            findNavController().navigate(R.id.action_nav_home_to_nav_pregnant_mother_list)
        }

        // Breastfeeding Mother Menu
        binding.menuBreastfeedingMother.setOnClickListener {
//            findNavController().navigate(R.id.action_nav_home_to_nav_breastfeeding_mother_list)
        }

        // Child Menu
        binding.menuChild.setOnClickListener {
//            findNavController().navigate(R.id.action_nav_home_to_nav_child_list)
        }
    }

    private fun observeViewModel() {
        // Observe user data to personalize the header card
        mainViewModel.userSession.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                binding.tvUserId.text = it.name // Assuming NIK is the ID shown
                binding.tvTpkName.text = "TPK ${it.name}" // Example
                binding.tvLocation1.text = "${it.name} - ${it.name}"
                binding.tvLocation2.text = "${it.name} - ${it.name}"
//                binding.tvUserId.text = it.nik // Assuming NIK is the ID shown
//                binding.tvTpkName.text = "TPK ${it.kelurahanName}" // Example
//                binding.tvLocation1.text = "${it.provinsiName} - ${it.kabupatenName}"
//                binding.tvLocation2.text = "${it.kecamatanName} - ${it.kelurahanName}"
            }
        }
    }

    /**
     * Creates and shows a PopupMenu that mimics the Toolbar's overflow menu.
     * @param anchorView The view (the icon) to which the popup will be anchored.
     */
    private fun showOverflowMenu(anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        // Inflate the same menu that your MainActivity uses for the toolbar
        popup.menuInflater.inflate(R.menu.main, popup.menu)

        // Set a listener to handle clicks on the menu items
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
//                R.id.action_logout -> {
//                    // Handle Settings click, for example, navigate to a SettingsFragment
//                    // findNavController().navigate(R.id.action_global_to_settingsFragment)
//                    Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show()
//                    true
//                }
                R.id.action_logout -> {
                    // Handle Logout click
                    mainViewModel.logout()
                    true
                }
                else -> false
            }
        }
        // Show the menu
        popup.show()
    }

    override fun onResume() {
        super.onResume()
        // Hide the main toolbar when this fragment is shown
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        // Show the main toolbar again when leaving this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
