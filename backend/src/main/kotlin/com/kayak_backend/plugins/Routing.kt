package com.kayak_backend.plugins

import com.kayak_backend.routes.sunset
import com.kayak_backend.routes.testRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        testRouting()
        sunset()
    }
}
