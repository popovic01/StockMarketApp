package com.example.stockmarketapp.presentation.company_info

import java.time.LocalDateTime

sealed class CompanyInfoEvent {
    object Refresh: CompanyInfoEvent()
}
