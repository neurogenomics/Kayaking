package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.tideTimes.TideTimeService
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.tides.TideService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDateTime

fun Route.tidetimes(tidetimes: TideTimeService = TideTimeService()) {
    // TODO: Get getOrFail to serialize so this can be done implicitly

    route("/tidetimes") {
        get {
//            val lat = call.parameters.getOrFail<Double>("lat");
//            val lon = call.parameters.getOrFail<Double>("lon");
//            val dateTime = getDateParameter(call.parameters, "datetime");
//            val location = Location(lat, lon);
            call.respond(tidetimes.getTideTimes());
        }
    }
}