package com.bkkbnjabar.sipenting.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController // Diperlukan untuk BottomNavigationView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.ActivityMainBinding
import com.bkkbnjabar.sipenting.ui.auth.LoginActivity
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur Toolbar dari app_bar_main sebagai ActionBar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Menemukan NavHostFragment berdasarkan ID baru dari activity_main.xml
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Definisikan top-level destinations untuk AppBarConfiguration.
        // Ini adalah ID fragment yang akan muncul di BottomNavigationView
        // dan tidak akan memiliki tombol back di AppBar saat berada di sana.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,                 // Sesuai dengan bottom_nav_menu.xml
            R.id.pregnantMotherListFragment,   // Sesuai dengan bottom_nav_menu.xml
            R.id.breastfeedingMotherListFragment, // Sesuai dengan bottom_nav_menu.xml
            R.id.childListFragment             // Sesuai dengan bottom_nav_menu.xml
            // Hapus nav_gallery dan nav_slideshow jika tidak ada di bottom_nav_menu Anda
        ))

        // Setup ActionBar dengan NavController dan AppBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup BottomNavigationView dengan NavController
        // FIXED: Menggunakan ID nav_view_bottom dari XML Anda
        binding.navViewBottom.setupWithNavController(navController)

        // Observasi status autentikasi dari MainViewModel
        // Ini adalah logika yang Anda inginkan untuk redirect ke LoginActivity
        mainViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (!isAuthenticated) {
                Log.d("MainActivity", "User not authenticated, navigating to LoginActivity.")
                navigateToLogin()
            } else {
                Log.d("MainActivity", "User is authenticated.")
                // Opsional: Muat data pengguna atau lakukan inisialisasi lain setelah autentikasi dikonfirmasi
                // Jika Anda sebelumnya memiliki loadUserDisplayName(), logika serupa bisa diletakkan di sini
            }
        }

        // Observasi status logout dari MainViewModel
        observeLogoutViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Mengembangkan menu untuk Top App Bar (biasanya untuk Logout atau Settings)
        menuInflater.inflate(R.menu.main, menu) // Pastikan Anda memiliki main_menu.xml di res/menu/
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Tangani klik item menu di Top App Bar
        return when (item.itemId) {
            R.id.action_logout -> { // ID item logout dari res/menu/main.xml
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Melakukan proses logout.
     */
    private fun performLogout() {
        mainViewModel.logout()
    }

    /**
     * Mengamati status logout dari ViewModel dan menavigasi.
     */
    private fun observeLogoutViewModel() {
        mainViewModel.logoutResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Logout: Loading...")
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Anda telah berhasil logout!", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Logout: Success. Navigating to LoginActivity.")
                    navigateToLogin()
                    mainViewModel.resetLogoutResult() // Reset state setelah sukses
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Gagal logout: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("MainActivity", "Logout: Error - ${resource.message}")
                    mainViewModel.resetLogoutResult() // Reset state setelah error
                }
                Resource.Idle -> {
                    Log.d("MainActivity", "Logout state is Idle.")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Ini menangani tombol "Up" (panah kembali) di AppBar saat navigasi antar fragment
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus back stack
        startActivity(intent)
        finish() // Tutup MainActivity
    }
}