package com.dev.sk.xchangehub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dev.sk.xchangehub.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectFlows();
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