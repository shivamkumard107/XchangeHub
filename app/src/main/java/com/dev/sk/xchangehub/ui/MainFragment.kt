package com.dev.sk.xchangehub.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dev.sk.xchangehub.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        collectFlows()
        return binding.root
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            // collect data from viewModel and set on UI
        }
    }

    companion object {
        fun instance() = MainFragment()
        const val TAG = "MainFragment"
    }


}