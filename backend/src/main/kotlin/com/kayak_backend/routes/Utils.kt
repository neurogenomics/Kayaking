package com.kayak_backend.routes

import io.ktor.http.*
import io.ktor.server.plugins.*
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDateTime

fun getDateParameter(
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
