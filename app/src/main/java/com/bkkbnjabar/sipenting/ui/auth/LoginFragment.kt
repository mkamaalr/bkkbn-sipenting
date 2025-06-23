package com.bkkbnjabar.sipenting.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.auth.LoginRequest
import com.bkkbnjabar.sipenting.databinding.FragmentLoginBinding
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // FIXED: Menggunakan binding.btnLogin sesuai ID di XML
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Contoh TextViews yang tidak ada di XML yang kamu berikan,
        // jadi komentar ini akan tetap ada sebagai referensi jika kamu menambahkannya.
        // binding.tvForgotPassword.setOnClickListener {
        //     Toast.makeText(context, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show()
        // }
        // binding.tvRegister.setOnClickListener {
        //     Toast.makeText(context, "Fitur registrasi belum tersedia", Toast.LENGTH_SHORT).show()
        // }
    }

    private fun performLogin() {
        val inputIdentifier = binding.etUsernameEmail.text.toString().trim() // Input dari field tunggal
        val password = binding.etPassword.text.toString().trim()

        if (inputIdentifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Nama Pengguna/Email dan Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan loading spinner
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false // Nonaktifkan tombol login

        // Logika untuk menentukan apakah input adalah email atau username
        val loginRequest: LoginRequest
        if (Patterns.EMAIL_ADDRESS.matcher(inputIdentifier).matches()) {
            // Jika input terlihat seperti email, kirim ke parameter 'email'
            loginRequest = LoginRequest(email = inputIdentifier, password = password)
            Log.d("LoginFragment", "Attempting login with email: $inputIdentifier")
        } else {
            // Jika tidak, kirim ke parameter 'username'
            loginRequest = LoginRequest(username = inputIdentifier, password = password)
            Log.d("LoginFragment", "Attempting login with username: $inputIdentifier")
        }

        loginViewModel.login(loginRequest)
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            // Sembunyikan loading spinner dan aktifkan kembali tombol
            // FIXED: Menggunakan binding.progressBar dan binding.btnLogin sesuai ID di XML
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true

            when (resource) {
                is Resource.Loading -> {
                    Log.d("LoginFragment", "Login Loading...")
                    // FIXED: Menggunakan binding.progressBar dan binding.btnLogin sesuai ID di XML
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    val userSession = resource.data
                    Toast.makeText(context, "Login Berhasil! Selamat datang, ${userSession?.userName}", Toast.LENGTH_SHORT).show()
                    Log.d("LoginFragment", "Login Success for user: ${userSession?.userName}")
                    navigateToMain()
                    loginViewModel.resetLoginResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Login Gagal: ${resource.message}", Toast.LENGTH_LONG).show()
                    Log.e("LoginFragment", "Login Error: ${resource.message}")
                    loginViewModel.resetLoginResult()
                }
                Resource.Idle -> {
                    Log.d("LoginFragment", "Login state is Idle.")
                    // Pastikan progress bar disembunyikan jika ViewModel direset ke Idle
                    // FIXED: Menggunakan binding.progressBar dan binding.btnLogin sesuai ID di XML
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
