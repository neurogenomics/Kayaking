package com.kayak_backend.plugins

import com.kayak_backend.routes.sunset
import com.kayak_backend.routes.testRouting
import com.kayak_backend.routes.tide
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        testRouting()
        sunset()
        tide()
    }
}
