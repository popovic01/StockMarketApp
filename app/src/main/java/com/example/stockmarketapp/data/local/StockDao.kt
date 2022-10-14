package com.example.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//Data Access Objects
//here you define your database interactions
//they can include a variety of query methods
@Dao
interface StockDao {

    //when we get companies from an api we want to insert it into our local database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(companyListingEntities: List<CompanyListingEntity>)

    @Query("DELETE FROM companylistingentity WHERE status = :status")
    suspend fun clearCompanyListings(status: String)

    //search for symbol or query in the name (:query means query is the parameter)
    //|| is for concatenation
    @Query("""
        SELECT *
        FROM companylistingentity
        WHERE (LOWER(name) LIKE '%' || LOWER(:query) || '%' OR 
            UPPER(:query) == symbol) AND status = :status
    """)
    suspend fun searchCompanyListing(query: String, status: String): List<CompanyListingEntity>
}