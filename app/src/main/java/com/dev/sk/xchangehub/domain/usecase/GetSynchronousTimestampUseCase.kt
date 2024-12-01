package com.dev.sk.xchangehub.domain.usecase

import com.dev.sk.xchangehub.domain.base.SynchronousUseCase
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetSynchronousTimestampUseCase @Inject constructor(private val currencyRepository: CurrencyRepository):
    SynchronousUseCase<Unit, Long> {
    override fun execute(request: Unit?): Long {
        return currencyRepository.getSyncTimeStamp()
    }
}