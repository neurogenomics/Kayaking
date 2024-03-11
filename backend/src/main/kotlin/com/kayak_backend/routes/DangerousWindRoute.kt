package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.serialization.LocalDateTimeSerializer
import com.kayak_backend.services.dangerousWindWarning.DangerousWindService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@Serializable
data class WindPoints(
    val locations: List<Location>,
    val checkpoints: List<Long>,
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime,
)

fun Route.dangerousWind(dangerousWind: DangerousWindService) {
    route("/dangerouswind") {
        post {
            val requestBody = call.receiveText()
            val data = Json.decodeFromString<WindPoints>(requestBody)
            call.respond(dangerousWind.findBadWinds(data.locations, data.checkpoints, data.date))
        }
    }
}
