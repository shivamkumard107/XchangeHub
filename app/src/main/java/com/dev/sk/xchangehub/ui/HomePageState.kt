package com.dev.sk.xchangehub.ui

import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.utils.DEFAULT_BASE_CURRENCY

sealed class UiStatus {
    data object Loading : UiStatus()
    data object Success : UiStatus()
    data class Error(val message: String?) : UiStatus()
    data object Syncing : UiStatus()
}

data class CurrencyConversionUiState(
    val status: UiStatus = UiStatus.Loading,
    var selectedCurrency: CurrencyDTO = DEFAULT_BASE_CURRENCY,
    val availableCurrencies: List<CurrencyDTO>? = null,
    val convertedAmounts: Map<CurrencyDTO, Double>? = null,
    val conversionRates: Map<CurrencyDTO, Double>? = null,
    val amount: Double = 1.0,
)