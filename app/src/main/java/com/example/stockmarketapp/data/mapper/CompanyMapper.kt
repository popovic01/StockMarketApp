package com.example.stockmarketapp.data.mapper

import com.example.stockmarketapp.data.local.CompanyListingEntity
import com.example.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing

//mapping entity in data layer to model in domain layer
fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange,
        status = status
    )
}

//mapping model in domain layer to entity in data layer
fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange,
        status = status
        //id will automatically be generated
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}

