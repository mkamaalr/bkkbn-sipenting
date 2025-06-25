package com.bkkbnjabar.sipenting.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bkkbnjabar.sipenting.databinding.FragmentLoginBinding
import com.bkkbnjabar.sipenting.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment that displays the login screen and handles user input.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel specific to this Fragment
    private val loginViewModel: LoginViewModel by viewModels()
    // ViewModel shared with the host Activity
    private val authViewModel: AuthViewModel by activityViewModels()

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
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            loginViewModel.login(username, password)
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    // Notify the activity-level ViewModel to navigate
                    authViewModel.onLoginSuccess()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(context, "Login Gagal: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Idle -> {
                    binding.progressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
