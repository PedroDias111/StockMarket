package com.example.stockmarket.di

import com.example.stockmarket.data.csv.CSVParser
import com.example.stockmarket.data.csv.CompanyListingsParser
import com.example.stockmarket.data.csv.IntradayInfoParser
import com.example.stockmarket.data.local.CompanyListingEntity
import com.example.stockmarket.data.repository.StockRepositoryImpl
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ):CSVParser<CompanyListingEntity>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ):CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepository: StockRepositoryImpl
    ):StockRepository
}