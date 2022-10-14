package com.example.stockmarketapp.domain.repository

import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    //flow emits multiple values over a period of time (Loading, Success or Error in our case)
    //fetchFromRemote - if it is set to true, we want to get data from api (if it is false, we will get data from the cache)
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean, query: String, status: String
    ): Flow<Resource<List<CompanyListing>>>
}