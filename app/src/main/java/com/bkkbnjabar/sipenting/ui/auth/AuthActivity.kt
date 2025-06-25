package com.bkkbnjabar.sipenting.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bkkbnjabar.sipenting.databinding.ActivityAuthBinding
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that hosts all authentication-related fragments (e.g., Login, Register).
 * It observes the authentication state and navigates to MainActivity upon successful login.
 */
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe the navigation event from the ViewModel
        authViewModel.navigateToMain.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Clear the task stack and create a new one for MainActivity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Finish AuthActivity so the user cannot go back to it
    }
}
