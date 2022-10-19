package com.example.stockmarketapp.presentation.company_info

import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorCompanyInfo: String? = null,
    val errorIntradayInfo: String? = null
)