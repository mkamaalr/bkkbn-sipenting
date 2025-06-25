package com.bkkbnjabar.sipenting.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.ActivityMainBinding
import com.bkkbnjabar.sipenting.ui.auth.AuthActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.navViewBottom

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_pregnant_mother_list, R.id.nav_child_list, R.id.nav_breastfeeding_mother_list)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        observeViewModel()
        setupBottomNavVisibility()
    }

    // ================== MENAMBAHKAN FUNGSI INI ==================
    private fun setupBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Tampilkan BottomNav pada destinasi level atas
                R.id.nav_home,
                R.id.nav_pregnant_mother_list,
                R.id.nav_child_list,
                R.id.nav_breastfeeding_mother_list -> {
                    binding.navViewBottom.visibility = View.VISIBLE
                }
                // Sembunyikan untuk layar lainnya
                else -> {
                    binding.navViewBottom.visibility = View.GONE
                }
            }
        }
    }
    // ============================================================

    // ... (onCreateOptionsMenu dan observeViewModel tetap sama) ...
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        // ...
    }

    // ================== MENGEMBALIKAN FUNGSI INI ==================
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    // ==============================================================
}
