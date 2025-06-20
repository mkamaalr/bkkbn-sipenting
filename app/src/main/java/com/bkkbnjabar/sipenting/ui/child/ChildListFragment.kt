package com.bkkbnjabar.sipenting.ui.child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bkkbnjabar.sipenting.databinding.FragmentChildListBinding
import dagger.hilt.android.AndroidEntryPoint

class ChildListFragment : Fragment() {

    private var _binding: FragmentChildListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}