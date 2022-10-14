package com.example.stockmarketapp.data.remote

import com.example.stockmarketapp.data.remote.dto.CompanyInfoDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    //ResponseBody - one-shot stream from the origin server
    //when we call getListings function, the api call will be executed
    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(@Query("apikey") apiKey: String = API_KEY, @Query("state") state: String, @Query("date") date: String = DATE): ResponseBody

    //returns ResponseBody because we get csv file (we need to get the byte stream)
    @GET("query?function=TIME_SERIES_INTRADAY&interval=60min&datatype=csv")
    suspend fun getIntradayInfo(@Query("symbol") symbol: String, @Query("apikey") apiKey: String = API_KEY): ResponseBody

    //Moshi parses json to dto
    @GET("query?function=OVERVIEW")
    suspend fun getCompanyInfo(@Query("symbol") symbol: String, @Query("apikey") apiKey: String = API_KEY): CompanyInfoDto

    companion object {
        const val API_KEY = "CBPG8V6J9LFSGJBR"
        const val BASE_URL = "https://alphavantage.co"
        const val DATE = "2014-07-10"
    }

}