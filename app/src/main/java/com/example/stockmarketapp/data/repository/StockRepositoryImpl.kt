package com.example.stockmarketapp.data.repository

import com.example.stockmarketapp.data.csv.CSVParser
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.toCompanyListing
import com.example.stockmarketapp.data.mapper.toCompanyListingEntity
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton //we only want one instance of this
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing> //we want depend on an abstraction (interface in this case)
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            //emits value we pass (later, we will catch these values in viewmodel)
            //we need to emit value of type Resource<List<CompanyListing>>
            emit(Resource.Loading(true)) //loading is true, first we want to show progress bar in the UI
            //then we want to execute query for getting companies from the database
            val localListings = dao.searchCompanyListing(query) //we need to transform localListings (list of entities) to domain level models
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() } //we map each entity to model
            ))

            //we don't want to make api call if we have valid data from db and if user didn't swipe to refresh the page
            //is query is blank, localListings contains all companies (if there is some in the db)
            //if query is not blank, localListings could be empty list (if no company matches the query), even though db is not empty
            //that is why we need to check these two conditions - only then the db is empty
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote //only if db is not empty and user didn't swipe to refresh
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false)) //nothing is loading anymore, we can stop showing progress bar
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            //when we catch data from an api, we want to insert it into our cache
            remoteListings?.let { listings ->
                //data should always come from the cache to the UI (not from an api)
                dao.clearCompanyListings() //to clear the cache
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() }) //insert new listings in the db
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }
}