package com.bkkbnjabar.sipenting.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.ActivityMainBinding
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.ui.auth.AuthActivity
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var authRepository: AuthRepository // Suntikkan AuthRepository untuk logout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pengaturan Toolbar
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // Konfigurasi AppBarConfiguration untuk tujuan tingkat atas (tidak ada tombol Up untuk ini)
        // ID ini harus cocok dengan ID di bottom_nav_menu.xml DAN main_nav_graph.xml
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.pregnantMotherListFragment,
                R.id.breastfeedingMotherListFragment,
                R.id.childListFragment
            )
        )

        // Hubungkan Komponen Navigasi dengan Toolbar (menangani perubahan judul dan tombol Up)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Hubungkan Komponen Navigasi dengan BottomNavigationView
        binding.bottomNavView.setupWithNavController(navController)

        // Opsional: Dengarkan perubahan tujuan untuk menyembunyikan/menampilkan navigasi bawah atau toolbar jika diperlukan
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Contoh: Jika Anda ingin menyembunyikan navigasi bawah pada formulir
            // if (destination.id == R.id.pregnantMotherRegistrationFragment1) {
            //     binding.bottomNavView.visibility = View.GONE
            // } else {
            //     binding.bottomNavView.visibility = View.VISIBLE
            // }
        }
    }

    // Mengembang menu toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Tangani klik item menu toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                Toast.makeText(this, "Profil diklik!", Toast.LENGTH_SHORT).show()
                // TODO: Navigasi ke Fragment Profil atau Aktivitas
                true
            }
            R.id.action_logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            when (val result = authRepository.logout()) {
                is Resource.Success -> {
                    Toast.makeText(this@MainActivity, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this@MainActivity, "Logout gagal: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    // Seharusnya tidak terjadi karena logout biasanya cepat atau ditangani oleh UI
                }
            }
        }
    }

    // Tangani tombol Up (panah kembali) di Toolbar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}