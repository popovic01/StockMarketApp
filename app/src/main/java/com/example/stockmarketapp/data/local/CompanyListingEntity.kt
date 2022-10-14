package com.example.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity //table in our database
data class CompanyListingEntity(
    val name: String,
    val symbol: String,
    val exchange: String,
    val status: String,
    @PrimaryKey val id: Int? = null //Room will automatically generate id for us
)
