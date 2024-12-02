package com.dev.sk.xchangehub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sk.xchangehub.domain.base.DataState
import com.dev.sk.xchangehub.domain.base.SynchronousUseCase
import com.dev.sk.xchangehub.domain.base.UseCase
import com.dev.sk.xchangehub.domain.model.CurrencyDTO
import com.dev.sk.xchangehub.domain.model.CurrencyRequest
import com.dev.sk.xchangehub.utils.DEFAULT_BASE_CURRENCY
import com.dev.sk.xchangehub.utils.THIRTY_MIN_IN_MILLIS
import com.dev.sk.xchangehub.utils.toSafeDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val getCurrenciesUseCase: UseCase<Unit, Flow<DataState<List<CurrencyDTO>>>>,
    private val getCurrencyConversionUseCase: UseCase<CurrencyRequest, Flow<DataState<Map<CurrencyDTO, Double>>>>,
    private val syncDataUseCase: UseCase<Unit, Result<Boolean>>,
    private val getSyncTimestampUseCase: SynchronousUseCase<Unit, Long>,
    private val updateSyncTimestampUseCase: SynchronousUseCase<Unit, Unit>
) : ViewModel() {

    private val _userQuery: MutableStateFlow<Double> = MutableStateFlow(0.0)
    private val _uiState = MutableStateFlow(CurrencyConversionUiState())
    val uiState: StateFlow<CurrencyConversionUiState> = _uiState

    init {
        initialize()
        handleUserQueryDebounce()
    }

    private fun initialize() {
        if (isSyncRequired()) {
            doSync()
        } else {
            doSetup()
        }
    }

    private fun doSetup() {
        getAvailableCurrencies()
        selectCurrency(DEFAULT_BASE_CURRENCY)
    }


    fun userQuery(string: String) {
        val amount = string.toSafeDouble()
        _userQuery.value = amount
    }

    fun selectCurrency(currencyDTO: CurrencyDTO) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currencyDTO)
        viewModelScope.launch(Dispatchers.IO) {
            getCurrencyConversionUseCase.execute(CurrencyRequest(currencyDTO))
                .collect { conversionMapState ->
                    when (conversionMapState) {
                        is DataState.Error -> {
                            _uiState.value =
                                _uiState.value.copy(status = UiStatus.Error(conversionMapState.throwable?.message))
                        }

                        DataState.Loading -> {
                            _uiState.value = _uiState.value.copy(status = UiStatus.Loading)
                        }

                        is DataState.Success -> {
                            _uiState.value =
                                _uiState.value.copy(conversionRates = conversionMapState.data)
                            _uiState.value = _uiState.value.copy(
                                status = UiStatus.Success,
                                convertedAmounts = calculateConvertedAmounts(uiState.value.amount)
                            )
                        }
                    }
                }
        }
    }

    private fun handleUserQueryDebounce() {
        viewModelScope.launch {
            _userQuery.debounce(300).collect { value ->
                _uiState.value = _uiState.value.copy(
                    amount = value,
                    convertedAmounts = calculateConvertedAmounts(value)
                )
            }
        }
    }

    private fun calculateConvertedAmounts(amount: Double): Map<CurrencyDTO, Double>? {
        return _uiState.value.conversionRates?.mapValues { (_, rate) -> rate * amount }
    }

    private fun isSyncRequired(): Boolean {
        val lastSyncTime = getSyncTimestampUseCase.execute()
        val currentTimeMillis = System.currentTimeMillis()
        val isFirstSync = lastSyncTime == 0L
        val isTimeElapsed = (currentTimeMillis - lastSyncTime) > THIRTY_MIN_IN_MILLIS
        val isTimestampValid = lastSyncTime in 1..currentTimeMillis
        return isFirstSync || (isTimestampValid && isTimeElapsed)
    }


    private fun doSync() {
        _uiState.value = _uiState.value.copy(status = UiStatus.Syncing)
        viewModelScope.launch(Dispatchers.IO) {
            val result = syncDataUseCase.execute(Unit)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(status = UiStatus.Loading)
                    updateSyncTimestampUseCase.execute()
                    doSetup()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(status = UiStatus.Error(it.message))
                }
            )
        }
    }

    private fun getAvailableCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrenciesUseCase.execute(Unit).collect { currencyState ->
                when (currencyState) {
                    is DataState.Error -> {
                        _uiState.value =
                            _uiState.value.copy(status = UiStatus.Error(currencyState.throwable?.message))
                    }

                    DataState.Loading -> {
                        _uiState.value = _uiState.value.copy(status = UiStatus.Loading)
                    }

                    is DataState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            status = UiStatus.Success,
                            availableCurrencies = currencyState.data
                        )
                    }
                }
            }
        }
    }
}