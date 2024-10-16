package com.example.stockmarket.domain.repository

import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    //Returns a Flow
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingModel>>>
    
    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>
}