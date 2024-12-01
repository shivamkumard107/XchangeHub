package com.dev.sk.xchangehub.utils

import com.dev.sk.xchangehub.domain.model.CurrencyDTO

const val BASE_CURRENCY = "USD"

val DEFAULT_BASE_CURRENCY: CurrencyDTO = CurrencyDTO(BASE_CURRENCY, "United States Dollar")

const val THIRTY_MIN_IN_MILLIS = 30 * 60 * 1000L