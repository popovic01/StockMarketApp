package com.example.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = CompanyInfoEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("companyId"),
                        onDelete = CASCADE)]
)
data class IntradayInfoEntity(
    val timestamp: String,
    val close: Double,
    val companyId: Int,
    @PrimaryKey val id: Int? = null
)
