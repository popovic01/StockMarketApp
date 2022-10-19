package com.example.stockmarketapp.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

//Data Access Objects
//here you define your database interactions
//they can include a variety of query methods
@Dao
interface StockDao {

    //when we get companies from an api we want to insert it into our local database
    @Insert(onConflict = REPLACE)
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

    @Query("DELETE FROM companyinfoentity WHERE symbol = :symbol")
    suspend fun clearCompanyInfo(symbol: String)

    @Query("DELETE FROM intradayinfoentity WHERE companyId IN (SELECT id FROM companyinfoentity where symbol = :symbol)")
    suspend fun clearIntradayInfos(symbol: String)

    @Insert(onConflict = REPLACE)
    suspend fun insertCompanyInfo(companyInfoEntity: CompanyInfoEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertIntradayInfo(intradayInfoEntity: List<IntradayInfoEntity>)

    @Query("SELECT * FROM companyinfoentity WHERE symbol = :symbol")
    suspend fun getCompanyInfo(symbol: String): CompanyInfoEntity?

    @Query("SELECT * FROM intradayinfoentity WHERE companyId IN (SELECT id FROM companyinfoentity where symbol = :symbol)")
    suspend fun getIntradayInfos(symbol: String): List<IntradayInfoEntity>
}