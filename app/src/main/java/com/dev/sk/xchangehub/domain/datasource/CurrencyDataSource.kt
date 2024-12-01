package com.dev.sk.xchangehub.domain.datasource

import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.ExchangeRateDTO

interface CurrencyDataSource {
    suspend fun getCurrencies(): Result<List<CurrencyDTO>>
    suspend fun getExchangeRates(): Result<ExchangeRateDTO>
}