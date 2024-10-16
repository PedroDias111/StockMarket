package com.example.stockmarket.presentation.company_info

import android.icu.text.SymbolTable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmarket.data.repository.StockRepositoryImpl
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStatesHandle: SavedStateHandle, // Navigation arguments e.g. symbol of company
    private val repository: StockRepository
): ViewModel() {

    private val _state = MutableStateFlow(CompanyInfoState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val symbol = savedStatesHandle.get<String>("symbol") ?: return@launch
            _state.update { it.copy( isLoading = true) }
            val companyInfoResult = async {repository.getCompanyInfo(symbol = symbol)}
            val intradayInfoResult = async {repository.getIntradayInfo(symbol = symbol)}

            when(val result = companyInfoResult.await()){
                is Resource.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message,
                        companyInfo = null
                    ) }
                }
                is Resource.Success -> {
                    _state.update { it.copy(
                        companyInfo = result.data,
                        isLoading = false
                    ) }
                }
                else -> Unit
            }

            when(val result = intradayInfoResult.await()){
                is Resource.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message,
                        companyInfo = null
                    ) }
                }
                is Resource.Success -> {
                    result.data?.let {
                        _state.update { it.copy(
                            stockInfos= result.data,
                            isLoading = false
                        ) }
                    }
                }
                else -> Unit
            }

        }
    }
}