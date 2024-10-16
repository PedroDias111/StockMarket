package com.example.stockmarket.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.stockmarket.data.csv.CSVParser
import com.example.stockmarket.data.csv.CompanyListingsParser
import com.example.stockmarket.data.csv.IntradayInfoParser
import com.example.stockmarket.data.local.CompanyListingEntity
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompanyInfo
import com.example.stockmarket.data.mapper.toCompanyListingModel
import com.example.stockmarket.data.remote.StockAPI
import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import dagger.Binds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockAPI,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListingEntity>,
    val intradayInfoParser: CSVParser<IntradayInfo>
): StockRepository {

    private val dao = db.dao

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingModel>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localListings = dao.searchCompanyListing(query)
            emit(
                Resource.Success(
                data = localListings.map {
                    it.toCompanyListingModel()
                }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try{
                val response = api.getCompanyListing()
                companyListingsParser.parse(response.byteStream())

            }catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error( "Couldn't load data"))
                null
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error( "Couldn't load data"))
                null
            }

            remoteListings?.let { listings->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings)
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing(query = "")
                        .map{
                        it.toCompanyListingModel()
                }))
                emit(Resource.Loading(false))

            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try{
                val response = api.getCompanyInfo(symbol)
                Resource.Success(response.toCompanyInfo())
            }catch (e: IOException){
                e.printStackTrace()
                Resource.Error( "Couldn't load intraday info")
            }catch (e: HttpException){
                e.printStackTrace()
                Resource.Error( "Couldn't load intraday info")
            }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try{
                val response = api.getIntradayInfo(symbol)
                val results = intradayInfoParser.parse(response.byteStream())
                Resource.Success(results)
            }catch (e: IOException){
                e.printStackTrace()
                Resource.Error( "Couldn't load intraday info")
            }catch (e: HttpException){
                e.printStackTrace()
                Resource.Error( "Couldn't load intraday info")
            }
    }
}
