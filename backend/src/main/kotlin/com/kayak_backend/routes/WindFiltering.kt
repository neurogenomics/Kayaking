package com.kayak_backend.routes
import com.kayak_backend.services.windFiltering.WindFiltering
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.windFiltering(windFiltering: WindFiltering = WindFiltering()) {
    route("/windFiltering") {
        get {
            call.respond(windFiltering.classifyAreas().filter { it.bad }.map { it.bearing.coor })
        }
    }
}
