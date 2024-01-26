package com.kayak_backend.routes
import com.kayak_backend.models.Location
import com.kayak_backend.services.slipways.SlipwayService
import com.kayak_backend.services.sunset.SunsetService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate

fun Route.slipway(slipway: SlipwayService = SlipwayService()) {

    route("/slipway") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat");
            val lng = call.parameters.getOrFail<Double>("lng");
            val location = Location(lat, lng);
            call.respond(slipway.closestSlipway(location));
        }
    }
}