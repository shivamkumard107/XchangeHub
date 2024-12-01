package com.dev.sk.xchangehub.data.remote

import com.dev.sk.xchangehub.data.model.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("api/currencies.json")
    suspend fun getCurrencies(): Map<String, String>?

    @GET("api/latest.json")
    suspend fun getExchangeRates(@Query("base") baseCurrency: String): ExchangeRatesResponse?
}