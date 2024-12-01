package com.dev.sk.xchangehub.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ExchangeRatesResponse(
    @SerialName("base")
    val base: String? = null,
    @SerialName("disclaimer")
    val disclaimer: String? = null,
    @SerialName("license")
    val license: String? = null,
    @SerialName("rates")
    val rates: Map<String, Double>? = null,
    @SerialName("timestamp")
    val timestamp: Int? = null
)