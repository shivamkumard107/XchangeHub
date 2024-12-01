package com.dev.sk.xchangehub.presentation.home

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.repository.CurrencyRepository
import com.dev.sk.xchangehub.domain.usecase.GetCurrenciesUseCase
import com.dev.sk.xchangehub.domain.usecase.GetCurrencyConversionUseCase
import com.dev.sk.xchangehub.domain.usecase.GetSynchronousTimestampUseCase
import com.dev.sk.xchangehub.domain.usecase.SyncDataUseCase
import com.dev.sk.xchangehub.domain.usecase.UpdateSynchronousTimestampUseCase
import com.dev.sk.xchangehub.utils.THIRTY_MIN_IN_MILLIS
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

class FakeSyncDataUseCase @Inject constructor(private val repository: CurrencyRepository) :
    UseCase<Unit, Result<Boolean>> {
    override suspend fun execute(request: Unit): Result<Boolean> {
        return Result.success(true)
    }
}

class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var systemUnderTest: HomeViewModel

    private var getCurrenciesUseCase: GetCurrenciesUseCase = mock()
    private var getCurrencyConversionUseCase: GetCurrencyConversionUseCase = mock()
    private var syncDataUseCase: SyncDataUseCase = mock()
    private var getSyncTimestampUseCase: GetSynchronousTimestampUseCase = mock()
    private var updateSyncTimestampUseCase: UpdateSynchronousTimestampUseCase = mock()
    private var currencyRepository: CurrencyRepository = mock()

    private val currencyUSD = CurrencyDTO("USD", "United States Dollar")
    private val currencyJPY =  CurrencyDTO("JPY", "Japanese Yen")
    private val currencyINR = CurrencyDTO("INR", "Indian Rupee")

    private val dummyCurrencyList: List<CurrencyDTO> = listOf(
        currencyUSD,
        currencyJPY,
        currencyINR,
    )

    private val dummyCurrencyConversionMap = mapOf(
        currencyUSD to 1.0,
        currencyJPY to 20.0,
        currencyINR to 40.0
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() = runBlocking{
        Dispatchers.setMain(dispatcher)
        //Arrange
        whenever(getSyncTimestampUseCase.execute()).thenReturn(-1L)
        whenever(getCurrenciesUseCase.execute(any())).thenReturn(flowOf(DataState.Loading, DataState.Success(dummyCurrencyList)))
        whenever(getCurrencyConversionUseCase.execute(any())).thenReturn(flowOf(DataState.Loading, DataState.Success(dummyCurrencyConversionMap)))
        systemUnderTest = HomeViewModel(getCurrenciesUseCase, getCurrencyConversionUseCase, syncDataUseCase, getSyncTimestampUseCase, updateSyncTimestampUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `verify that the sync actually happens with mock case`() = runTest(dispatcher) {
        //Arrange
        whenever(getSyncTimestampUseCase.execute()).thenReturn(System.currentTimeMillis() - (THIRTY_MIN_IN_MILLIS+10000))
        whenever(syncDataUseCase.execute(Unit)).thenReturn(Result.success(true))
        val newSystemUnderTest = HomeViewModel(getCurrenciesUseCase, getCurrencyConversionUseCase, syncDataUseCase, getSyncTimestampUseCase, updateSyncTimestampUseCase)
        // Assert
        newSystemUnderTest.uiState.test {
            val state = awaitItem()
            assertTrue(state.status==UiStatus.Syncing)
        }
    }

    @Test
    fun `verify that the sync actually happens with fake case`() = runTest(dispatcher) {
        //Arrange
        whenever(getSyncTimestampUseCase.execute()).thenReturn(System.currentTimeMillis() - (THIRTY_MIN_IN_MILLIS+10000))
        whenever(currencyRepository.fetchAndSyncData()).thenReturn(Result.success(true))
        whenever(syncDataUseCase.execute(Unit)).thenReturn(Result.success(true))
        val newSystemUnderTest = HomeViewModel(getCurrenciesUseCase, getCurrencyConversionUseCase, FakeSyncDataUseCase(currencyRepository), getSyncTimestampUseCase, updateSyncTimestampUseCase)
        // Assert
        newSystemUnderTest.uiState.test {
            val state = awaitItem()
            assertTrue(state.status==UiStatus.Syncing)
        }
    }


    @Test
    fun `verify initialization correctness`() = runTest(dispatcher) {
        // Act
        systemUnderTest.initialize()
        // Assert
        systemUnderTest.uiState.test {
            val state = awaitItem()
            assertTrue(state.status==UiStatus.Success)
            assertTrue(state.convertedAmounts == dummyCurrencyConversionMap)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `user inputs a query`() = runTest {
        systemUnderTest.userQuery("100.0")
        systemUnderTest.uiState.test {
            val initialState = awaitItem()
            val finalState = awaitItem()
            assertTrue(finalState.status==UiStatus.Success)
            assertTrue(finalState.convertedAmounts == dummyCurrencyConversionMap.mapValues { it.value*100 })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `userQuery with invalid input sets amount to zero`() = runTest(dispatcher) {
        // Act
        systemUnderTest.userQuery("invalid_input")
        dispatcher.scheduler.advanceTimeBy(300)
        // Assert
        systemUnderTest.uiState.test {
            val initialState = awaitItem()
            val debouncedState = awaitItem()
            assertEquals(0.0, debouncedState.amount, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `handleUserQueryDebounce works correctly with debounce`() = runTest(dispatcher) {
        // Act
        systemUnderTest.userQuery("50")
        systemUnderTest.userQuery("100")
        //Debounce
        dispatcher.scheduler.advanceTimeBy(200)
        systemUnderTest.uiState.test {
            val initialState = awaitItem()
            assertEquals(1.0, initialState.amount,0.0)
            //Debounce
            dispatcher.scheduler.advanceTimeBy(100)
            val debouncedState = awaitItem()
            assertEquals(100.0, debouncedState.amount,0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectCurrency updates selectedCurrency correctly`() = runTest(dispatcher) {
        // Act
        systemUnderTest.selectCurrency(currencyINR)
        // Assert
        assertEquals(currencyINR, systemUnderTest.uiState.value.selectedCurrency)
    }


    @Test
    fun `user selects another currency`() = runTest {
        systemUnderTest.selectCurrency(currencyJPY)
        systemUnderTest.uiState.test {
            val initialState = awaitItem()
            val finalState = awaitItem()
            assertTrue(finalState.status==UiStatus.Success)
            assertTrue(finalState.convertedAmounts == dummyCurrencyConversionMap.mapValues { it.value*0 })
            cancelAndIgnoreRemainingEvents()
        }
    }
}