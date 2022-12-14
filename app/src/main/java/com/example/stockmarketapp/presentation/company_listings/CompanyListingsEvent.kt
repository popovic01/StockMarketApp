package com.example.stockmarketapp.presentation.company_listings

//different ui events user can perform on a single screen
sealed class CompanyListingsEvent {
    object Refresh: CompanyListingsEvent()
    data class OnSearchQueryChange(val query: String): CompanyListingsEvent()
    data class OnRadioButtonChange(val status: String): CompanyListingsEvent()
    data class OnCheckBoxChange(val assetType: String): CompanyListingsEvent()
}
