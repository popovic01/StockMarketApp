package com.example.stockmarketapp.presentation.company_listings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

//responsibility of the view model: mapping data from the repository to the state we can show in our composable in the UI
@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository //we inject interface, not class!
): ViewModel() {

    var state by mutableStateOf(CompanyListingsState())

    private var searchJob: Job? = null

    init {
        getCompanyListings() //to initially fetch data from an api
    }

    fun onEvent(event: CompanyListingsEvent) {
        when(event) {
            is CompanyListingsEvent.Refresh -> {
                //we want to refetch data from an api
                getCompanyListings(fetchFromRemote = true, status = state.status)
            }
            //this is triggered for every single character we type (that is too many queries)
            //solution: whenever we type something new, we add the delay of 0.5s
            //this way, search is performed only when 0.5s passes and we didn't type anything new
            is CompanyListingsEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel() //if we already have job running
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListings(status = state.status)
                }
            }
            is CompanyListingsEvent.OnRadioButtonChange -> {
                state = state.copy(status = event.status)
                getCompanyListings(status = state.status)
            }
        }
    }

    private fun getCompanyListings(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false,
        status: String = "Active"
    ) {
        //launching coroutine from a viewmodelscope
        viewModelScope.launch {
            repository
                .getCompanyListings(fetchFromRemote, query, status) //returns the flow
                //collects the result from previous function call
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                state = state.copy(
                                    companies = listings
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading) //we just change isLoading, other values are same
                        }
                    }
                }
        }
    }
}