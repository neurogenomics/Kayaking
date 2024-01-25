package com.kayak_backend.routes
import com.kayak_backend.models.Location
import com.kayak_backend.services.sunset.Sunset
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
fun Route.sunset() {
    val sunset = Sunset();

    route("/sunset") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat");
            val lng = call.parameters.getOrFail<Double>("lng");
            val location = Location(lat, lng);
            call.respond(sunset.getSunset(location));
        }
    }
}