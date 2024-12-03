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
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.dev.sk.xchangehub.databinding.FragmentMainBinding
import com.dev.sk.xchangehub.utils.DEFAULT_BASE_CURRENCY
import com.dev.sk.xchangehub.utils.formatCurrency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private val currencyListAdapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            simple_spinner_item
        )
    }
    private val exchangeRateAdapter by lazy { ExchangeCurrencyAdapter() }

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
            viewModel.uiState.collect { uiState ->
                val availableCurrencies = uiState.availableCurrencies.orEmpty()
                currencyListAdapter.apply {
                    clear()
                    addAll(availableCurrencies.map { it.currencyCode }.toList())
                    notifyDataSetChanged()
                }

                val data = availableCurrencies.mapNotNull { currency ->
                    uiState.convertedAmounts?.get(currency)?.let { amount ->
                        CurrencyExchangeItem(
                            amount.formatCurrency(),
                            currency.currencyCode,
                            currency.currencyName
                        )
                    }
                }

                exchangeRateAdapter.submitList(data)
                binding.progressBar.visibility = View.GONE
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
                        val currencyDTO = currencyListAdapter.getItem(position)
                        val selected =
                            viewModel.uiState.value.availableCurrencies?.find { it.currencyCode == currencyDTO }
                        selected?.let {
                            viewModel.selectCurrency(it)
                            progressBar.visibility = View.VISIBLE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        viewModel.selectCurrency(DEFAULT_BASE_CURRENCY)
                    }
                }
        }
    }

    private fun setupUI() {
        binding.apply {
            val manager = GridLayoutManager(context, 3, VERTICAL, false)
            exchangeRateList.adapter = exchangeRateAdapter
            exchangeRateAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    manager.smoothScrollToPosition(
                        exchangeRateList,
                        null,
                        0
                    )
                }
            })
            exchangeRateList.layoutManager = manager
            targetCurrencySelector.adapter = currencyListAdapter
        }
    }

    companion object {
        fun instance() = MainFragment()
        const val TAG = "MainFragment"
    }


}