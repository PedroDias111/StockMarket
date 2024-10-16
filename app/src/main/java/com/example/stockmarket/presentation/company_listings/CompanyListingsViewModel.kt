package com.example.stockmarket.presentation.company_listings

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
): ViewModel() {
    private val _state  = MutableStateFlow(CompanyListingsState())
    val state: StateFlow<CompanyListingsState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init{
        refreshCompanyListings()
    }

    fun onEvent( event: CompanyListingsEvents){
        when (event){
            is CompanyListingsEvents.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query)}
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    refreshCompanyListings()
                }
            }
            CompanyListingsEvents.Refresh -> {
                refreshCompanyListings(fetchFromRemote = true)
            }
        }
    }

    private fun refreshCompanyListings(
        query: String = _state.value.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ){
        viewModelScope.launch {

            repository
                .getCompanyListings(fetchFromRemote=fetchFromRemote, query=query)
                .collect{ result->
                    when (result){
                        is Resource.Success -> {
                            result.data?.let { listings->
                                _state.update { it.copy(
                                    companies = listings,
                                    isLoading = false,
                                )
                                }
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            _state.update { it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                    }

                }
        }

    }
}


