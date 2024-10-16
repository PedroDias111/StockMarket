package com.example.stockmarket.data.mapper

import com.example.stockmarket.data.remote.dto.CompanyInfoDto
import com.example.stockmarket.domain.model.CompanyInfo
import com.squareup.moshi.Json

fun CompanyInfoDto.toCompanyInfo (): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}