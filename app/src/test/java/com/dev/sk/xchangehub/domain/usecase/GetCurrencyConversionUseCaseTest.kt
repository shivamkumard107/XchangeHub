package com.dev.sk.xchangehub.domain.usecase

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.helper.CurrencyConverterHelper
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.CurrencyRequest
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetCurrencyConversionUseCaseTest {

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    private lateinit var currencyConverterHelper: CurrencyConverterHelper

    private lateinit var getCurrencyConversionUseCase: GetCurrencyConversionUseCase

    private val sampleCurrencyDTO = CurrencyDTO("USD", "United States Dollar")
    private val sampleRequest = CurrencyRequest(sampleCurrencyDTO)
    private val sampleRates = mapOf(sampleCurrencyDTO to 1.0)
    private val convertedRates = mapOf(sampleCurrencyDTO to 1.0)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getCurrencyConversionUseCase = GetCurrencyConversionUseCase(currencyRepository, currencyConverterHelper)
    }

    @Test
    fun `execute should return success when repository data and conversion succeed`() = runBlocking {
        // Arrange
        val dataStateSuccess: DataState<Map<CurrencyDTO, Double?>> = DataState.Success(sampleRates)
        whenever(currencyRepository.getCurrencyExchangeRates()).thenReturn(flowOf(dataStateSuccess))
        whenever(
            currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(sampleCurrencyDTO, sampleRates)
        ).thenReturn(Result.success(convertedRates))

        // Act
        val resultFlow = getCurrencyConversionUseCase.execute(sampleRequest).toList()

        // Assert
        assertEquals(1, resultFlow.size)
        assertTrue(resultFlow[0] is DataState.Success)
        assertEquals(convertedRates, (resultFlow[0] as DataState.Success).data)
    }

    @Test
    fun `execute should return error when repository returns an error`() = runBlocking {
        // Arrange
        val error = Exception("Repository error")
        val dataStateError: DataState<Map<CurrencyDTO, Double?>> = DataState.Error(null, error)
        whenever(currencyRepository.getCurrencyExchangeRates()).thenReturn(flowOf(dataStateError))

        // Act
        val resultFlow = getCurrencyConversionUseCase.execute(sampleRequest).toList()

        // Assert
        assertEquals(1, resultFlow.size)
        assertTrue(resultFlow[0] is DataState.Error)
        assertEquals(error, (resultFlow[0] as DataState.Error).throwable)
    }

    @Test
    fun `execute should return loading state when repository is loading`() = runBlocking {
        // Arrange
        whenever(currencyRepository.getCurrencyExchangeRates()).thenReturn(flowOf(DataState.Loading))

        // Act
        val resultFlow = getCurrencyConversionUseCase.execute(sampleRequest).toList()

        // Assert
        assertEquals(1, resultFlow.size)
        assertTrue(resultFlow[0] is DataState.Loading)
    }

    @Test
    fun `execute should return error when conversion fails`() = runBlocking {
        // Arrange
        val dataStateSuccess: DataState<Map<CurrencyDTO, Double?>> = DataState.Success(sampleRates)
        val conversionError = Exception("Conversion error")
        whenever(currencyRepository.getCurrencyExchangeRates()).thenReturn(flowOf(dataStateSuccess))
        whenever(
            currencyConverterHelper.mapRequestedCurrencyRatesToAvlCurrencies(sampleCurrencyDTO, sampleRates)
        ).thenReturn(Result.failure(conversionError))

        // Act
        val resultFlow = getCurrencyConversionUseCase.execute(sampleRequest).toList()

        // Assert
        assertEquals(1, resultFlow.size)
        assertTrue(resultFlow[0] is DataState.Error)
        assertEquals(conversionError, (resultFlow[0] as DataState.Error).throwable)
    }
}