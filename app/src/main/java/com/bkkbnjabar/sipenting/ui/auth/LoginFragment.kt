package com.bkkbnjabar.sipenting.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bkkbnjabar.sipenting.databinding.FragmentLoginBinding
import com.bkkbnjabar.sipenting.ui.main.MainActivity
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Penting: Hilt akan menyuntikkan ViewModel ke sini
class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

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
        binding.btnLogin.setOnClickListener {
            val usernameOrEmail = binding.etUsernameEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Silakan masukkan nama pengguna/email dan kata sandi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tentukan apakah input adalah email atau nama pengguna berdasarkan keberadaan '@'
            val isEmail = usernameOrEmail.contains("@")
            if (isEmail) {
                viewModel.login(email = usernameOrEmail, password = password)
            } else {
                viewModel.login(username = usernameOrEmail, password = password)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, resource.data?.message ?: "Login berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    val errorMessage = resource.message ?: "Terjadi kesalahan tidak dikenal"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Selesaikan AuthActivity agar pengguna tidak dapat kembali ke login
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}