package com.dev.sk.xchangehub.domain.datasource

import com.dev.sk.xchangehub.data.remote.NetworkService
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.ExchangeRateDTO
import com.dev.sk.xchangehub.utils.BASE_CURRENCY
import com.dev.sk.xchangehub.utils.catchAsync
import javax.inject.Inject

const val SERVER_ERROR = "Server Error"

class NetworkDataSource @Inject constructor(
    private val networkService: NetworkService
) : CurrencyDataSource {
    override suspend fun getCurrencies(): Result<List<CurrencyDTO>> {
        val response = catchAsync { networkService.getCurrencies() }
        response.onSuccess {
            if (it == null) {
                return Result.failure(Exception(SERVER_ERROR))
            }
            return Result.success(it.map { entry -> CurrencyDTO(entry.key, entry.value) })
        }
        response.onFailure {
            return Result.failure(it)
        }
        return Result.failure(Exception(SERVER_ERROR))
    }

    override suspend fun getExchangeRates(): Result<ExchangeRateDTO> {
        val response = catchAsync { networkService.getExchangeRates(BASE_CURRENCY) }
        response.onSuccess {
            if (it == null) {
                return Result.failure(Exception(SERVER_ERROR))
            }
            val exchangeMap: MutableMap<String, Double> = mutableMapOf()
            if (it.rates == null) {
                return Result.failure(Exception(SERVER_ERROR))
            }
            it.rates.map { entry -> exchangeMap.put(entry.key, entry.value) }
            return Result.success(ExchangeRateDTO(exchangeMap))
        }
        return Result.failure(Exception(SERVER_ERROR))
    }
}