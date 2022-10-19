package com.example.stockmarketapp.data.mapper

import com.example.stockmarketapp.data.local.IntradayInfoEntity
import com.example.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.example.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss" //for parsing
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter) //parsing string from api to LocalDateTime
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

fun IntradayInfoEntity.toIntradayInfo(): IntradayInfo {
    val convertedDateTime = timestamp.replace("T", " ") //in sql the format is 2022-10-13T17:00 so we need to convert it
    val pattern = "yyyy-MM-dd HH:mm" //for parsing
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(convertedDateTime, formatter) //parsing string from api to LocalDateTime
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

fun IntradayInfo.toIntradayInfoEntity(companyId: Int): IntradayInfoEntity {
    return IntradayInfoEntity(
        timestamp = date.toString(),
        close = close,
        companyId = companyId
    )
}
