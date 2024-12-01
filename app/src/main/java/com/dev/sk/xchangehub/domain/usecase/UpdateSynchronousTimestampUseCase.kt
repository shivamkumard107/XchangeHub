package com.dev.sk.xchangehub.domain.usecase

import com.dev.sk.xchangehub.domain.base.SynchronousUseCase
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import javax.inject.Inject

class UpdateSynchronousTimestampUseCase @Inject constructor(private val repository: CurrencyRepository):
    SynchronousUseCase<Unit, Unit> {
    override fun execute(request: Unit?) {
        repository.updateSyncTimeStamp()
    }
}