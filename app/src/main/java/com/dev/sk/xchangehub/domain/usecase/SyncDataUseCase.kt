package com.dev.sk.xchangehub.domain.usecase

import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(private val repository: CurrencyRepository) :
    UseCase<Unit, Result<Boolean>> {
    override suspend fun execute(request: Unit): Result<Boolean> {
        return repository.fetchAndSyncData()
    }
}