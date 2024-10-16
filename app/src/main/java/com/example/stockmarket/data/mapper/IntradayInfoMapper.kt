package com.example.stockmarket.data.mapper

import com.example.stockmarket.data.remote.dto.IntradayInfoDto
import com.example.stockmarket.domain.model.IntradayInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss" // Define Pattern format for Date&Time Parsing
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )

}

