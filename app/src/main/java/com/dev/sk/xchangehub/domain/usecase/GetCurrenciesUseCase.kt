package com.dev.sk.xchangehub.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(private val repository: CurrencyRepository) :
    UseCase<Unit, Flow<DataState<List<CurrencyDTO>>>> {
    override suspend fun execute(request: Unit): Flow<DataState<List<CurrencyDTO>>> {
        return repository.getCurrencies()
    }
}