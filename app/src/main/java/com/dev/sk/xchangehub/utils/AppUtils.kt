package com.dev.sk.xchangehub.utils

import java.text.NumberFormat
import java.util.Locale

fun String.toSafeDouble(defaultValue: Double = 0.0): Double {
    return this.toDoubleOrNull() ?: defaultValue
}

fun Double.formatCurrency(lengthThreshold: Int = 8): String {
    val integerPartLength = this.toLong().toString().length
    return if (integerPartLength >= lengthThreshold) {
        this.formatCurrencyWithSuffix()
    } else {
        this.formatAsCurrencyWithoutSymbol()
    }
}

fun String.formatCurrency(): String {
    if (this.isEmpty()) return ""

    val parts = this.split(".")
    val integerPart = parts[0].toLongOrNull()?.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    } ?: parts[0]

    return if (parts.size > 1) {
        "$integerPart.${parts[1]}"
    } else {
        integerPart
    }
}

fun Double.formatCurrencyWithSuffix(): String {
    return when {
        this >= 1_000_000_000_000_000 -> String.format(Locale.getDefault(),"%.2e", this)
        this >= 1_000_000_000_000 -> String.format(Locale.getDefault(),"%.2fT", this / 1_000_000_000_000)
        this >= 1_000_000_000 -> String.format(Locale.getDefault(),"%.2fB", this / 1_000_000_000)
        this >= 1_000_000 -> String.format(Locale.getDefault(),"%.2fM", this / 1_000_000)
        this >= 1_000 -> String.format(Locale.getDefault(),"%.2fK", this / 1_000)
        else -> String.format(Locale.getDefault(),"%.2f", this)
    }
}

fun Double.formatAsCurrencyWithoutSymbol(locale: Locale = Locale.getDefault()): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this)
}