package com.bkkbnjabar.sipenting.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bkkbnjabar.sipenting.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Penting: Hilt akan menyuntikkan komponen ke dalam Activity ini
class AuthActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Muat LoginFragment jika ini pertama kalinya
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commit()
        }
    }
}