package com.dev.sk.xchangehub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.helper.CurrencyConverterHelper
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.CurrencyRequest
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrencyConversionUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val currencyConverterHelper: CurrencyConverterHelper
) : UseCase<CurrencyRequest, Flow<DataState<Map<CurrencyDTO, Double>>>> {
    override suspend fun execute(request: CurrencyRequest): Flow<DataState<Map<CurrencyDTO, Double>>> {
        return currencyRepository.getCurrencyExchangeRates().map { mapState ->
            when (mapState) {
                is DataState.Error -> DataState.Error(null, mapState.throwable)
                DataState.Loading -> DataState.Loading
                is DataState.Success -> {
                    val conversionResult =
                        currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(
                            request.currencyDTO,
                            mapState.data,
                        )
                    conversionResult.fold(
                        onSuccess = { convertedData ->
                            DataState.Success(convertedData)
                        },
                        onFailure = { throwable ->
                            DataState.Error(null, throwable)
                        }
                    )
                }
            }
        }
    }
}