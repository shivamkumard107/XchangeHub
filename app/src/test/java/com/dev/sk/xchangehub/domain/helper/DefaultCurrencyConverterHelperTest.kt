package com.dev.sk.xchangehub.domain.helper

import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.utils.BASE_CURRENCY_AMOUNT_IS_ZERO_EXCEPTION
import com.dev.sk.xchangehub.utils.BASE_CURRENCY_NOT_FOUND_EXCEPTION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCurrencyConverterHelperTest {

    private lateinit var currencyConverterHelper: DefaultCurrencyConverterHelper

    private val baseCurrency = CurrencyDTO("USD", "United States Dollar")
    private val selectedCurrency = CurrencyDTO("EUR", "Euro")
    private val anotherCurrency = CurrencyDTO("GBP", "British Pound")

    @Before
    fun setUp() {
        currencyConverterHelper = DefaultCurrencyConverterHelper()
    }

    @Test
    fun `mapRequestedCurrencyRatesToAvlCurrencies should return success with valid rates`() {
        // Arrange
        val amounts = mapOf(
            baseCurrency to 1.0,
            selectedCurrency to 0.85,
            anotherCurrency to 0.75
        )

        // Act
        val result = currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(selectedCurrency, amounts)

        // Assert
        assertTrue(result.isSuccess)
        val convertedMap = result.getOrNull()
        assertNotNull(convertedMap)
        assertEquals(3, convertedMap?.size)
        assertEquals(1.0, convertedMap?.get(selectedCurrency))
        assertEquals(1.0 / 0.85, convertedMap?.get(baseCurrency))
        assertEquals(0.75 / 0.85, convertedMap?.get(anotherCurrency))
    }

    @Test
    fun `mapRequestedCurrencyRatesToAvlCurrencies local conversion check`() {
        // Arrange
        val amounts = mapOf(
            baseCurrency to 1.0,
            anotherCurrency to 0.75
        )

        // Act
        val result = currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(selectedCurrency, amounts)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), amounts)
    }

    @Test
    fun `mapRequestedCurrencyRatesToAvlCurrencies should fail when base currency is not found`() {
        // Arrange
        val amounts = mapOf(
            selectedCurrency to 0.85,
            anotherCurrency to 0.75
        )

        // Act
        val result = currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(selectedCurrency, amounts)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(BASE_CURRENCY_NOT_FOUND_EXCEPTION.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `mapRequestedCurrencyRatesToAvlCurrencies should fail when base currency amount is zero`() {
        // Arrange
        val amounts = mapOf(
            baseCurrency to 0.0,
            selectedCurrency to 0.85,
            anotherCurrency to 0.75
        )

        // Act
        val result = currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(selectedCurrency, amounts)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(BASE_CURRENCY_AMOUNT_IS_ZERO_EXCEPTION.message, result.exceptionOrNull()?.cause?.message )
    }

    @Test
    fun `mapRequestedCurrencyRatesToAvlCurrencies should return normalized values when base and selected currencies are different`() {
        // Arrange
        val amounts = mapOf(
            baseCurrency to 1.0,
            selectedCurrency to 0.85,
            anotherCurrency to 0.75
        )

        // Act
        val result = currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(selectedCurrency, amounts)

        // Assert
        assertTrue(result.isSuccess)
        val convertedMap = result.getOrNull()
        assertNotNull(convertedMap)
        assertEquals(3, convertedMap?.size)
        assertEquals(1.0, convertedMap?.get(selectedCurrency))
        assertEquals(1.0 / 0.85, convertedMap?.get(baseCurrency))
        assertEquals(0.75 / 0.85, convertedMap?.get(anotherCurrency))
    }
}