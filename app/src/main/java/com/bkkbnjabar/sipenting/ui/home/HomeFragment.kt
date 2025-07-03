package com.bkkbnjabar.sipenting.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.FragmentHomeBinding
import com.bkkbnjabar.sipenting.ui.main.MainViewModel
import com.bkkbnjabar.sipenting.ui.main.PieChartData
import com.bkkbnjabar.sipenting.ui.main.SyncStatus
import com.github.mikephil.charting.charts.PieChart
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

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
        setupPieChart(binding.pieChartAge)
        setupPieChart(binding.pieChartImmunization)
        observeViewModel()

    }

    private fun setupClickListeners() {
        // Set a click listener on the home icon
        binding.ivHomeIcon.setOnClickListener { view ->
            // Show the popup menu, anchored to the icon itself
            showOverflowMenu(view)
        }

        binding.cardSyncData.setOnClickListener {
            mainViewModel.startSync()
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

//        mainViewModel.logoutEvent.observe(viewLifecycleOwner) { event ->
//            event.getContentIfNotHandled()?.let {
//                if (it) {
//                    findNavController().navigate(R.id.action_global_to_auth_nav_graph)
//                }
//            }
//        }

        // Observe the sync status to provide visual feedback to the user
        mainViewModel.syncStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SyncStatus.IN_PROGRESS -> {
                    binding.cardSyncData.isEnabled = false
                    binding.tvSyncText.text = "Sinkronisasi..."
                    binding.ivSyncIcon.setImageResource(R.drawable.ic_sync_pending)
                }
                SyncStatus.SUCCESS -> {
                    binding.cardSyncData.isEnabled = true
                    binding.tvSyncText.text = "Sync Data Berhasil"
                    binding.ivSyncIcon.setImageResource(R.drawable.ic_sync_done)
                    Toast.makeText(context, "Sinkronisasi data berhasil!", Toast.LENGTH_SHORT).show()
                }
                SyncStatus.ERROR -> {
                    binding.cardSyncData.isEnabled = true
                    binding.tvSyncText.text = "Gagal, Coba Lagi"
                    binding.ivSyncIcon.setImageResource(R.drawable.ic_sync_error)
                    Toast.makeText(context, "Sinkronisasi gagal, silakan coba lagi.", Toast.LENGTH_LONG).show()
                }
                else -> { // IDLE
                    binding.cardSyncData.isEnabled = true
                    binding.tvSyncText.text = "Sync Data"
                    binding.ivSyncIcon.setImageResource(R.drawable.ic_sync_pending)
                }
            }
        }

        // Observer for the last sync timestamp
        mainViewModel.lastSyncTimestamp.observe(viewLifecycleOwner) { timestamp ->
            if (timestamp > 0) {
                // Format the timestamp into a readable date and time
                val sdf = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID"))
                binding.tvLastSync.text = "Terakhir sinkron: ${sdf.format(Date(timestamp))}"
                binding.tvLastSync.visibility = View.VISIBLE
            } else {
                // If the timestamp is 0, it means no sync has happened yet
                binding.tvLastSync.text = "Belum pernah sinkronisasi"
                binding.tvLastSync.visibility = View.VISIBLE
            }
        }

        mainViewModel.ageChartData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                val colors = listOf(
                    ContextCompat.getColor(requireContext(), R.color.chart_green),
                    ContextCompat.getColor(requireContext(), R.color.chart_yellow)
                )
                updatePieChartData(binding.pieChartAge, data, colors)
            }
        }

        mainViewModel.immunizationChartData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                // Define the colors for this specific chart
                val colors = listOf(
                    ContextCompat.getColor(requireContext(), R.color.chart_green), // Tidak Lengkap
                    ContextCompat.getColor(requireContext(), R.color.chart_yellow) // Lengkap
                )
                updatePieChartData(binding.pieChartImmunization, data, colors)
            }
        }

    }

    private fun setupPieChart(chart: PieChart) {
        chart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.isEnabled = true
        }
    }

    private fun updatePieChartData(chart: PieChart, data: List<PieChartData>, colors: List<Int>) {
        val entries = ArrayList<PieEntry>()
        for (item in data) {
            entries.add(PieEntry(item.value, item.label))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(chart))
        pieData.setValueTextSize(14f)
        pieData.setValueTextColor(Color.BLACK)

        chart.data = pieData
        chart.invalidate() // Refresh the chart
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
