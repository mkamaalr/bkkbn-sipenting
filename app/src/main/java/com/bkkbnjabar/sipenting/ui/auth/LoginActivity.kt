package com.bkkbnjabar.sipenting.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bkkbnjabar.sipenting.R // Pastikan R diimpor dengan benar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set layout yang akan menampung LoginFragment
        setContentView(R.layout.activity_login)
    }
}