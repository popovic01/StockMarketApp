package com.example.stockmarketapp.data.remote.dto

import com.squareup.moshi.Json

//used to show kotlin representation of a json response
data class CompanyInfoDto(
    @field:Json(name = "Symbol") //field name in json response
    val symbol: String?,
    @field:Json(name = "Description")
    val description: String?,
    @field:Json(name = "Name")
    val name: String?,
    @field:Json(name = "Country")
    val country: String?,
    @field:Json(name = "Industry")
    val industry: String?
)
