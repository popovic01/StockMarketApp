package com.example.stockmarketapp.domain.repository

import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
import com.example.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface StockRepository {

    //flow emits multiple values over a period of time (Loading, Success or Error in our case)
    //fetchFromRemote - if it is set to true, we want to get data from api (if it is false, we will get data from the cache)
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean, query: String, status: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(fetchFromRemote: Boolean, symbol: String): Flow<Resource<List<IntradayInfo>>>

    suspend fun getCompanyInfo(fetchFromRemote: Boolean, symbol: String): Flow<Resource<CompanyInfo>>
}