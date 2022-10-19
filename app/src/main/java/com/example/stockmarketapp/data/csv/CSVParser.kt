package com.example.stockmarketapp.data.csv

import java.io.InputStream
import java.time.LocalDateTime

//we made interface because we don't want to depend on concretions (classes) but on abstractions (interfaces or abstract classes)
interface CSVParser<T> {
    suspend fun parse(stream: InputStream): List<T>
}