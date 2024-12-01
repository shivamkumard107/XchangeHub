package com.dev.sk.xchangehub.domain.helper

import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.utils.BASE_CURRENCY
import com.dev.sk.xchangehub.utils.BASE_CURRENCY_AMOUNT_IS_ZERO_EXCEPTION
import com.dev.sk.xchangehub.utils.BASE_CURRENCY_NOT_FOUND_EXCEPTION
import com.dev.sk.xchangehub.utils.CURRENCY_NOT_AVL_EXCEPTION
import com.dev.sk.xchangehub.utils.DEFAULT_BASE_CURRENCY
import javax.inject.Inject

interface CurrencyConverterHelper {
    fun mapRequestedCurrencyRatesToAvlCurrencies(
        selectedCurrency: CurrencyDTO,
        amounts: Map<CurrencyDTO, Double?>
    ): Result<Map<CurrencyDTO, Double>>
}

class DefaultCurrencyConverterHelper @Inject constructor() : CurrencyConverterHelper {
    override fun mapRequestedCurrencyRatesToAvlCurrencies(
        selectedCurrency: CurrencyDTO,
        amounts: Map<CurrencyDTO, Double?>
    ): Result<Map<CurrencyDTO, Double>> {
        val baseCurrencyDTO = amounts.entries.find { it.key.currencyCode == BASE_CURRENCY }?.key
            ?: DEFAULT_BASE_CURRENCY

        val selectedCurrencyRate = amounts[selectedCurrency]
            ?: resolveUnavailableCurrency(selectedCurrency)
            ?: return Result.failure(CURRENCY_NOT_AVL_EXCEPTION)

        val baseRate = amounts[baseCurrencyDTO]
            ?: return Result.failure(BASE_CURRENCY_NOT_FOUND_EXCEPTION)

        if (baseRate == 0.0) {
            return Result.failure(Exception(BASE_CURRENCY_AMOUNT_IS_ZERO_EXCEPTION))
        }
        val normalizedSelectedRate = selectedCurrencyRate / baseRate
        val convertedCurrencyMap = amounts.mapNotNull { (key, value) ->
            value?.let {
                key to (it / normalizedSelectedRate)
            }
        }.toMap()

        return Result.success(convertedCurrencyMap)
    }

    private fun resolveUnavailableCurrency(selectedCurrency: CurrencyDTO): Double? {
        //TODO: Implement logic for currencies that do not have corresponding amount
        return 1.0
    }
}