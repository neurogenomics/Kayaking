package com.kayak_backend.routes

import io.ktor.http.*
import io.ktor.server.plugins.*
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime

fun getDateTimeParameter(
    parameters: Parameters,
    name: String,
): LocalDateTime {
    try {
        val maybeDateStr = parameters[name] ?: return LocalDateTime.now()
        return kotlinx.datetime.LocalDateTime.parse(maybeDateStr).toJavaLocalDateTime()
    } catch (e: IllegalArgumentException) {
        throw ParameterConversionException(name, "Date in format YYYY-MM-DDTHH:mm:ss")
    }
}

fun getDateParameter(
    parameters: Parameters,
    name: String,
): LocalDate? {
    try {
        val maybeDateStr = parameters[name] ?: return null
        return kotlinx.datetime.LocalDate.parse(maybeDateStr).toJavaLocalDate()
    } catch (e: IllegalArgumentException) {
        throw ParameterConversionException(name, "Date in format YYYY-MM-DD")
    }
}
