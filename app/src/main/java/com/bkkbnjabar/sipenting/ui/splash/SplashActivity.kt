package com.bkkbnjabar.sipenting.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bkkbnjabar.sipenting.ui.auth.AuthActivity
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity: AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tidak perlu XML layout jika menggunakan tema windowBackground

        observeNavigation()
        // Opsional, tambahkan penundaan artifisial untuk layar splash
        lifecycleScope.launch { // Menggunakan lifecycleScope.launch langsung
            delay(2000) // Penundaan 2 detik
            viewModel.checkAuthentication() // Memicu panggilan API atau pemeriksaan autentikasi
        }
    }

    private fun observeNavigation() {
        viewModel.navigateTo.observe(this) { destination ->
            when (destination) {
                SplashViewModel.NavigationDestination.LOGIN -> {
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                SplashViewModel.NavigationDestination.MAIN -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish() // Selesaikan aktivitas splash agar pengguna tidak dapat kembali
        }
    }
}