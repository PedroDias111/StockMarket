package com.example.stockmarket.data.csv

import com.example.stockmarket.data.local.CompanyListingEntity
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor(): CSVParser<CompanyListingEntity> {

    override suspend fun parse(stream: InputStream): List<CompanyListingEntity> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line->
                    val symbol = line.getOrNull(index = 0)
                    val name = line.getOrNull(index = 1)
                    val exchange = line.getOrNull(index = 2)
                    CompanyListingEntity(
                        symbol = symbol?: return@mapNotNull null,
                        name = name?: return@mapNotNull null,
                        exchange = exchange?: return@mapNotNull null
                    )
                }
                .also {
                    csvReader.close()
                }
        }
    }
}

