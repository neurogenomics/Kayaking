package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.tideTimes(tideTimes: AdmiraltyTideTimeService = AdmiraltyTideTimeService()) {
    route("/tidetimes") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat");
            val lon = call.parameters.getOrFail<Double>("lon");
            val location = Location(lat, lon);
            call.respond(tideTimes.getTideTimes(location));
        }
    }
}