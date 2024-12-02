package com.dev.sk.xchangehub.ui

import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.dev.sk.xchangehub.databinding.FragmentMainBinding
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.utils.DEFAULT_BASE_CURRENCY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: ArrayAdapter<CurrencyDTO>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setupUI()
        setupListeners()
        collectFlows()
        return binding.root
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                adapter.addAll(it.availableCurrencies.orEmpty())
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            baseValue.doOnTextChanged { text, _, _, _ -> viewModel.userQuery(text.toString()) }
            targetCurrencySelector.onItemSelectedListener =
                object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val currencyDTO = adapter.getItem(position)
                        currencyDTO?.let { viewModel.selectCurrency(it) }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        viewModel.selectCurrency(DEFAULT_BASE_CURRENCY)
                    }
                }
        }
    }

    private fun setupUI() {
        binding.apply {
            exchangeRateList.adapter = ExchangeCurrencyAdapter()
            exchangeRateList.layoutManager = GridLayoutManager(context, 3, VERTICAL, false)
            adapter = ArrayAdapter<CurrencyDTO>(requireContext(), simple_spinner_item)
            targetCurrencySelector.adapter = adapter
        }
    }

    companion object {
        fun instance() = MainFragment()
        const val TAG = "MainFragment"
    }


}