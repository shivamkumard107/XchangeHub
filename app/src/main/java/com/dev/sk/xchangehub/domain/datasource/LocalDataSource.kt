package com.dev.sk.xchangehub.domain.datasource

import com.dev.sk.xchangehub.data.local.database.dao.ExchangeDao
import com.dev.sk.xchangehub.data.local.database.entities.ExchangeEntity
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.ExchangeRateDTO
import javax.inject.Inject

interface LocalDataSource : CurrencyDataSource {
    suspend fun insertExchangeRate(currencyWithExchangeRate: Map<CurrencyDTO, Double?>)
    suspend fun getCurrencyExchangeRates(): Result<Map<CurrencyDTO, Double?>>
}

const val SYNC_ERROR = "Sync Error"

class DefaultLocalDataSource @Inject constructor(
    private val exchangeDao: ExchangeDao
) : LocalDataSource {

    override suspend fun insertExchangeRate(currencyWithExchangeRate: Map<CurrencyDTO, Double?>) {
        val entities = currencyWithExchangeRate.map { entry ->
            ExchangeEntity(
                currencyCode = entry.key.currencyCode,
                currencyName = entry.key.currencyName,
                conversionRate = entry.value
            )
        }
        exchangeDao.updateOrInsertBatch(entities)
    }

    override suspend fun getCurrencyExchangeRates(): Result<Map<CurrencyDTO, Double?>> {
        val exchangeRate = exchangeDao.getExchangeRates()
        if (exchangeRate.isEmpty()) {
            return Result.failure(Exception(SYNC_ERROR))
        }
        val currencyRateMap: MutableMap<CurrencyDTO, Double?> = mutableMapOf()
        exchangeDao.getExchangeRates().map {
            currencyRateMap.put(CurrencyDTO(it.currencyCode, it.currencyName), it.conversionRate)
        }
        return Result.success(currencyRateMap)
    }

    override suspend fun getCurrencies(): Result<List<CurrencyDTO>> {
        val currencies = exchangeDao.getExchangeRates()
        if (currencies.isEmpty()) {
            return Result.failure(exception = Exception(SYNC_ERROR))
        }
        return Result.success(currencies.map { entity ->
            CurrencyDTO(
                entity.currencyCode,
                entity.currencyName
            )
        })
    }

    override suspend fun getExchangeRates(): Result<ExchangeRateDTO> {
        val exchangeRate = exchangeDao.getExchangeRates()
        if (exchangeRate.isEmpty()) {
            return Result.failure(exception = Exception(SYNC_ERROR))
        }
        val exchangeMap: MutableMap<String, Double?> = mutableMapOf()
        exchangeRate.map { exchangeMap.put(it.currencyCode, it.conversionRate) }
        return Result.success(ExchangeRateDTO(exchangeMap))
    }

}