package com.kayak_backend.plugins

import com.kayak_backend.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        testRouting()
        sunset()
        slipway()
        tide()
        tidetimes()
    }
}
