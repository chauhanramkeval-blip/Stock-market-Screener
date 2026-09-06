package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Company
import com.example.data.CompanyRepository
import com.example.data.network.ScreenerQueryDto
import com.example.data.network.ScreenerResultDto
import com.example.data.network.StockOverviewDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompanyViewModel(private val repository: CompanyRepository) : ViewModel() {
    
    // Local DB State
    val allCompanies: StateFlow<List<Company>> = repository.allCompanies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Network States
    private val _stockOverviewState = MutableStateFlow<UiState<StockOverviewDto>>(UiState.Loading)
    val stockOverviewState: StateFlow<UiState<StockOverviewDto>> = _stockOverviewState.asStateFlow()

    private val _screenerState = MutableStateFlow<UiState<List<ScreenerResultDto>>>(UiState.Loading)
    val screenerState: StateFlow<UiState<List<ScreenerResultDto>>> = _screenerState.asStateFlow()

    // Database Actions
    fun addCompany(company: Company) {
        viewModelScope.launch {
            repository.insert(company)
        }
    }

    // Network Actions
    fun fetchStockFundamentals(symbol: String) {
        viewModelScope.launch {
            _stockOverviewState.value = UiState.Loading
            try {
                val data = repository.fetchStockFundamentals(symbol)
                _stockOverviewState.value = UiState.Success(data)
            } catch (e: Exception) {
                _stockOverviewState.value = UiState.Error(e.message ?: "Failed to fetch stock overview")
            }
        }
    }

    fun runScreener(query: ScreenerQueryDto) {
        viewModelScope.launch {
            _screenerState.value = UiState.Loading
            try {
                val results = repository.runScreener(query)
                _screenerState.value = UiState.Success(results)
            } catch (e: Exception) {
                _screenerState.value = UiState.Error(e.message ?: "Failed to execute screener")
            }
        }
    }
}

class CompanyViewModelFactory(private val repository: CompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompanyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompanyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
