package com.example.stockmarketapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(@Query("apiKey") apiKey: String = API_KEY): ResponseBody
    //ResponseBody - one-shot stream from the origin server
    //when we call getListings function, the api call will be executed

    companion object {
        const val API_KEY = "CBPG8V6J9LFSGJBR"
        const val BASE_URL = "https://alphavantage.co"
    }

}