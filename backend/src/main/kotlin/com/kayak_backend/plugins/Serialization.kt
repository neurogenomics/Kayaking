package com.kayak_backend.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    routing {
    }
}
