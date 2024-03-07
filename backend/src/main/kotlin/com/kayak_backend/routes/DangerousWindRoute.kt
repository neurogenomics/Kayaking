package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.windFiltering.DangerousWindService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.dangerousWind(dangerousWind: DangerousWindService) {
    route("/dangerouswind") {
        get {
            val locations = call.parameters.getOrFail<List<Location>>("locations")
            val checkpoints = call.parameters.getOrFail<List<Long>>("checkpoints")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val coastBuffer = call.parameters["routeBuffer"] ?: 500.0
            call.respond(dangerousWind.findBadWinds(locations, checkpoints, startTime, coastBuffer))
        }
    }
}
