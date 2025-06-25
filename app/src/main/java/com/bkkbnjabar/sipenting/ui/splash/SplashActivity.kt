package com.bkkbnjabar.sipenting.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.ui.auth.AuthActivity
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Mulai preloading data di background
        viewModel.startPreloading()

        // Amati status preloading dari ViewModel
        viewModel.isPreloadingComplete.observe(this) { isComplete ->
            if (isComplete) {
                // Jika sudah selesai, baru lakukan navigasi
                checkLoginStatusAndNavigate()
            }
        }
    }

    private fun checkLoginStatusAndNavigate() {
        val destination = if (viewModel.isUserLoggedIn()) {
            MainActivity::class.java
        } else {
            AuthActivity::class.java
        }

        val intent = Intent(this, destination).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Tutup SplashActivity
    }
}
