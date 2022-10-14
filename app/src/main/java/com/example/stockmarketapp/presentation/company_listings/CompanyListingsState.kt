package com.example.stockmarketapp.presentation.company_listings

import com.example.stockmarketapp.domain.model.CompanyListing

//contains everything relevant to the UI state
data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false, //for progress bar
    val isRefreshing: Boolean = false, //for api call
    val searchQuery: String = "",
    val status: String = "Active"
)


