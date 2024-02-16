package com.kayak_backend.plugins

import com.kayak_backend.Conf
import com.kayak_backend.getTideService
import com.kayak_backend.getTideTimeService
import com.kayak_backend.getWindService
import com.kayak_backend.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(conf: Conf) {
    routing {
        testRouting()
        sunset()
        slipway()
        beaches()
        tideTimes(getTideTimeService(conf, System.getenv()))
        tide(getTideService(conf))
        wind(getWindService(conf))
        planRoute()
    }
}
