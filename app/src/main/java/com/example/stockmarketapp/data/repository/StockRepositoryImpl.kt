package com.example.stockmarketapp.data.repository

import android.util.Log
import com.example.stockmarketapp.data.csv.CSVParser
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.*
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
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
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>, //we want depend on an abstraction (interface in this case)
    private val intradayInfoParser: CSVParser<IntradayInfo>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
        status: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            //emits value we pass (later, we will catch these values in viewmodel)
            //we need to emit value of type Resource<List<CompanyListing>>
            emit(Resource.Loading(true)) //loading is true - first we want to show progress bar in the UI
            //then we want to execute query for getting companies from the database
            val localListings = dao.searchCompanyListing(query, status)
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
            emit(Resource.Success(emptyList())) //to show nothing while waiting for result from the api call
            //if user swipe for refresh or if db is empty
            val remoteListings = try {
                val statusForApi = if (status == "Delisted") "delisted" else "active"
                val response = api.getListings(state = statusForApi)
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
                dao.clearCompanyListings(status) //to clear the cache
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() }) //insert new listings in the db
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("", status)
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getCompanyInfo(fetchFromRemote: Boolean, symbol: String): Flow<Resource<CompanyInfo>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localCompanyInfo = dao.getCompanyInfo(symbol)
            emit(Resource.Success(
                data = localCompanyInfo?.toCompanyInfo()
            ))

            val isCompanyInfoNull = localCompanyInfo == null
            val shouldJustLoadFromCache = !isCompanyInfoNull && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }
            val remoteCompanyInfo = try {
                api.getCompanyInfo(symbol).toCompanyInfo()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load company info"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load company info"))
                null
            }

            remoteCompanyInfo?.let { companyInfo ->
                if (companyInfo.symbol.isEmpty()) {
                    emit(Resource.Error(message = "No info for this company"))
                    return@let
                }
                dao.clearCompanyInfo(symbol)
                dao.insertCompanyInfo(companyInfo.toCompanyInfoEntity())
                emit(Resource.Success(
                    data = dao.getCompanyInfo(symbol)?.toCompanyInfo()
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(fetchFromRemote: Boolean, symbol: String): Flow<Resource<List<IntradayInfo>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localIntradayInfos = dao.getIntradayInfos(symbol)
            emit(Resource.Success(
                data = localIntradayInfos.map { it.toIntradayInfo() }
            ))

            val isIntradayInfosEmpty = localIntradayInfos.isEmpty()
            val shouldJustLoadFromCache = !isIntradayInfosEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteIntradayInfos = try {
                val response = api.getIntradayInfo(symbol)
                intradayInfoParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load intra day info"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load intra day info"))
                null
            }

            remoteIntradayInfos?.let { infos ->
                if (infos.isEmpty()) {
                    emit(Resource.Error(
                        message = "No intra day info for this company"
                    ))
                    return@let
                }
                dao.clearIntradayInfos(symbol)
                val companyId = dao.getCompanyInfo(symbol)?.id ?: 0
                dao.insertIntradayInfo(
                    infos.map { it.toIntradayInfoEntity(companyId) }
                )
                emit(Resource.Success(
                    data = dao
                        .getIntradayInfos(symbol)
                        .map { it.toIntradayInfo() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

}