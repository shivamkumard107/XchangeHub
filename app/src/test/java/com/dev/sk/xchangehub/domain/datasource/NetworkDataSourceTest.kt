package com.dev.sk.xchangehub.domain.datasource

import kotlinx.coroutines.runBlocking
import com.dev.sk.xchangehub.data.model.ExchangeRatesResponse
import com.dev.sk.xchangehub.data.remote.NetworkService
import com.dev.sk.xchangehub.utils.BASE_CURRENCY
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class NetworkDataSourceTest {

    @Mock
    private lateinit var networkService: NetworkService

    private lateinit var networkDataSource: NetworkDataSource

    private val sampleCurrencyResponse = mapOf("USD" to "United States Dollar")
    private val sampleExchangeRateResponse = ExchangeRatesResponse(
        base = "USD",
        disclaimer = "Sample Disclaimer",
        license = "Sample License",
        rates = mapOf("EUR" to 0.85),
        timestamp = 1629878400
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        networkDataSource = NetworkDataSource(networkService)
    }

    @Test
    fun `getCurrencies should return success when response is valid`() = runBlocking {
        // Arrange
        Mockito.`when`(networkService.getCurrencies()).thenReturn(sampleCurrencyResponse)

        // Act
        val result = networkDataSource.getCurrencies()

        // Assert
        assertTrue(result.isSuccess)
        val currencies = result.getOrNull()
        assertNotNull(currencies)
        assertEquals(1, currencies?.size)
        assertEquals("USD", currencies?.get(0)?.currencyCode)
    }

    @Test
    fun `getCurrencies should return failure when response is null`() = runBlocking {
        // Arrange
        Mockito.`when`(networkService.getCurrencies()).thenReturn(null)

        // Act
        val result = networkDataSource.getCurrencies()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeRates should return success when response is valid`() = runBlocking {
        // Arrange
        Mockito.`when`(networkService.getExchangeRates(BASE_CURRENCY)).thenReturn(sampleExchangeRateResponse)

        // Act
        val result = networkDataSource.getExchangeRates()

        // Assert
        assertTrue(result.isSuccess)
        val exchangeRateDTO = result.getOrNull()
        assertNotNull(exchangeRateDTO)
        assertEquals(0.85, exchangeRateDTO?.rates?.get("EUR"))
    }

    @Test
    fun `getExchangeRates should return failure when response is null`() = runBlocking {
        // Arrange
        Mockito.`when`(networkService.getExchangeRates(BASE_CURRENCY)).thenReturn(null)

        // Act
        val result = networkDataSource.getExchangeRates()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }
}