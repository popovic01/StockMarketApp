package com.example.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//Data Access Objects
//main classes where you define your database interactions
//they can include a variety of query methods
@Dao
interface StockDao {

    //when we get companies from an api we want to insert it into our local database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(companyListingEntities: List<CompanyListingEntity>)

    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    //search for symbol or if it contains query in the name
    @Query("""
        SELECT *
        FROM companylistingentity
        WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
            UPPER(:query) == symbol
    """)
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}