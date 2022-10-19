package com.example.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompanyInfoEntity(
    val symbol: String,
    val name: String,
    val industry: String,
    val country: String,
    val description: String,
    val address: String,
    @PrimaryKey val id: Int? = null
)