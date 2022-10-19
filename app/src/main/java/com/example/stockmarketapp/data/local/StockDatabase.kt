package com.example.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    //list of tables in the db
    entities = [CompanyListingEntity::class, CompanyInfoEntity::class, IntradayInfoEntity::class],
    version = 1
)
abstract class StockDatabase: RoomDatabase() {
    abstract val dao: StockDao
}