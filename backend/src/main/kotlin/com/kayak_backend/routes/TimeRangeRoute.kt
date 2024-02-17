package com.kayak_backend.routes

import com.kayak_backend.services.times.TimeService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.times(timeService: TimeService) {
    route("/timeRange") {
        get {
            call.respond(timeService.getTimes().map { it.toString() })
        }
    }
}
