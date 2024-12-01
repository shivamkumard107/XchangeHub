package com.dev.sk.xchangehub.domain.repository

import com.dev.sk.xchangehub.data.local.sharedpref.SharedPreferenceService
import com.dev.sk.xchangehub.data.local.sharedpref.TIMESTAMP
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.datasource.CurrencyDataSource
import com.dev.sk.xchangehub.domain.datasource.LocalDataSource
import com.dev.sk.xchangehub.domain.datasource.SYNC_ERROR
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.ExchangeRateDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CurrencyRepository {
    suspend fun getCurrencies(): Flow<DataState<List<CurrencyDTO>>>
    suspend fun getCurrencyExchangeRates(): Flow<DataState<Map<CurrencyDTO, Double?>>>
    suspend fun fetchAndSyncData(): Result<Boolean>
    fun updateSyncTimeStamp()
    fun getSyncTimeStamp(): Long
}

class DefaultCurrencyRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: CurrencyDataSource,
    private val sharedPreferences: SharedPreferenceService,
) : CurrencyRepository {

    override suspend fun getCurrencies(): Flow<DataState<List<CurrencyDTO>>> =
        fetchData(
            localFetch = { localDataSource.getCurrencies() }
        )

    override suspend fun getCurrencyExchangeRates(): Flow<DataState<Map<CurrencyDTO, Double?>>> =
        fetchData(
            localFetch = { localDataSource.getCurrencyExchangeRates() }
        )

    override suspend fun fetchAndSyncData(): Result<Boolean> = coroutineScope {
        try {
            val currenciesDeferred = async { networkDataSource.getCurrencies() }
            val exchangeRatesDeferred = async { networkDataSource.getExchangeRates() }

            val currencies = currenciesDeferred.await()
            val exchangeRates = exchangeRatesDeferred.await()

            if (currencies.isFailure || exchangeRates.isFailure) {
                return@coroutineScope Result.failure(
                    currencies.exceptionOrNull() ?: exchangeRates.exceptionOrNull() ?: Exception(
                        SYNC_ERROR
                    )
                )
            }

            val combinedList =
                combineCurrenciesWithRates(currencies.getOrThrow(), exchangeRates.getOrThrow())
            localDataSource.insertExchangeRate(combinedList)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun updateSyncTimeStamp() {
        sharedPreferences.putLong(TIMESTAMP, System.currentTimeMillis())
    }

    override fun getSyncTimeStamp(): Long {
        return sharedPreferences.getLong(TIMESTAMP, 0L)
    }

    private suspend fun <T> fetchData(
        localFetch: suspend () -> Result<T>,
    ): Flow<DataState<T>> = flow {
        emit(DataState.Loading)
        val localResult = localFetch()
        if (localResult.isSuccess) {
            emit(DataState.Success(localResult.getOrThrow()))
        } else {
            val syncResult = fetchAndSyncData()
            if (syncResult.isSuccess) {
                updateSyncTimeStamp()
                emit(DataState.Success(localFetch().getOrThrow()))
            } else {
                emit(DataState.Error(null, syncResult.exceptionOrNull()))
            }
        }
    }.catch { e ->
        emit(DataState.Error(null, e))
    }

    private fun combineCurrenciesWithRates(
        currencies: List<CurrencyDTO>,
        exchangeRates: ExchangeRateDTO
    ): Map<CurrencyDTO, Double?> {
        val exchangeMap: MutableMap<CurrencyDTO, Double?> = mutableMapOf()
        currencies.mapNotNull { currency ->
            val rate = exchangeRates.rates[currency.currencyCode]
            exchangeMap.put(currency, rate)
        }
        return exchangeMap
    }
}