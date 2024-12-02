package com.dev.sk.xchangehub.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dev.sk.xchangehub.databinding.ItemViewBinding
import java.util.Locale

class ExchangeCurrencyAdapter : ListAdapter<CurrencyExchangeItem, ViewHolder>(ItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CurrencyExchangeVH(ItemViewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as CurrencyExchangeVH).bind(getItem(position))
    }


    inner class CurrencyExchangeVH(private val binding: ItemViewBinding) :
        ViewHolder(binding.root) {

        fun bind(item: CurrencyExchangeItem) {
            binding.apply {
                currencyCode.text = item.code
                currencyName.text = item.name
                amount.text = String.format(Locale.US, item.amount.toString())
            }
        }
    }

}

object ItemDiffCallback : DiffUtil.ItemCallback<CurrencyExchangeItem>() {
    override fun areItemsTheSame(
        oldItem: CurrencyExchangeItem,
        newItem: CurrencyExchangeItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CurrencyExchangeItem,
        newItem: CurrencyExchangeItem
    ): Boolean {
        return oldItem.code == newItem.code
    }
}