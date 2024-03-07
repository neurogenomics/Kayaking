package com.kayak_backend.plugins

import com.kayak_backend.*
import com.kayak_backend.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(conf: Conf) {
    routing {
        testRouting()
        sunset()
        beaches()
        tideTimes(getTideTimeService(conf, System.getenv()))
        tide(getTideService(conf))
        wind(getWindService(conf))
        times(getTimeService(conf))
        wave(getWaveService(conf))
        planRoute(getRoutePlanner(), getLegTimer(), getLegDifficulty())
    }
}
