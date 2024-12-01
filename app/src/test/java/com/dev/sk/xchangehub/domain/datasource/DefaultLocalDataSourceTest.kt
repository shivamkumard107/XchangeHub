package com.dev.sk.xchangehub.domain.datasource

import kotlinx.coroutines.runBlocking
import com.dev.sk.xchangehub.data.local.database.dao.ExchangeDao
import com.dev.sk.xchangehub.data.local.database.entities.ExchangeEntity
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class DefaultLocalDataSourceTest {

    @Mock
    private lateinit var exchangeDao: ExchangeDao

    private lateinit var localDataSource: DefaultLocalDataSource

    private val sampleCurrencyDTO = CurrencyDTO("USD", "United States Dollar")
    private val sampleEntity = ExchangeEntity("USD", "United States Dollar", 1.0)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        localDataSource = DefaultLocalDataSource(exchangeDao)
    }

    @Test
    fun `insertExchangeRate should call updateOrInsertBatch on dao`() = runBlocking {
        // Arrange
        val currencyMap = mapOf(sampleCurrencyDTO to 1.0)

        // Act
        localDataSource.insertExchangeRate(currencyMap)

        // Assert
        Mockito.verify(exchangeDao).updateOrInsertBatch(Mockito.anyList())
    }

    @Test
    fun `getCurrencyExchangeRates should return success when data is available`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(listOf(sampleEntity))

        // Act
        val result = localDataSource.getCurrencyExchangeRates()

        // Assert
        assertTrue(result.isSuccess)
        val resultMap = result.getOrNull()
        assertNotNull(resultMap)
        assertEquals(1, resultMap?.size)
        assertTrue(resultMap?.containsKey(sampleCurrencyDTO) == true)
        assertEquals(1.0, resultMap?.get(sampleCurrencyDTO))
    }

    @Test
    fun `getCurrencyExchangeRates should return failure when data is empty`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(emptyList())

        // Act
        val result = localDataSource.getCurrencyExchangeRates()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(SYNC_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCurrencies should return success when data is available`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(listOf(sampleEntity))

        // Act
        val result = localDataSource.getCurrencies()

        // Assert
        assertTrue(result.isSuccess)
        val currencies = result.getOrNull()
        assertNotNull(currencies)
        assertEquals(1, currencies?.size)
        assertEquals(sampleCurrencyDTO, currencies?.get(0))
    }

    @Test
    fun `getCurrencies should return failure when data is empty`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(emptyList())

        // Act
        val result = localDataSource.getCurrencies()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(SYNC_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeRates should return success when data is available`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(listOf(sampleEntity))

        // Act
        val result = localDataSource.getExchangeRates()

        // Assert
        assertTrue(result.isSuccess)
        val exchangeRateDTO = result.getOrNull()
        assertNotNull(exchangeRateDTO)
        assertEquals(1, exchangeRateDTO?.rates?.size)
        assertEquals(1.0, exchangeRateDTO?.rates?.get("USD"))
    }

    @Test
    fun `getExchangeRates should return failure when data is empty`() = runBlocking {
        // Arrange
        Mockito.`when`(exchangeDao.getExchangeRates()).thenReturn(emptyList())

        // Act
        val result = localDataSource.getExchangeRates()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(SYNC_ERROR, result.exceptionOrNull()?.message)
    }
}