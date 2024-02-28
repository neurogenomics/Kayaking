package com.kayak_backend.plugins

import com.kayak_backend.Conf
import com.kayak_backend.getTideService
import com.kayak_backend.getTideTimeService
import com.kayak_backend.getWindService
import com.kayak_backend.routes.beaches
import com.kayak_backend.routes.slipway
import com.kayak_backend.routes.sunset
import com.kayak_backend.routes.testRouting
import com.kayak_backend.routes.tide
import com.kayak_backend.routes.tideTimes
import com.kayak_backend.routes.wind
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
    }
}
