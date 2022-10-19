package com.example.stockmarketapp.presentation.company_info

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Error
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, //way to get access to navigation arguments directly, without passing them from the ui to the view model
    private val repository: StockRepository
): ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        getCompanyInfo()
    }

    fun onEvent(event: CompanyInfoEvent) {
        when(event) {
            is CompanyInfoEvent.Refresh -> {
                getCompanyInfo(true)
            }
        }
    }

    private fun getCompanyInfo(fetchFromRemote: Boolean = false) {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)

            repository
                .getCompanyInfo(fetchFromRemote, symbol)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { company ->
                                state = state.copy(company = company, isLoading = false, errorCompanyInfo = null)
                            }
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                errorCompanyInfo = result.message,
                                company = null
                            )
                        }
                    }
                }

            //if error occurs while getting company info, we don't want to make another api call for intra day info
            if (state.errorCompanyInfo == null) {
                repository
                    .getIntradayInfo(fetchFromRemote, symbol)
                    .collect { result ->
                        when(result) {
                            is Resource.Success -> {
                                result.data?.let { infos ->
                                    state = state.copy(stockInfos = infos, isLoading = false, errorIntradayInfo = null)
                                }
                            }
                            is Resource.Loading -> {
                                state = state.copy(isLoading = result.isLoading)
                            }
                            is Resource.Error -> {
                                state = state.copy(
                                    errorIntradayInfo = result.message,
                                    company = null,
                                    isLoading = false
                                )
                            }
                        }
                    }
            }
        }
    }
}