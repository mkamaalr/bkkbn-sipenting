package com.bkkbnjabar.sipenting.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bkkbnjabar.sipenting.databinding.FragmentHomeBinding

/**
 * Fragment yang berfungsi sebagai layar utama atau "Home" setelah pengguna login.
 * Saat ini hanya menampilkan teks sambutan, namun dapat dikembangkan untuk
 * menampilkan dashboard, ringkasan data, atau informasi penting lainnya.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel untuk HomeFragment bisa dibuat jika diperlukan di masa depan.
        // val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        // Anda bisa mengatur teks ini dari ViewModel jika perlu.
        textView.text = "Selamat Datang di Aplikasi SIPENTING"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
