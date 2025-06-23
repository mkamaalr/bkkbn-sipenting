package com.bkkbnjabar.sipenting.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.databinding.ActivitySplashBinding
import com.bkkbnjabar.sipenting.ui.auth.AuthViewModel
import com.bkkbnjabar.sipenting.ui.auth.LoginActivity
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels() // Gunakan SplashViewModel Anda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observasi hasil validasi token dari AuthViewModel
        authViewModel.tokenValidationResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("SplashActivity", "Validating token: Loading...")
                }
                is Resource.Success -> {
                    val isValid = resource.data ?: false
                    if (isValid) {
                        Log.d("SplashActivity", "Token valid, navigating to MainActivity.")
                        splashViewModel.loadInitialLookupData()
                        navigateToMain()
                    } else {
                        Log.d("SplashActivity", "Token invalid, navigating to LoginActivity.")
                        navigateToLogin()
                    }
                    // TIDAK PERLU MERESET DI SINI! SplashActivity akan segera di-finish.
                    // authViewModel.resetTokenValidationResult()
                }
                is Resource.Error -> {
                    Log.e("SplashActivity", "Token validation error: ${resource.message}, navigating to LoginActivity.")
                    navigateToLogin()
                    // TIDAK PERLU MERESET DI SINI! SplashActivity akan segera di-finish.
                    // authViewModel.resetTokenValidationResult()
                }
                Resource.Idle -> {
                    // Jika ViewModel sudah diinisialisasi ke Idle, kita tidak perlu memicu lagi dari sini.
                    // Ini adalah state awal atau state setelah reset dari ViewModel itu sendiri.
                    // Logika pemicu validasi harus ada di init{} ViewModel atau dipanggil SEKALI.
                    Log.d("SplashActivity", "Token validation is in Idle state (no action needed from observer).")
                }
            }
        }

        // Observasi status pemuatan data lookup
        splashViewModel.lookupDataLoadResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("SplashActivity", "Loading lookup data: Loading...")
                    // Tampilkan indikator loading jika perlu
                }
                is Resource.Success -> {
                    Log.d("SplashActivity", "Lookup data loaded successfully. Navigating to MainActivity.")
                    // Setelah data lookup berhasil dimuat, baru navigasi ke Main
                    navigateToMain()
                }
                is Resource.Error -> {
                    Log.e("SplashActivity", "Failed to load lookup data: ${resource.message}. Navigating to MainActivity anyway (or show error/retry).")
                    // Jika data lookup gagal dimuat, Anda bisa memilih untuk:
                    // 1. Tetap navigasi ke main (dengan risiko fitur tergantung data lookup tidak berfungsi)
                    // 2. Tampilkan pesan error dan opsi coba lagi
                    // 3. Tampilkan pesan error dan navigasi ke login (jika data lookup sangat kritis)
                    // Untuk saat ini, kita navigasi ke Main.
                    navigateToMain()
                }
                Resource.Idle -> {
                    // Hanya state awal atau setelah reset
                }
            }
        }

        // Panggil validasi token hanya SEKALI saat Activity dibuat.
        // Observer akan menangani transisi state selanjutnya.
        // Hapus kondisi 'if (authViewModel.tokenValidationResult.value == Resource.Idle)' dari observer.
        authViewModel.validateToken()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Tutup SplashActivity agar pengguna tidak bisa kembali
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Tutup SplashActivity agar pengguna tidak bisa kembali
    }

    // Tidak perlu companion object SPLASH_DELAY_MS jika tidak ada delay
}

