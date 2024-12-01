package com.dev.sk.xchangehub.domain.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import com.dev.sk.xchangehub.data.local.sharedpref.SharedPreferenceService
import com.dev.sk.xchangehub.data.local.sharedpref.TIMESTAMP
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.datasource.CurrencyDataSource
import com.dev.sk.xchangehub.domain.datasource.LocalDataSource
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.ExchangeRateDTO
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DefaultCurrencyRepositoryTest {

    @Mock
    private lateinit var localDataSource: LocalDataSource

    @Mock
    private lateinit var networkDataSource: CurrencyDataSource

    @Mock
    private lateinit var sharedPreferences: SharedPreferenceService

    private lateinit var currencyRepository: DefaultCurrencyRepository

    private val sampleCurrencies = listOf(CurrencyDTO("USD", "United States Dollar"))
    private val sampleExchangeRates = mapOf("USD" to 1.0)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        currencyRepository = DefaultCurrencyRepository(localDataSource, networkDataSource, sharedPreferences)
    }

    @Test
    fun `getCurrencies should emit DataState Success when local data is available`() = runBlocking {
        // Arrange
        whenever(localDataSource.getCurrencies()).thenReturn(Result.success(sampleCurrencies))

        // Act
        val flow = currencyRepository.getCurrencies()
        val emissions = flow.toList()

        // Assert
        assertEquals(2, emissions.size) // Loading and Success states
        assertTrue(emissions[0] is DataState.Loading)
        assertTrue(emissions[1] is DataState.Success)
        assertEquals(sampleCurrencies, (emissions[1] as DataState.Success).data)
    }

    @Test
    fun `getCurrencies should emit DataState Error when local data fetch fails and sync also fails`() = runBlocking {
        // Arrange
        whenever(localDataSource.getCurrencies()).thenReturn(Result.failure(Exception("Local fetch error")))
        whenever(networkDataSource.getCurrencies()).thenReturn(Result.failure(Exception("Network fetch error")))
        whenever(networkDataSource.getExchangeRates()).thenReturn(Result.failure(Exception("Network fetch error")))

        // Act
        val flow = currencyRepository.getCurrencies()
        val emissions = flow.toList()

        // Assert
        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is DataState.Loading)
        assertTrue(emissions[1] is DataState.Error)
        assertEquals("Network fetch error", (emissions[1] as DataState.Error).throwable?.message)
    }

    @Test
    fun `fetchAndSyncData should return success when network data fetch succeeds`() = runBlocking {
        // Arrange
        whenever(networkDataSource.getCurrencies()).thenReturn(Result.success(sampleCurrencies))
        whenever(networkDataSource.getExchangeRates()).thenReturn(Result.success(ExchangeRateDTO(sampleExchangeRates)))

        // Act
        val result = currencyRepository.fetchAndSyncData()

        // Assert
        assertTrue(result.isSuccess)
        Mockito.verify(localDataSource).insertExchangeRate(any())
    }

    @Test
    fun `fetchAndSyncData should return failure when network data fetch fails`() = runBlocking {
        // Arrange
        whenever(networkDataSource.getCurrencies()).thenReturn(Result.failure(Exception("Network error")))
        whenever(networkDataSource.getExchangeRates()).thenReturn(Result.failure(Exception("Network error")))

        // Act
        val result = currencyRepository.fetchAndSyncData()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }


    @Test
    fun `updateSyncTimeStamp should update timestamp in shared preferences`() {
        // Act
        currencyRepository.updateSyncTimeStamp()

        // Assert
        val keyCaptor = argumentCaptor<String>()
        val valueCaptor = argumentCaptor<Long>()

        Mockito.verify(sharedPreferences).putLong(keyCaptor.capture(), valueCaptor.capture())

        assertEquals(TIMESTAMP, keyCaptor.firstValue)
        assertTrue(valueCaptor.firstValue > 0)
    }

    @Test
    fun `getSyncTimeStamp should return timestamp from shared preferences`() {
        // Arrange
        val expectedTimestamp = 123456789L
        whenever(sharedPreferences.getLong(TIMESTAMP, 0L)).thenReturn(expectedTimestamp)

        // Act
        val timestamp = currencyRepository.getSyncTimeStamp()

        // Assert
        assertEquals(expectedTimestamp, timestamp)
    }
}