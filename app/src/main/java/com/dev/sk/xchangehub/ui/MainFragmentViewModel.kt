package com.dev.sk.xchangehub.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(private val repository: Any) : ViewModel() {

    private val _userQuery: MutableStateFlow<Double> = MutableStateFlow(0.0)
    private val _uiState = MutableStateFlow("")
    val uiState: StateFlow<String> = _uiState


    init {
        initialize()
    }

    private fun initialize() {

    }

}