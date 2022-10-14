package com.example.stockmarketapp.di

import android.app.Application
import androidx.room.Room
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

//modules in Daeger-Hilt: containers in which we define our dependencies and how Daeger-Hilt can actually create these
//all dependencies we need for our app
@Module
@InstallIn(SingletonComponent::class) //to make sure dependencies will live as long as application does
object AppModule {

    //for every dependency, we need a function

    @Provides //function provide a dependency
    @Singleton
    fun provideStockApi(): StockApi {
        //creating an instance of the stock api
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stockdb.db"
        ).build()
    }
}