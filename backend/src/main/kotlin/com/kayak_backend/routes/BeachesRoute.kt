package com.kayak_backend.routes
import com.kayak_backend.services.slipways.BeachesService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.beaches(beach: BeachesService = BeachesService()) {
    route("/beaches") {
        get {
            call.respond(beach.getAllBeaches())
        }
    }
}
