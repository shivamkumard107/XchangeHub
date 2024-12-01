package com.dev.sk.xchangehub.domain.model

import androidx.annotation.Keep

@Keep
data class ExchangeRateDTO(
    val rates: Map<String, Double?>
)