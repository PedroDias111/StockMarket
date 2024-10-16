package com.example.stockmarket.presentation.company_info

import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.util.Resource
import java.lang.Error

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val companyInfo: CompanyInfo ?= null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
