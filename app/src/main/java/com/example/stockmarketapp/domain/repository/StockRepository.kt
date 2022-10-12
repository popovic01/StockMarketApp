package com.example.stockmarketapp.domain.repository

import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    //flow emits multiple values over a period of time (first Loading, then Success or Error)
    //fetchFromRemote - if it is set to true, we want to get data from api (if it is false, we will get data from the cache)
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean, query: String
    ): Flow<Resource<List<CompanyListing>>>
}